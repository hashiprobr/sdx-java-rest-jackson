package br.pro.hashi.sdx.rest.jackson.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;
import br.pro.hashi.sdx.rest.jackson.JacksonInjector;
import br.pro.hashi.sdx.rest.server.RestServer;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;

/**
 * Convenience class to configure and build objects of type {@link RestServer}
 * with Jackson support.
 */
public class JacksonRestServerBuilder extends RestServerBuilder {
	/**
	 * <p>
	 * Constructs a server builder with a default Jackson serializer and a default
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default
	 * configuration. Namely, with the options below.
	 * </p>
	 * 
	 * <pre>
	 * {@code   .disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)
	 *   .disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET)
	 *   .enable(SerializationFeature.INDENT_OUTPUT)}
	 * </pre>
	 */
	public JacksonRestServerBuilder() {
		new JacksonInjector().inject(this);
		addExtension();
	}

	/**
	 * <p>
	 * Constructs a server builder with an extended default Jackson serializer and
	 * an extended default Jackson deserializer.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default configuration
	 * (see no-args constructor) and extends its type support with instances of all
	 * concrete implementations of {@link JacksonConverter} in a specified package.
	 * </p>
	 * 
	 * @param packageName the package name
	 */
	public JacksonRestServerBuilder(String packageName) {
		new JacksonInjector().inject(this, packageName);
		addExtension();
	}

	/**
	 * <p>
	 * Constructs a server builder with an extended custom Jackson serializer and an
	 * extended custom Jackson deserializer.
	 * </p>
	 * <p>
	 * This method uses a specified {@link ObjectMapper} and extends its type
	 * support with instances of all concrete implementations of
	 * {@link JacksonConverter} in a specified package.
	 * </p>
	 * 
	 * @param mapper      the Jackson mapper
	 * @param packageName the package name
	 */
	public JacksonRestServerBuilder(ObjectMapper mapper, String packageName) {
		new JacksonInjector().inject(this, mapper, packageName);
		addExtension();
	}

	/**
	 * <p>
	 * Constructs a server builder with a custom Jackson serializer and a custom
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * This method uses a specified {@link ObjectMapper}.
	 * </p>
	 * 
	 * @param mapper the Jackson mapper
	 */
	public JacksonRestServerBuilder(ObjectMapper mapper) {
		new JacksonInjector().inject(this, mapper);
		addExtension();
	}

	private void addExtension() {
		this.withExtension("json", "application/json");
	}
}
