package br.pro.hashi.sdx.rest.jackson.server;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import br.pro.hashi.sdx.rest.server.RestServer;

class JacksonRestServerTest {
	private RestServer server;

	@BeforeEach
	void setUp() {
		server = mock(RestServer.class);
	}

	@Test
	void builds() {
		try (MockedConstruction<JacksonRestServerBuilder> construction = mockBuilderConstruction()) {
			assertSame(server, JacksonRestServer.from("package"));
		}
	}

	private MockedConstruction<JacksonRestServerBuilder> mockBuilderConstruction() {
		return mockConstruction(JacksonRestServerBuilder.class, (mock, context) -> {
			when(mock.build(any())).thenReturn(server);
		});
	}
}
