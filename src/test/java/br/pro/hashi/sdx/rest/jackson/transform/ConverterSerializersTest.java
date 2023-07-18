package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

class ConverterSerializersTest {
	private AutoCloseable mocks;
	private @Mock ConverterFactory converterFactory;
	private ConverterSerializers s;
	private @Mock SerializationConfig config;
	private @Mock BeanDescription description;
	private @Mock TypeSerializer typeSerializer;
	private @Mock JsonSerializer<Object> jsonSerializer;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

		s = new ConverterSerializers(converterFactory);
	}

	@AfterEach
	void tearDown() {
		assertDoesNotThrow(() -> {
			mocks.close();
		});
	}

	@Test
	void findsReferenceSerializer() {
		ReferenceType javaType = mock(ReferenceType.class);
		assertNull(s.findReferenceSerializer(config, javaType, description, typeSerializer, jsonSerializer));
	}

	@Test
	void findsArraySerializer() {
		ArrayType javaType = mock(ArrayType.class);
		assertNull(s.findArraySerializer(config, javaType, description, typeSerializer, jsonSerializer));
	}

	@Test
	void findsCollectionLikeSerializer() {
		CollectionLikeType javaType = mock(CollectionLikeType.class);
		assertNull(s.findCollectionLikeSerializer(config, javaType, description, typeSerializer, jsonSerializer));
	}

	@Test
	void findsMapSerializer() {
		MapType javaType = mock(MapType.class);
		assertNull(s.findMapSerializer(config, javaType, description, jsonSerializer, typeSerializer, jsonSerializer));
	}

	@Test
	void findsMapLikeSerializer() {
		MapLikeType javaType = mock(MapLikeType.class);
		assertNull(s.findMapLikeSerializer(config, javaType, description, jsonSerializer, typeSerializer, jsonSerializer));
	}
}
