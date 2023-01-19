package br.pro.hashi.sdx.rest.jackson.client;

import br.pro.hashi.sdx.rest.client.RestClient;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;

/**
 * Convenience class to quickly build a REST client with Jackson support.
 */
public final class JacksonRestClient {
	/**
	 * Instantiates a default REST client using a specified URL prefix.
	 * 
	 * @param urlPrefix the URL prefix
	 * @return the client
	 */
	public static RestClient to(String urlPrefix) {
		return builder().build(urlPrefix);
	}

	/**
	 * Convenience method that instantiates a REST client builder.
	 * 
	 * @return the client builder
	 */
	public static RestClientBuilder builder() {
		return new JacksonRestClientBuilder();
	}

	private JacksonRestClient() {
	}
}
