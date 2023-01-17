package br.pro.hashi.sdx.rest.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import br.pro.hashi.sdx.rest.transform.extension.Converter;

/**
 * <p>
 * A Jackson converter can convert objects of a source type to and from objects
 * of a target type.
 * </p>
 * <p>
 * The idea is that the source type is not supported by Jackson, but the target
 * type is (possibly via another converter).
 * </p>
 * 
 * @param <S> the source type
 * @param <T> the target type
 */
public interface JacksonConverter<S, T> extends Converter<S, T> {
	/**
	 * <p>
	 * Obtains a {@link JsonSerializer<S>} based on this converter.
	 * </p>
	 * <p>
	 * Classes are encouraged to provide an alternative implementation.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @return the Jackson serializer
	 */
	default JsonSerializer<S> getJacksonSerializer(JavaType targetType) {
		return new JsonSerializer<>() {
			@Override
			public void serialize(S value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				gen.writeObject(to(value));
			}
		};
	};

	/**
	 * <p>
	 * Obtains a {@link JsonDeserializer<S>} based on this converter.
	 * </p>
	 * <p>
	 * Classes are encouraged to provide an alternative implementation.
	 * </p>
	 * 
	 * @param targetType the target type
	 * @return the Jackson deserializer
	 */
	default JsonDeserializer<S> getJacksonDeserializer(JavaType targetType) {
		return new JsonDeserializer<>() {
			@Override
			public S deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
				return from(ctxt.readValue(p, targetType));
			}
		};
	}
}
