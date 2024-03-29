package br.pro.hashi.sdx.rest.jackson.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.JacksonInjector;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;

/**
 * Builds REST servers with Jackson support.
 */
public class JacksonRestServerBuilder extends RestServerBuilder {
	/**
	 * <p>
	 * Constructs a new builder with a default Jackson serializer and a default
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * See {@link JacksonInjector#inject(br.pro.hashi.sdx.rest.Builder)}.
	 * </p>
	 */
	public JacksonRestServerBuilder() {
		JacksonInjector.getInstance().inject(this);
		configure();
	}

	/**
	 * <p>
	 * Constructs a new builder with a custom Jackson serializer and a custom
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * See
	 * {@link JacksonInjector#inject(br.pro.hashi.sdx.rest.Builder, ObjectMapper)}.
	 * </p>
	 *
	 * @param objectMapper the object mapper
	 * @throws NullPointerException if the object mapper is null
	 */
	public JacksonRestServerBuilder(ObjectMapper objectMapper) {
		JacksonInjector.getInstance().inject(this, objectMapper);
		configure();
	}

	/**
	 * <p>
	 * Constructs a new builder with an extended default Jackson serializer and an
	 * extended default Jackson deserializer.
	 * </p>
	 * <p>
	 * See {@link JacksonInjector#inject(br.pro.hashi.sdx.rest.Builder, String)}.
	 * </p>
	 *
	 * @param packageName the package name
	 * @throws NullPointerException if the package name is null
	 */
	public JacksonRestServerBuilder(String packageName) {
		JacksonInjector.getInstance().inject(this, packageName);
		configure();
	}

	/**
	 * <p>
	 * Constructs a new builder with an extended custom Jackson serializer and an
	 * extended custom Jackson deserializer.
	 * </p>
	 * <p>
	 * See
	 * {@link JacksonInjector#inject(br.pro.hashi.sdx.rest.Builder, ObjectMapper, String)}.
	 * </p>
	 *
	 * @param objectMapper the object mapper
	 * @param packageName  the package name
	 * @throws NullPointerException if the object mapper is null or package name is
	 *                              null
	 */
	public JacksonRestServerBuilder(ObjectMapper objectMapper, String packageName) {
		JacksonInjector.getInstance().inject(this, objectMapper, packageName);
		configure();
	}

	private void configure() {
		withExtensionType("json", JacksonInjector.JSON_TYPE);
		withFallbackType(JacksonInjector.JSON_TYPE);
	}
}
