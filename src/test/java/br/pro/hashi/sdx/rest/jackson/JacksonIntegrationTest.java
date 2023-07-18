package br.pro.hashi.sdx.rest.jackson;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.pro.hashi.sdx.rest.Builder;
import br.pro.hashi.sdx.rest.Hint;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;
import br.pro.hashi.sdx.rest.jackson.mock.Address;
import br.pro.hashi.sdx.rest.jackson.mock.Email;
import br.pro.hashi.sdx.rest.jackson.mock.ObjectWithDouble;
import br.pro.hashi.sdx.rest.jackson.mock.ObjectWithString;
import br.pro.hashi.sdx.rest.jackson.mock.ObjectWithTransient;
import br.pro.hashi.sdx.rest.jackson.mock.ObjectWithoutTransient;
import br.pro.hashi.sdx.rest.jackson.mock.Sheet;
import br.pro.hashi.sdx.rest.jackson.mock.User;
import br.pro.hashi.sdx.rest.jackson.mock.Wrapper;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;
import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

class JacksonIntegrationTest {
	private Builder<?> builder;
	private Map<String, Serializer> serializers;
	private Map<String, Deserializer> deserializers;
	private JacksonInjector injector;

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesUserWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertSerializesUser("""
				{
				  "name" : "Serializing Name",
				  "address" : {
				    "street" : "Serializing Street",
				    "number" : 0,
				    "city" : "Serializing City"
				  },
				  "email" : {
				    "login" : "serializing",
				    "domain" : "email.com"
				  },
				  "active" : true
				}
				""");
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesUserWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesUser("""
				{
				  "name" : "Serializing Name",
				  "address" : [ "Serializing City", "0", "Serializing Street" ],
				  "email" : "serializing@email.com",
				  "active" : true
				}
				""");
	}

	private void assertSerializesUser(String content) {
		Email email = new Email();
		email.setLogin("serializing");
		email.setDomain("email.com");
		Address address = new Address("Serializing Street", 0, "Serializing City");
		User user = new User();
		user.setName("Serializing Name");
		user.setAddress(address);
		user.setEmail(email);
		user.setActive(true);
		assertSerializes(content, user, User.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesUserWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertDeserializesUser("""
				{
				  "name": "Deserializing Name",
				  "address": {
				    "street": "Deserializing Street",
				    "number": 1,
				    "city": "Deserializing City"
				  },
				  "email": {
				  	"login": "deserializing",
				  	"domain": "email.com"
				  },
				  "active": false
				}
				""");
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesUserWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertDeserializesUser("""
				{
				  "name": "Deserializing Name",
				  "address": [
				    "Deserializing City",
				    "1",
				    "Deserializing Street"
				  ],
				  "email": "deserializing@email.com",
				  "active": false
				}
				""");
	}

	private void assertDeserializesUser(String content) {
		User user = deserialize(content, User.class);
		assertEquals("Deserializing Name", user.getName());
		Address address = user.getAddress();
		assertEquals("Deserializing Street", address.getStreet());
		assertEquals(1, address.getNumber());
		assertEquals("Deserializing City", address.getCity());
		Email email = user.getEmail();
		assertEquals("deserializing", email.getLogin());
		assertEquals("email.com", email.getDomain());
		assertFalse(user.isActive());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesSheetWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertSerializesSheet("""
				{
				  "rows" : [ [ "Street 0", "0", "City 0" ], [ "Street 1", "1", "City 1" ] ]
				}
				""");
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesSheetWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesSheet("""
				[ [ "City 0", "0", "Street 0" ], [ "City 1", "1", "Street 1" ] ]
				""");
	}

	private void assertSerializesSheet(String content) {
		Sheet sheet = new Sheet();
		sheet.addRow("Street 0", 0, "City 0");
		sheet.addRow("Street 1", 1, "City 1");
		assertSerializes(content, sheet, Sheet.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesSheetWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertDeserializesSheet("""
				{
				  "rows": [
				    [
				      "Street 1",
				      "1",
				      "City 1"
				    ],
				    [
				      "Street 0",
				      "0",
				      "City 0"
				    ]
				  ]
				}
				""");
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesSheetWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertDeserializesSheet("""
				[
				  [
				    "City 1",
				    "1",
				    "Street 1"
				  ],
				  [
				    "City 0",
				    "0",
				    "Street 0"
				  ]
				]
				""");
	}

	private void assertDeserializesSheet(String content) {
		Sheet sheet = deserialize(content, Sheet.class);
		assertEquals(List.of("Street 1", "1", "City 1"), sheet.getRow(0));
		assertEquals(List.of("Street 0", "0", "City 0"), sheet.getRow(1));
		assertThrows(IndexOutOfBoundsException.class, () -> {
			sheet.getRow(2);
		});
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesBooleanWrappersWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertSerializesBooleanWrappers("""
				[ {
				  "value" : false
				}, {
				  "value" : true
				} ]
				""", List.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesBooleanWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesBooleanWrappers("""
				[ "false", "true" ]
				""", new Hint<List<Wrapper<Boolean>>>() {}.getType());
	}

	private void assertSerializesBooleanWrappers(String content, Type type) {
		List<Wrapper<Boolean>> wrappers = new ArrayList<>();
		wrappers.add(new Wrapper<>(false));
		wrappers.add(new Wrapper<>(true));
		assertSerializes(content, wrappers, type);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesBooleanWrappersWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertDeserializesBooleanWrappers("""
				[
				  {
				    "value": true
				  },
				  {
				    "value": false
				  }
				]
				""", new Hint<List<Wrapper<Boolean>>>() {}.getType());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesBooleanWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertDeserializesBooleanWrappers("""
				[
				  "true",
				  "false"
				]
				""", new Hint<List<Wrapper<Boolean>>>() {}.getType());
	}

	private void assertDeserializesBooleanWrappers(String content, Type type) {
		List<Wrapper<Boolean>> wrappers = deserialize(content, type);
		assertEquals(2, wrappers.size());
		assertTrue(wrappers.get(0).getValue());
		assertFalse(wrappers.get(1).getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesByteWrappersWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertSerializesByteWrappers("""
				[ {
				  "value" : 63
				}, {
				  "value" : 127
				} ]
				""", List.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesByteWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesByteWrappers("""
				[ [ "6", "3" ], [ "1", "2", "7" ] ]
				""", new Hint<List<Wrapper<Byte>>>() {}.getType());
	}

	private void assertSerializesByteWrappers(String content, Type type) {
		List<Wrapper<Byte>> wrappers = new ArrayList<>();
		wrappers.add(new Wrapper<>((byte) 63));
		wrappers.add(new Wrapper<>((byte) 127));
		assertSerializes(content, wrappers, type);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesByteWrappersWithoutConverters(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		assertDeserializesByteWrappers("""
				[
				  {
				    "value": 127
				  },
				  {
				    "value": 63
				  }
				]
				""", new Hint<List<Wrapper<Byte>>>() {}.getType());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesByteWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertDeserializesByteWrappers("""
				[
				  [
				    "1",
				    "2",
				    "7"
				  ],
				  [
				    "6",
				    "3"
				  ]
				]
				""", new Hint<List<Wrapper<Byte>>>() {}.getType());
	}

	private void assertDeserializesByteWrappers(String content, Type type) {
		List<Wrapper<Byte>> wrappers = deserialize(content, type);
		assertEquals(2, wrappers.size());
		assertEquals(127, (byte) wrappers.get(0).getValue());
		assertEquals(63, (byte) wrappers.get(1).getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithoutTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithoutTransient object = new ObjectWithoutTransient(true);
		assertSerializes("""
				{
				  "value" : true
				}""", object, ObjectWithoutTransient.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithoutTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithoutTransient object = deserialize("""
				{
				  "value": true
				}""", ObjectWithoutTransient.class);
		assertTrue(object.isValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithTransient object = new ObjectWithTransient(true);
		assertSerializes("""
				{ }""", object, ObjectWithTransient.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithTransient object = deserialize("""
				{
				  "value": true
				}""", ObjectWithTransient.class);
		assertFalse(object.isValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithNaN(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = new ObjectWithDouble(Double.NaN);
		assertSerializes("""
				{
				  "value" : NaN
				}""", object, ObjectWithDouble.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithNaN(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = deserialize("""
				{
				  "value": NaN
				}""", ObjectWithDouble.class);
		assertTrue(Double.isNaN(object.getValue()));
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithNegativeInfinity(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = new ObjectWithDouble(Double.NEGATIVE_INFINITY);
		assertSerializes("""
				{
				  "value" : -Infinity
				}""", object, ObjectWithDouble.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithNegativeInfinity(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = deserialize("""
				{
				  "value": -Infinity
				}""", ObjectWithDouble.class);
		double value = object.getValue();
		assertTrue(value < 0);
		assertTrue(Double.isInfinite(value));
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithPositiveInfinity(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = new ObjectWithDouble(Double.POSITIVE_INFINITY);
		assertSerializes("""
				{
				  "value" : Infinity
				}""", object, ObjectWithDouble.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithPositiveInfinity(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithDouble object = deserialize("""
				{
				  "value": Infinity
				}""", ObjectWithDouble.class);
		double value = object.getValue();
		assertTrue(value > 0);
		assertTrue(Double.isInfinite(value));
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithHtml(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithString object = new ObjectWithString("<div></div>");
		assertSerializes("""
				{
				  "value" : "<div></div>"
				}""", object, ObjectWithString.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithHtml(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithString object = deserialize("""
				{
				  "value": "<div></div>"
				}""", ObjectWithString.class);
		assertEquals("<div></div>", object.getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithNull(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithString object = new ObjectWithString(null);
		assertSerializes("""
				{
				  "value" : null
				}""", object, ObjectWithString.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithNull(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithString object = deserialize("""
				{
				  "value": null
				}""", ObjectWithString.class);
		assertNull(object.getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesNode(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
		arrayNode.add("s");
		arrayNode.add(6.6);
		arrayNode.add(3);
		arrayNode.add(false);
		arrayNode.add(true);
		arrayNode.addNull();
		ObjectNode objectNode = new ObjectNode(JsonNodeFactory.instance);
		objectNode.set("value", arrayNode);
		assertSerializes("""
				{
				  "value" : [ "s", 6.6, 3, false, true, null ]
				}""", objectNode, JsonNode.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesParser(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectMapper objectMapper = new ObjectMapper();
		JsonParser parser = assertDoesNotThrow(() -> {
			return objectMapper.getFactory().createParser("""
					{
					  "value": [
					    "s",
					    6.6,
					    3,
					    false,
					    true,
					    null
					  ]
					}""");
		});
		assertSerializes("""
				{
				  "value" : [ "s", 6.6, 3, false, true, null ]
				}""", parser, JsonParser.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void doesNotSerializeParser(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		Serializer serializer = serializers.get(JacksonInjector.JSON_TYPE);
		JsonParser parser = mock(JsonParser.class);
		assertDoesNotThrow(() -> {
			when(parser.nextToken()).thenReturn(JsonToken.NOT_AVAILABLE);
		});
		Writer writer = Writer.nullWriter();
		assertThrows(SerializingException.class, () -> {
			serializer.write(parser, JsonParser.class, writer);
		});
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesConsumer(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		Consumer<JsonGenerator> consumer = (generator) -> {
			assertDoesNotThrow(() -> {
				generator.writeStartObject();
				generator.writeFieldName("value");
				generator.writeStartArray();
				generator.writeString("s");
				generator.writeNumber(6.6);
				generator.writeNumber(3);
				generator.writeBoolean(false);
				generator.writeBoolean(true);
				generator.writeNull();
				generator.writeEndArray();
				generator.writeEndObject();
				generator.flush();
			});
		};
		assertSerializes("""
				{
				  "value" : [ "s", 6.6, 3, false, true, null ]
				}""", consumer, new Hint<Consumer<JsonGenerator>>() {}.getType());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesNode(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		JsonNode node = deserialize("""
				{
				  "value": [
				    "s",
				    6.6,
				    3,
				    false,
				    true,
				    null
				  ]
				}""", JsonNode.class);
		List<String> names = new ArrayList<>();
		node.fieldNames().forEachRemaining(names::add);
		assertEquals(List.of("value"), names);
		List<JsonNode> nodes = new ArrayList<>();
		node.get("value").iterator().forEachRemaining(nodes::add);
		assertEquals(6, nodes.size());
		assertEquals("s", nodes.get(0).asText());
		assertEquals(6.6, nodes.get(1).asDouble());
		assertEquals(3, nodes.get(2).asInt());
		assertFalse(nodes.get(3).asBoolean());
		assertTrue(nodes.get(4).asBoolean());
		assertTrue(nodes.get(5).isNull());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesParser(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		JsonParser parser = deserialize("""
				{
				  "value": [
				    "s",
				    6.6,
				    3,
				    false,
				    true,
				    null
				  ]
				}""", JsonParser.class);
		assertDoesNotThrow(() -> {
			assertEquals(JsonToken.START_OBJECT, parser.nextToken());
			assertEquals("value", parser.nextFieldName());
			assertEquals(JsonToken.START_ARRAY, parser.nextToken());
			assertEquals("s", parser.nextTextValue());
			assertEquals(JsonToken.VALUE_NUMBER_FLOAT, parser.nextToken());
			assertEquals(6.6, parser.getDoubleValue());
			assertEquals(JsonToken.VALUE_NUMBER_INT, parser.nextToken());
			assertEquals(3, parser.getIntValue());
			assertEquals(JsonToken.VALUE_FALSE, parser.nextToken());
			assertEquals(JsonToken.VALUE_TRUE, parser.nextToken());
			assertEquals(JsonToken.VALUE_NULL, parser.nextToken());
			assertEquals(JsonToken.END_ARRAY, parser.nextToken());
			assertEquals(JsonToken.END_OBJECT, parser.nextToken());
			parser.close();
		});
	}

	private <T extends Builder<T>> void setUp(Class<T> type) {
		builder = mock(type);
		serializers = new HashMap<>();
		deserializers = new HashMap<>();
		when(builder.withSerializer(any(String.class), any(Serializer.class))).thenAnswer((invocation) -> {
			String contentType = invocation.getArgument(0);
			Serializer serializer = invocation.getArgument(1);
			serializers.put(contentType, serializer);
			return null;
		});
		when(builder.withDeserializer(any(String.class), any(Deserializer.class))).thenAnswer((invocation) -> {
			String contentType = invocation.getArgument(0);
			Deserializer deserializer = invocation.getArgument(1);
			deserializers.put(contentType, deserializer);
			return null;
		});
		injector = JacksonInjector.getInstance();
	}

	private void injectWithoutConverters() {
		injector.inject(builder);
	}

	private void injectWithConverters() {
		injector.inject(builder, "br.pro.hashi.sdx.rest.jackson.mock");
	}

	private void assertSerializes(String content, Object object, Type type) {
		Serializer serializer = serializers.get(JacksonInjector.JSON_TYPE);
		StringWriter writer = new StringWriter();
		serializer.write(object, type, writer);
		assertDoesNotThrow(() -> {
			writer.close();
		});
		assertEquals(content.strip(), writer.toString());
	}

	private <T> T deserialize(String content, Type type) {
		Deserializer deserializer = deserializers.get(JacksonInjector.JSON_TYPE);
		Reader reader = new StringReader(content);
		return deserializer.read(reader, type);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	void doesNotInjectWithNullBuilder() {
		injector = JacksonInjector.getInstance();
		assertThrows(NullPointerException.class, () -> {
			injector.inject(null);
		});
	}
}
