package br.pro.hashi.sdx.rest.jackson.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.JacksonInjector;
import br.pro.hashi.sdx.rest.transform.Serializer;

class JacksonRestServerBuilderTest {
	private MockedConstruction<JacksonInjector> construction;
	private JacksonRestServerBuilder b;

	@BeforeEach
	void setUp() {
		construction = Mockito.mockConstruction(JacksonInjector.class, (mock, context) -> {
			doAnswer((invocation) -> {
				mockContentType(invocation);
				return null;
			}).when(mock).inject(any());
			doAnswer((invocation) -> {
				mockContentType(invocation);
				return null;
			}).when(mock).inject(any(), any(String.class));
			doAnswer((invocation) -> {
				mockContentType(invocation);
				return null;
			}).when(mock).inject(any(), any(ObjectMapper.class));
			doAnswer((invocation) -> {
				mockContentType(invocation);
				return null;
			}).when(mock).inject(any(), any(), any());
		});
	}

	private void mockContentType(InvocationOnMock invocation) {
		JacksonRestServerBuilder builder = invocation.getArgument(0);
		builder.withSerializer("application/json", mock(Serializer.class));
	}

	@AfterEach
	void tearDown() {
		construction.close();
	}

	@Test
	void constructsWithNoArgs() {
		b = new JacksonRestServerBuilder();
		verify(construction.constructed().get(0)).inject(b);
	}

	@Test
	void constructsWithPackageName() {
		b = new JacksonRestServerBuilder("package");
		verify(construction.constructed().get(0)).inject(b, "package");
	}

	@Test
	void constructsWithObjectMapperAndPackageName() {
		ObjectMapper mapper = mock(ObjectMapper.class);
		b = new JacksonRestServerBuilder(mapper, "package");
		verify(construction.constructed().get(0)).inject(b, mapper, "package");
	}

	@Test
	void constructsWithObjectMapper() {
		ObjectMapper mapper = mock(ObjectMapper.class);
		b = new JacksonRestServerBuilder(mapper);
		verify(construction.constructed().get(0)).inject(b, mapper);
	}
}
