package br.pro.hashi.sdx.rest.jackson.transform;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

class ConverterDeserializers implements Deserializers {
	private final ConverterFactory factory;
	private final Map<JavaType, JsonDeserializer<?>> deserializers;

	ConverterDeserializers(ConverterFactory factory) {
		this.factory = factory;
		this.deserializers = new HashMap<>();
	}

	void addDeserializer(JacksonConverter<?, ?> converter) {
		JavaType sourceType = factory.constructType(converter.getSourceType());
		JavaType targetType = factory.constructType(converter.getTargetType());
		deserializers.put(sourceType, converter.getJacksonDeserializer(targetType));
	}

	@Override
	public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc) {
		return deserializers.get(factory.constructType(type));
	}

	@Override
	public JsonDeserializer<?> findTreeNodeDeserializer(Class<? extends JsonNode> nodeType, DeserializationConfig config, BeanDescription beanDesc) {
		return deserializers.get(factory.constructType(nodeType));
	}

	@Override
	public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) {
		return deserializers.get(type);
	}

	@Override
	public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer) {
		return deserializers.get(refType);
	}

	@Override
	public JsonDeserializer<?> findArrayDeserializer(ArrayType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
		return deserializers.get(type);
	}

	@Override
	public JsonDeserializer<?> findCollectionDeserializer(CollectionType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
		return deserializers.get(type);
	}

	@Override
	public JsonDeserializer<?> findCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, BeanDescription beanDesc, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
		return deserializers.get(type);
	}

	@Override
	public JsonDeserializer<?> findMapDeserializer(MapType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
		return deserializers.get(type);
	}

	@Override
	public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type, DeserializationConfig config, BeanDescription beanDesc, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
		return deserializers.get(type);
	}
}
