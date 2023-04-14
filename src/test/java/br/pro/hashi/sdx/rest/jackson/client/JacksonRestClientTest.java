package br.pro.hashi.sdx.rest.jackson.client;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import br.pro.hashi.sdx.rest.client.RestClient;
import br.pro.hashi.sdx.rest.client.RestClientBuilder;

class JacksonRestClientTest {
	private RestClient client;

	@Test
	void builds() {
		try (MockedConstruction<JacksonRestClientBuilder> construction = mockBuilderConstruction()) {
			assertSame(client, JacksonRestClient.to("http://a"));
			RestClientBuilder builder = construction.constructed().get(0);
			verify(builder).build("http://a");
		}
	}

	private MockedConstruction<JacksonRestClientBuilder> mockBuilderConstruction() {
		client = mock(RestClient.class);
		return mockConstruction(JacksonRestClientBuilder.class, (mock, context) -> {
			when(mock.build("http://a")).thenReturn(client);
		});
	}
}
