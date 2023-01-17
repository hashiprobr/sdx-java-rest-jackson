package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

class ConverterSerializersTest {
	private JsonSerializer<?> expected;
	private JacksonConverter<?, ?> converter;
	private ConverterFactory factory;
	private ConverterSerializers s;
	private SerializationConfig config;
	private BeanDescription description;
	private TypeSerializer typeSerializer;
	private JsonSerializer<Object> objectSerializer;

	@BeforeEach
	void setUp() {
		expected = mock(JsonSerializer.class);
		converter = mock(JacksonConverter.class);
		doReturn(expected).when(converter).getJacksonSerializer(any());
		factory = mock(ConverterFactory.class);
		s = new ConverterSerializers(factory);
		config = mock(SerializationConfig.class);
		description = mock(BeanDescription.class);
		typeSerializer = mock(TypeSerializer.class);
		objectSerializer = new JsonSerializer<>() {
			@Override
			public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) {
			}
		};
	}

	@Test
	void findsSerializer() {
		JavaType javaType = mockType(JavaType.class);
		assertSame(expected, s.findSerializer(config, javaType, description));
	}

	@Test
	void findsReferenceSerializer() {
		ReferenceType javaType = mockType(ReferenceType.class);
		assertSame(expected, s.findReferenceSerializer(config, javaType, description, typeSerializer, objectSerializer));
	}

	@Test
	void findsArraySerializer() {
		ArrayType javaType = mockType(ArrayType.class);
		assertSame(expected, s.findArraySerializer(config, javaType, description, typeSerializer, objectSerializer));
	}

	@Test
	void findsCollectionSerializer() {
		CollectionType javaType = mockType(CollectionType.class);
		assertSame(expected, s.findCollectionSerializer(config, javaType, description, typeSerializer, objectSerializer));
	}

	@Test
	void findsCollectionLikeSerializer() {
		CollectionLikeType javaType = mockType(CollectionLikeType.class);
		assertSame(expected, s.findCollectionLikeSerializer(config, javaType, description, typeSerializer, objectSerializer));
	}

	@Test
	void findsMapSerializer() {
		MapType javaType = mockType(MapType.class);
		assertSame(expected, s.findMapSerializer(config, javaType, description, objectSerializer, typeSerializer, objectSerializer));
	}

	@Test
	void findsMapLikeSerializer() {
		MapLikeType javaType = mockType(MapLikeType.class);
		assertSame(expected, s.findMapLikeSerializer(config, javaType, description, objectSerializer, typeSerializer, objectSerializer));
	}

	private <T extends JavaType> T mockType(Class<T> type) {
		T javaType = mock(type);
		doReturn(javaType).when(factory).constructType(any());
		s.addSerializer(converter);
		return javaType;
	}
}
