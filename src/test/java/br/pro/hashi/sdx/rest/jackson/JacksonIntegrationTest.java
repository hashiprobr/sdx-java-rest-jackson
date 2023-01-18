package br.pro.hashi.sdx.rest.jackson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import br.pro.hashi.sdx.rest.Builder;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;
import br.pro.hashi.sdx.rest.jackson.mock.Address;
import br.pro.hashi.sdx.rest.jackson.mock.Email;
import br.pro.hashi.sdx.rest.jackson.mock.Sheet;
import br.pro.hashi.sdx.rest.jackson.mock.User;
import br.pro.hashi.sdx.rest.jackson.mock.Wrapper;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;
import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.Hint;
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
	<T extends Builder<T>> void serializesUserWithConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithConverters();
		assertReadsUser("""
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
	<T extends Builder<T>> void serializesUserWithoutConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithoutConverters();
		assertReadsUser("""
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

	private void assertReadsUser(String content) throws IOException {
		Email email = new Email();
		email.setLogin("serializing");
		email.setDomain("email.com");
		Address address = new Address("Serializing Street", 0, "Serializing City");
		User user = new User();
		user.setActive(true);
		user.setEmail(email);
		user.setAddress(address);
		user.setName("Serializing Name");
		assertReads(content, user, User.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesUserWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertWritesUser("""
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
		assertWritesUser("""
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

	private void assertWritesUser(String content) {
		User user = fromString(content, User.class);
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
	<T extends Builder<T>> void serializesSheetWithConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithConverters();
		assertReadsSheet("""
				[ [ "City 0", "0", "Street 0" ], [ "City 1", "1", "Street 1" ] ]
				""");
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesSheetWithoutConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithoutConverters();
		assertReadsSheet("""
				{
				  "rows" : [ [ "Street 0", "0", "City 0" ], [ "Street 1", "1", "City 1" ] ]
				}
				""");
	}

	private void assertReadsSheet(String content) throws IOException {
		Sheet sheet = new Sheet();
		sheet.addRow("Street 0", 0, "City 0");
		sheet.addRow("Street 1", 1, "City 1");
		assertReads(content, sheet, Sheet.class);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesSheetWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertWritesSheet("""
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
		assertWritesSheet("""
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

	private void assertWritesSheet(String content) {
		Sheet sheet = fromString(content, Sheet.class);
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
	<T extends Builder<T>> void serializesBooleanWrappersWithConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithConverters();
		assertReadsBooleanWrappers("""
				[ "false", "true" ]
				""", new Hint<List<Wrapper<Boolean>>>() {}.getType());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesBooleanWrappersWithoutConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithoutConverters();
		assertReadsBooleanWrappers("""
				[ {
				  "value" : false
				}, {
				  "value" : true
				} ]
				""", List.class);
	}

	private void assertReadsBooleanWrappers(String content, Type type) throws IOException {
		List<Wrapper<Boolean>> wrappers = new ArrayList<>();
		wrappers.add(new Wrapper<>(false));
		wrappers.add(new Wrapper<>(true));
		assertReads(content, wrappers, type);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesBooleanWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertWritesBooleanWrappers("""
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
		assertWritesBooleanWrappers("""
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

	private void assertWritesBooleanWrappers(String content, Type type) {
		List<Wrapper<Boolean>> wrappers = fromString(content, type);
		assertEquals(2, wrappers.size());
		assertTrue(wrappers.get(0).getValue());
		assertFalse(wrappers.get(1).getValue());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesByteWrappersWithConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithConverters();
		assertReadsByteWrappers("""
				[ [ "6", "3" ], [ "1", "2", "7" ] ]
				""", new Hint<List<Wrapper<Byte>>>() {}.getType());
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void serializesByteWrappersWithoutConverters(Class<T> type) throws IOException {
		setUp(type);
		injectWithoutConverters();
		assertReadsByteWrappers("""
				[ {
				  "value" : 63
				}, {
				  "value" : 127
				} ]
				""", List.class);
	}

	private void assertReadsByteWrappers(String content, Type type) throws IOException {
		List<Wrapper<Byte>> wrappers = new ArrayList<>();
		wrappers.add(new Wrapper<>((byte) 63));
		wrappers.add(new Wrapper<>((byte) 127));
		assertReads(content, wrappers, type);
	}

	@ParameterizedTest
	@ValueSource(classes = {
			RestClientBuilder.class,
			RestServerBuilder.class })
	<T extends Builder<T>> void deserializesByteWrappersWithConverters(Class<T> type) {
		setUp(type);
		injectWithConverters();
		assertWritesByteWrappers("""
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
		assertWritesByteWrappers("""
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

	private void assertWritesByteWrappers(String content, Type type) {
		List<Wrapper<Byte>> wrappers = fromString(content, type);
		assertEquals(2, wrappers.size());
		assertEquals((byte) 127, wrappers.get(0).getValue());
		assertEquals((byte) 63, wrappers.get(1).getValue());
	}

	private <T extends Builder<T>> void setUp(Class<T> type) {
		serializers = new HashMap<>();
		deserializers = new HashMap<>();
		builder = mock(type);
		when(builder.withSerializer(any(String.class), any(Serializer.class))).thenAnswer((invocation) -> {
			serializers.put(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		});
		when(builder.withDeserializer(any(String.class), any(Deserializer.class))).thenAnswer((invocation) -> {
			deserializers.put(invocation.getArgument(0), invocation.getArgument(1));
			return null;
		});
		injector = new JacksonInjector();
	}

	private void injectWithConverters() {
		injector.inject(builder, "br.pro.hashi.sdx.rest.jackson.mock");
	}

	private void injectWithoutConverters() {
		injector.inject(builder);
	}

	private void assertReads(String content, Object object, Type type) throws IOException {
		Reader reader = serializers.get("application/json").toReader(object, type);
		content = content.strip();
		int length;
		char[] chars = new char[content.length()];
		int offset = 0;
		int remaining = chars.length;
		while (remaining > 0 && (length = reader.read(chars, offset, remaining)) != -1) {
			offset += length;
			remaining -= length;
		}
		assertEquals(-1, reader.read());
		assertEquals(content, new String(chars));
		reader.close();
	}

	private <T> T fromString(String content, Type type) {
		Reader reader = new StringReader(content);
		return deserializers.get("application/json").fromReader(reader, type);
	}
}
