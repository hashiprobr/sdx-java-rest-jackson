package br.pro.hashi.sdx.rest.jackson.transform;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ArrayType;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.ReferenceType;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

class ConverterSerializers implements Serializers {
	private final ConverterFactory converterFactory;
	private final Map<JavaType, JsonSerializer<?>> map;

	ConverterSerializers(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
		this.map = new HashMap<>();
	}

	void addConverter(JacksonConverter<?, ?> converter) {
		JavaType sourceType = converterFactory.constructType(converter.getSourceType());
		JavaType targetType = converterFactory.constructType(converter.getTargetType());
		map.put(sourceType, converter.getJacksonSerializer(targetType));
	}

	@Override
	public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, ReferenceType type, BeanDescription beanDesc, TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findArraySerializer(SerializationConfig config, ArrayType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findCollectionSerializer(SerializationConfig config, CollectionType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type, BeanDescription beanDesc, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
		return map.get(type);
	}

	@Override
	public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer, TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
		return map.get(type);
	}
}
