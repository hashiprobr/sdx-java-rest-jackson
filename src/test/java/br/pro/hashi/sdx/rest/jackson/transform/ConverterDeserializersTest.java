package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

class ConverterDeserializersTest {
	private JsonDeserializer<?> expected;
	private JacksonConverter<?, ?> converter;
	private ConverterFactory factory;
	private ConverterDeserializers d;
	private DeserializationConfig config;
	private BeanDescription description;
	private KeyDeserializer keyDeserializer;
	private TypeDeserializer typeDeserializer;
	private JsonDeserializer<?> objectDeserializer;

	@BeforeEach
	void setUp() {
		expected = mock(JsonDeserializer.class);
		converter = mock(JacksonConverter.class);
		doReturn(expected).when(converter).getJacksonDeserializer(any());
		factory = mock(ConverterFactory.class);
		d = new ConverterDeserializers(factory);
		config = mock(DeserializationConfig.class);
		description = mock(BeanDescription.class);
		keyDeserializer = mock(KeyDeserializer.class);
		typeDeserializer = mock(TypeDeserializer.class);
		objectDeserializer = mock(JsonDeserializer.class);
	}

	@Test
	void findsEnumDeserializer() {
		mockType(JavaType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findEnumDeserializer(Object.class, config, description);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsTreeNodeDeserializer() {
		mockType(JavaType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findTreeNodeDeserializer(JsonNode.class, config, description);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsBeanDeserializer() {
		JavaType javaType = mockType(JavaType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findBeanDeserializer(javaType, config, description);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsReferenceDeserializer() {
		ReferenceType javaType = mockType(ReferenceType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findReferenceDeserializer(javaType, config, description, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsArrayDeserializer() {
		ArrayType javaType = mockType(ArrayType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findArrayDeserializer(javaType, config, description, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsCollectionDeserializer() {
		CollectionType javaType = mockType(CollectionType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findCollectionDeserializer(javaType, config, description, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsCollectionLikeDeserializer() {
		CollectionLikeType javaType = mockType(CollectionLikeType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findCollectionLikeDeserializer(javaType, config, description, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsMapDeserializer() {
		MapType javaType = mockType(MapType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findMapDeserializer(javaType, config, description, keyDeserializer, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	@Test
	void findsMapLikeDeserializer() {
		MapLikeType javaType = mockType(MapLikeType.class);
		JsonDeserializer<?> actual = assertDoesNotThrow(() -> {
			return d.findMapLikeDeserializer(javaType, config, description, keyDeserializer, typeDeserializer, objectDeserializer);
		});
		assertSame(expected, actual);
	}

	private <T extends JavaType> T mockType(Class<T> type) {
		T javaType = mock(type);
		doReturn(javaType).when(factory).constructType(any());
		d.addDeserializer(converter);
		return javaType;
	}
}
