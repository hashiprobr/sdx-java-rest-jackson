package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.exc.StreamWriteException;

import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

class JacksonSerializerTest {
	private AutoCloseable mocks;
	private @Mock ConverterMapper converterMapper;
	private JacksonSerializer s;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

		s = new JacksonSerializer(converterMapper);
	}

	@AfterEach
	void tearDown() {
		assertDoesNotThrow(() -> {
			mocks.close();
		});
	}

	@Test
	void doesNotWriteIfMapperThrowsWriteException() {
		Object body = new Object();
		Writer writer = Writer.nullWriter();
		Throwable cause = mock(StreamWriteException.class);
		assertDoesNotThrow(() -> {
			doThrow(cause).when(converterMapper).writeValue(writer, body, Object.class);
		});
		Exception exception = assertThrows(SerializingException.class, () -> {
			s.write(body, Object.class, writer);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void doesNotWriteIfMapperThrowsIOException() {
		Object body = new Object();
		Writer writer = Writer.nullWriter();
		assertDoesNotThrow(() -> {
			doThrow(IOException.class).when(converterMapper).writeValue(writer, body, Object.class);
		});
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.write(body, Object.class, writer);
		});
		assertInstanceOf(IOException.class, exception.getCause());
	}
}
