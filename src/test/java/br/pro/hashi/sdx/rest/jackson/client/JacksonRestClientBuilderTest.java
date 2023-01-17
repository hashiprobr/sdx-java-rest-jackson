package br.pro.hashi.sdx.rest.jackson.client;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.JacksonInjector;

class JacksonRestClientBuilderTest {
	private MockedConstruction<JacksonInjector> construction;
	private JacksonRestClientBuilder b;

	@BeforeEach
	void setUp() {
		construction = mockConstruction(JacksonInjector.class);
	}

	@AfterEach
	void tearDown() {
		construction.close();
	}

	@Test
	void constructsWithNoArgs() {
		b = new JacksonRestClientBuilder();
		verify(construction.constructed().get(0)).inject(b);
	}

	@Test
	void constructsWithPackageName() {
		b = new JacksonRestClientBuilder("package");
		verify(construction.constructed().get(0)).inject(b, "package");
	}

	@Test
	void constructsWithObjectMapperAndPackageName() {
		ObjectMapper mapper = mock(ObjectMapper.class);
		b = new JacksonRestClientBuilder(mapper, "package");
		verify(construction.constructed().get(0)).inject(b, mapper, "package");
	}

	@Test
	void constructsWithObjectMapper() {
		ObjectMapper mapper = mock(ObjectMapper.class);
		b = new JacksonRestClientBuilder(mapper);
		verify(construction.constructed().get(0)).inject(b, mapper);
	}
}
