package br.pro.hashi.sdx.rest.jackson.client;

import br.pro.hashi.sdx.rest.client.RestClient;

/**
 * Convenience class to quickly build a REST client with Jackson support.
 */
public final class JacksonRestClient {
	/**
	 * Instantiates a REST client using a specified URL prefix.
	 * 
	 * @param urlPrefix the URL prefix
	 * @return the REST client
	 */
	public static RestClient to(String urlPrefix) {
		return new JacksonRestClientBuilder().build(urlPrefix);
	}

	private JacksonRestClient() {
	}
}
