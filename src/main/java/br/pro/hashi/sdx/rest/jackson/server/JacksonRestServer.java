package br.pro.hashi.sdx.rest.jackson.server;

import br.pro.hashi.sdx.rest.server.RestServer;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;

/**
 * Convenience class to quickly build a REST server with Jackson support.
 */
public final class JacksonRestServer {
	/**
	 * Instantiates a REST server using the resources of a specified package.
	 * 
	 * @param packageName the package name
	 * @return the server
	 */
	public static RestServer from(String packageName) {
		return builder().build(packageName);
	}

	/**
	 * Convenience method for instantiating a REST server builder.
	 * 
	 * @return the server builder
	 */
	public static RestServerBuilder builder() {
		return new JacksonRestServerBuilder();
	}

	private JacksonRestServer() {
	}
}
