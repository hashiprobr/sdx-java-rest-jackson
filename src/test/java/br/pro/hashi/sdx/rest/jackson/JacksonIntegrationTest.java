package br.pro.hashi.sdx.rest.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
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

class JacksonIntegrationTest {
	private Map<String, Serializer> serializers;
	private Map<String, Deserializer> deserializers;
	private Builder<?> builder;
	private JacksonInjector injector;

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

	private void assertSerializesUser(String content) {
		Email email = new Email();
		email.setLogin("serializing");
		email.setDomain("email.com");
		Address address = new Address("Serializing Street", 0, "Serializing City");
		User user = new User();
		user.setActive(true);
		user.setEmail(email);
		user.setAddress(address);
		user.setName("Serializing Name");
		assertSerializes(content, user, User.class);
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
	<T extends Builder<T>> void serializesSheetWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesSheet("""
				[ [ "City 0", "0", "Street 0" ], [ "City 1", "1", "Street 1" ] ]
				""");
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

	private void assertDeserializesSheet(String content) {
		Sheet sheet = deserialize(content, Sheet.class);
		List<String> row = sheet.getRow(0);
		assertEquals("Street 1", row.get(0));
		assertEquals("1", row.get(1));
		assertEquals("City 1", row.get(2));
		row = sheet.getRow(1);
		assertEquals("Street 0", row.get(0));
		assertEquals("0", row.get(1));
		assertEquals("City 0", row.get(2));
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
	<T extends Builder<T>> void serializesByteWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertSerializesByteWrappers("""
				[ [ "6", "3" ], [ "1", "2", "7" ] ]
				""", new Hint<List<Wrapper<Byte>>>() {}.getType());
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

	private void assertDeserializesByteWrappers(String content, Type type) {
		List<Wrapper<Byte>> wrappers = deserialize(content, type);
		assertEquals(2, wrappers.size());
		assertEquals((byte) 127, wrappers.get(0).getValue());
		assertEquals((byte) 63, wrappers.get(1).getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithoutTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithoutTransient object = new ObjectWithoutTransient();
		assertSerializes("""
				{
				  "field" : true
				}""", object, ObjectWithoutTransient.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void deserializesWithoutTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithString object = deserialize("""
				{}""", ObjectWithString.class);
		assertNull(object.getField());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesWithTransient(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		ObjectWithTransient object = new ObjectWithTransient();
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
				  "field": true
				}""", ObjectWithTransient.class);
		assertFalse(object.getField());
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
				  "field" : NaN
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
				  "field": NaN
				}""", ObjectWithDouble.class);
		assertTrue(Double.isNaN(object.getField()));
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
				  "field" : -Infinity
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
				  "field": -Infinity
				}""", ObjectWithDouble.class);
		assertTrue(object.getField() < 0);
		assertTrue(Double.isInfinite(object.getField()));
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
				  "field" : Infinity
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
				  "field": Infinity
				}""", ObjectWithDouble.class);
		assertTrue(object.getField() > 0);
		assertTrue(Double.isInfinite(object.getField()));
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
				  "field" : "<div></div>"
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
				  "field": "<div></div>"
				}""", ObjectWithString.class);
		assertEquals("<div></div>", object.getField());
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
				  "field" : null
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
				  "field": null
				}""", ObjectWithString.class);
		assertNull(object.getField());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	public <T extends Builder<T>> void serializesConsumer(Class<T> type) {
		setUp(type);
		injectWithoutConverters();
		Consumer<JsonGenerator> consumer = (generator) -> {
			try {
				generator.writeStartObject();
				generator.writeFieldName("field");
				generator.writeStartArray();
				generator.writeBoolean(false);
				generator.writeBoolean(true);
				generator.writeEndArray();
				generator.writeEndObject();
				generator.flush();
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}
		};
		assertSerializes("""
				{"field":[false,true]}""", consumer, new Hint<Consumer<JsonGenerator>>() {}.getType());
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
				  "field": [
				    false,
				    true
				  ]
				}""", JsonParser.class);
		try {
			assertEquals(JsonToken.START_OBJECT, parser.nextToken());
			assertEquals("field", parser.nextFieldName());
			assertEquals(JsonToken.START_ARRAY, parser.nextToken());
			assertFalse(parser.nextBooleanValue());
			assertTrue(parser.nextBooleanValue());
			assertEquals(JsonToken.END_ARRAY, parser.nextToken());
			assertEquals(JsonToken.END_OBJECT, parser.nextToken());
			parser.close();
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
	}

	private <T extends Builder<T>> void setUp(Class<T> type) {
		serializers = new HashMap<>();
		deserializers = new HashMap<>();
		builder = mock(type);
		when(builder.withSerializer(eq("application/json"), any())).thenAnswer((invocation) -> {
			serializers.put(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		});
		when(builder.withDeserializer(eq("application/json"), any())).thenAnswer((invocation) -> {
			deserializers.put(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		});
		injector = JacksonInjector.getInstance();
	}

	private void injectWithConverters() {
		injector.inject(builder, "br.pro.hashi.sdx.rest.jackson.mock");
	}

	private void injectWithoutConverters() {
		injector.inject(builder);
	}

	private void assertSerializes(String content, Object object, Type type) {
		StringWriter writer = new StringWriter();
		serializers.get("application/json").write(object, type, writer);
		try {
			writer.close();
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
		assertEquals(content.strip(), writer.toString());
	}

	private <T> T deserialize(String content, Type type) {
		Reader reader = new StringReader(content);
		return deserializers.get("application/json").read(reader, type);
	}
}
