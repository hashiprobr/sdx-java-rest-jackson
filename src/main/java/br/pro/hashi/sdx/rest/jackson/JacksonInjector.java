package br.pro.hashi.sdx.rest.jackson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * A Jackson injector can inject a Jackson-based serializer and a Jackson-based
 * deserializer in an object of type {@link RestClientBuilder} or an object of
 * type {@link RestServerBuilder}.
 */
public class JacksonInjector extends Injector {
	private static final String JSON_TYPE = "application/json";

	private final Logger logger;

	/**
	 * Constructs a new Jackson injector.
	 */
	public JacksonInjector() {
		this.logger = LoggerFactory.getLogger(JacksonInjector.class);
	}

	/**
	 * <p>
	 * Injects a default serializer and a default deserializer in a client or server
	 * builder.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default
	 * configuration. Namely, with the options below.
	 * </p>
	 * 
	 * <pre>
	 * {@code   .enable(SerializationFeature.INDENT_OUTPUT)}
	 * </pre>
	 * 
	 * @param builder the client or server builder
	 */
	public void inject(Builder<?> builder) {
		inject(builder, newMapper());
	}

	/**
	 * <p>
	 * Injects an extended default serializer and an extended default deserializer
	 * in a client or server builder.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default configuration
	 * (see {@code inject(Builder<?>)}) and extends its type support with instances
	 * of all concrete implementations of {@link JacksonConverter} in a specified
	 * package.
	 * </p>
	 * 
	 * @param builder     the client or server builder
	 * @param packageName the package name
	 */
	public void inject(Builder<?> builder, String packageName) {
		inject(builder, newMapper(), packageName);
	}

	/**
	 * <p>
	 * Injects an extended custom serializer and an extended custom deserializer in
	 * a client or server builder.
	 * </p>
	 * <p>
	 * This method uses a specified {@link ObjectMapper} and extends its type
	 * support with instances of all concrete implementations of
	 * {@link JacksonConverter} in a specified package.
	 * </p>
	 * 
	 * @param builder     the client or server builder
	 * @param mapper      the Jackson mapper
	 * @param packageName the package name
	 */
	public void inject(Builder<?> builder, ObjectMapper mapper, String packageName) {
		ConverterFactory factory = new ConverterFactory(mapper);
		ConverterModule module = new ConverterModule(factory);
		for (JacksonConverter<?, ?> converter : getSubConverters(packageName, JacksonConverter.class)) {
			module.addConverter(converter);
			logger.info("Registered %s".formatted(converter.getClass().getName()));
		}
		mapper.registerModule(module);
		inject(builder, new ConverterMapper(factory, mapper));
	}

	/**
	 * <p>
	 * Injects a custom serializer and a custom deserializer in a client or server
	 * builder.
	 * </p>
	 * <p>
	 * This method uses a specified {@link ObjectMapper}.
	 * </p>
	 * 
	 * @param builder the client or server builder
	 * @param mapper  the Jackson mapper
	 */
	public void inject(Builder<?> builder, ObjectMapper mapper) {
		inject(builder, new ConverterMapper(new ConverterFactory(mapper), mapper));
	}

	private void inject(Builder<?> builder, ConverterMapper mapper) {
		builder.withSerializer(JSON_TYPE, new JacksonSerializer(mapper));
		builder.withDeserializer(JSON_TYPE, new JacksonDeserializer(mapper));
		builder.withFallbackTextType(JSON_TYPE);
	}

	private ObjectMapper newMapper() {
		return new ObjectMapper()
				.enable(SerializationFeature.INDENT_OUTPUT);
	}
}
