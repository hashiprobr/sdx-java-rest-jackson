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
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

class ConverterDeserializersTest {
	private AutoCloseable mocks;
	private @Mock ConverterFactory converterFactory;
	private ConverterDeserializers d;
	private @Mock DeserializationConfig config;
	private @Mock BeanDescription description;
	private @Mock TypeDeserializer typeDeserializer;
	private @Mock JsonDeserializer<Object> jsonDeserializer;
	private @Mock KeyDeserializer keyDeserializer;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

		d = new ConverterDeserializers(converterFactory);
	}

	@AfterEach
	void tearDown() {
		assertDoesNotThrow(() -> {
			mocks.close();
		});
	}

	@Test
	void findsEnumDeserializer() {
		assertNull(d.findEnumDeserializer(Object.class, config, description));
	}

	@Test
	void findsTreeNodeDeserializer() {
		assertNull(d.findTreeNodeDeserializer(JsonNode.class, config, description));
	}

	@Test
	void findsReferenceDeserializer() {
		ReferenceType javaType = mock(ReferenceType.class);
		assertNull(d.findReferenceDeserializer(javaType, config, description, typeDeserializer, jsonDeserializer));
	}

	@Test
	void findsArrayDeserializer() {
		ArrayType javaType = mock(ArrayType.class);
		assertNull(d.findArrayDeserializer(javaType, config, description, typeDeserializer, jsonDeserializer));
	}

	@Test
	void findsCollectionLikeDeserializer() {
		CollectionLikeType javaType = mock(CollectionLikeType.class);
		assertNull(d.findCollectionLikeDeserializer(javaType, config, description, typeDeserializer, jsonDeserializer));
	}

	@Test
	void findsMapDeserializer() {
		MapType javaType = mock(MapType.class);
		assertNull(d.findMapDeserializer(javaType, config, description, keyDeserializer, typeDeserializer, jsonDeserializer));
	}

	@Test
	void findsMapLikeDeserializer() {
		MapLikeType javaType = mock(MapLikeType.class);
		assertNull(d.findMapLikeDeserializer(javaType, config, description, keyDeserializer, typeDeserializer, jsonDeserializer));
	}
}
