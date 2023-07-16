package br.pro.hashi.sdx.rest.jackson.server;

import br.pro.hashi.sdx.rest.server.RestServer;
import br.pro.hashi.sdx.rest.server.RestServerBuilder;

/**
 * Convenience class that builds a default REST server with Jackson support.
 */
public final class JacksonRestServer {
	/**
	 * Builds a server with a default configuration from the resources in the
	 * specified package.
	 * 
	 * @param packageName the package name
	 * @return the server
	 */
	public static RestServer from(String packageName) {
		RestServerBuilder builder = new JacksonRestServerBuilder();
		return builder.build(packageName);
	}

	private JacksonRestServer() {
	}
}
