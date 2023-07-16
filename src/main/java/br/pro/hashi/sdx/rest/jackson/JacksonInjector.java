package br.pro.hashi.sdx.rest.jackson;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import br.pro.hashi.sdx.rest.Builder;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;
import br.pro.hashi.sdx.rest.jackson.transform.ConverterFactory;
import br.pro.hashi.sdx.rest.jackson.transform.ConverterMapper;
import br.pro.hashi.sdx.rest.jackson.transform.ConverterModule;
import br.pro.hashi.sdx.rest.jackson.transform.JacksonDeserializer;
import br.pro.hashi.sdx.rest.jackson.transform.JacksonSerializer;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;
import br.pro.hashi.sdx.rest.transform.extension.Injector;

/**
 * Injects a Jackson serializer and a Jackson deserializer in a
 * {@link RestClientBuilder} or a {@link RestServerBuilder}.
 */
public class JacksonInjector extends Injector {
	private static final JacksonInjector INSTANCE = new JacksonInjector();
	private static final Lookup LOOKUP = MethodHandles.lookup();

	/**
	 * Represents the JSON content type.
	 */
	public static final String JSON_TYPE = "application/json";

	/**
	 * Obtains the injector instance.
	 */
	public static JacksonInjector getInstance() {
		return INSTANCE;
	}

	private final Logger logger;

	private JacksonInjector() {
		this.logger = LoggerFactory.getLogger(JacksonInjector.class);
	}

	/**
	 * <p>
	 * Injects a default serializer and a default deserializer in the specified
	 * client or server builder.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default
	 * configuration. Namely, with the options below.
	 * </p>
	 * 
	 * <pre>
	 * {@code   .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
	 *   .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
	 *   .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
	 *   .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
	 *   .disable(JsonWriteFeature.WRITE_NAN_AS_STRINGS.mappedFeature())
	 *   .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature())
	 *   .enable(SerializationFeature.INDENT_OUTPUT)}
	 * </pre>
	 * 
	 * @param builder the client or server builder
	 */
	public final void inject(Builder<?> builder) {
		inject(builder, defaultObjectMapper());
	}

	/**
	 * <p>
	 * Injects a custom serializer and a custom deserializer in the specified client
	 * or server builder.
	 * </p>
	 * <p>
	 * This method uses the specified {@link ObjectMapper}.
	 * </p>
	 * 
	 * @param builder      the client or server builder
	 * @param objectMapper the object mapper
	 */
	public final void inject(Builder<?> builder, ObjectMapper objectMapper) {
		inject(builder, new ConverterMapper(new ConverterFactory(objectMapper), objectMapper));
	}

	/**
	 * <p>
	 * Injects an extended default serializer and an extended default deserializer
	 * in the specified client or server builder.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default configuration
	 * (see {@code inject(Builder<?>)}) and extends its type support with instances
	 * of all concrete implementations of {@link JacksonConverter} in the specified
	 * package (including subpackages).
	 * </p>
	 * 
	 * @param builder     the client or server builder
	 * @param packageName the package name
	 */
	public final void inject(Builder<?> builder, String packageName) {
		inject(builder, defaultObjectMapper(), packageName);
	}

	/**
	 * <p>
	 * Injects an extended custom serializer and an extended custom deserializer in
	 * the specified client or server builder.
	 * </p>
	 * <p>
	 * This method uses the specified {@link ObjectMapper} and extends its type
	 * support (see {@code inject(Builder<?>, String)}).
	 * </p>
	 * 
	 * @param builder     the client or server builder
	 * @param objectMapper      the object mapper
	 * @param packageName the package name
	 */
	public final void inject(Builder<?> builder, ObjectMapper objectMapper, String packageName) {
		ConverterFactory factory = new ConverterFactory(objectMapper);
		ConverterModule module = new ConverterModule(factory);
		for (JacksonConverter<?, ?> converter : getSubConverters(packageName, JacksonConverter.class, LOOKUP)) {
			module.addConverter(converter);
			logger.info("Registered %s".formatted(converter.getClass().getName()));
		}
		objectMapper.registerModule(module);
		inject(builder, new ConverterMapper(factory, objectMapper));
	}

	private void inject(Builder<?> builder, ConverterMapper converterMapper) {
		builder.withSerializer(JSON_TYPE, new JacksonSerializer(converterMapper));
		builder.withDeserializer(JSON_TYPE, new JacksonDeserializer(converterMapper));
	}

	private ObjectMapper defaultObjectMapper() {
		return new ObjectMapper()
				.setVisibility(PropertyAccessor.ALL, Visibility.NONE)
				.setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
				.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(JsonWriteFeature.WRITE_NAN_AS_STRINGS.mappedFeature())
				.enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS.mappedFeature())
				.enable(SerializationFeature.INDENT_OUTPUT);
	}
}
