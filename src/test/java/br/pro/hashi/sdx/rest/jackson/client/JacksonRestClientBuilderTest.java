package br.pro.hashi.sdx.rest.jackson.client;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.JacksonInjector;

class JacksonRestClientBuilderTest {
	private AutoCloseable mocks;
	private @Mock JacksonInjector injector;
	private MockedStatic<JacksonInjector> injectorStatic;
	private JacksonRestClientBuilder b;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

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
		b = new JacksonRestClientBuilder();
		verify(injector).inject(b);
	}

	@Test
	void constructsWithObjectMapper() {
		ObjectMapper objectMapper = mock(ObjectMapper.class);
		b = new JacksonRestClientBuilder(objectMapper);
		verify(injector).inject(b, objectMapper);
	}

	@Test
	void constructsWithPackageName() {
		String packageName = "package";
		b = new JacksonRestClientBuilder(packageName);
		verify(injector).inject(b, packageName);
	}

	@Test
	void constructsWithObjectMapperAndPackageName() {
		ObjectMapper objectMapper = mock(ObjectMapper.class);
		String packageName = "package";
		b = new JacksonRestClientBuilder(objectMapper, packageName);
		verify(injector).inject(b, objectMapper, packageName);
	}
}
