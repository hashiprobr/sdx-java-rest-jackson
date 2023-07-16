package br.pro.hashi.sdx.rest.jackson.server;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.Builder;
import br.pro.hashi.sdx.rest.jackson.JacksonInjector;
import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.Serializer;

class JacksonRestServerBuilderTest {
	private AutoCloseable mocks;
	private @Mock JacksonInjector injector;
	private Answer<JacksonInjector> answer;
	private MockedStatic<JacksonInjector> injectorStatic;
	private JacksonRestServerBuilder b;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

		answer = (invocation) -> {
			Builder<?> builder = invocation.getArgument(0);
			builder.withSerializer("application/json", mock(Serializer.class));
			builder.withDeserializer("application/json", mock(Deserializer.class));
			return null;
		};

		injectorStatic = mockStatic(JacksonInjector.class);
		injectorStatic.when(() -> JacksonInjector.getInstance()).thenReturn(injector);
	}

	@AfterEach
	void tearDown() {
		injectorStatic.close();
		assertDoesNotThrow(() -> {
			mocks.close();
		});
	}

	@Test
	void constructsWithNoArgs() {
		doAnswer(answer).when(injector).inject(any());
		b = new JacksonRestServerBuilder();
		verify(injector).inject(b);
	}

	@Test
	void constructsWithObjectMapper() {
		ObjectMapper objectMapper = mock(ObjectMapper.class);
		doAnswer(answer).when(injector).inject(any(), eq(objectMapper));
		b = new JacksonRestServerBuilder(objectMapper);
		verify(injector).inject(b, objectMapper);
	}

	@Test
	void constructsWithPackageName() {
		String packageName = "package";
		doAnswer(answer).when(injector).inject(any(), eq(packageName));
		b = new JacksonRestServerBuilder(packageName);
		verify(injector).inject(b, packageName);
	}

	@Test
	void constructsWithObjectMapperAndPackageName() {
		ObjectMapper objectMapper = mock(ObjectMapper.class);
		String packageName = "package";
		doAnswer(answer).when(injector).inject(any(), eq(objectMapper), eq(packageName));
		b = new JacksonRestServerBuilder(objectMapper, packageName);
		verify(injector).inject(b, objectMapper, packageName);
	}
}
