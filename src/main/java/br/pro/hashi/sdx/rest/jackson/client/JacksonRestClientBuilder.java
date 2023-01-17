package br.pro.hashi.sdx.rest.jackson.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.client.RestClient;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;
import br.pro.hashi.sdx.rest.jackson.JacksonConverter;
import br.pro.hashi.sdx.rest.jackson.JacksonInjector;

/**
 * Convenience class to configure and build objects of type {@link RestClient}
 * with Jackson support.
 */
public class JacksonRestClientBuilder extends RestClientBuilder {
	/**
	 * <p>
	 * Constructs a client builder with a default Jackson serializer and a default
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * This method instantiates an {@link ObjectMapper} with a default
	 * configuration. Namely, with the options below.
	 * </p>
	 * 
	 * <pre>
	 * {@code   .enable(SerializationFeature.INDENT_OUTPUT)}
	 * </pre>
	 */
	public JacksonRestClientBuilder() {
		new JacksonInjector().inject(this);
	}

	/**
	 * <p>
	 * Constructs a client builder with an extended default Jackson serializer and
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
	public JacksonRestClientBuilder(String packageName) {
		new JacksonInjector().inject(this, packageName);
	}

	/**
	 * <p>
	 * Constructs a client builder with an extended custom Jackson serializer and an
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
	public JacksonRestClientBuilder(ObjectMapper mapper, String packageName) {
		new JacksonInjector().inject(this, mapper, packageName);
	}

	/**
	 * <p>
	 * Constructs a client builder with a custom Jackson serializer and a custom
	 * Jackson deserializer.
	 * </p>
	 * <p>
	 * This method uses a specified {@link ObjectMapper}.
	 * </p>
	 * 
	 * @param mapper the Jackson mapper
	 */
	public JacksonRestClientBuilder(ObjectMapper mapper) {
		new JacksonInjector().inject(this, mapper);
	}
}
