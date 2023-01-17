package br.pro.hashi.sdx.rest.jackson.server;

import br.pro.hashi.sdx.rest.server.RestServer;

/**
 * Convenience class to quickly build a REST server with Jackson support.
 */
public final class JacksonRestServer {
	/**
	 * Instantiates a REST server using the resources of a specified package.
	 * 
	 * @param packageName the package name
	 * @return the REST server
	 */
	public static RestServer from(String packageName) {
		return new JacksonRestServerBuilder().build(packageName);
	}

	private JacksonRestServer() {
	}
}
