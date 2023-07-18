package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.exc.StreamReadException;

import br.pro.hashi.sdx.rest.transform.exception.DeserializingException;

class JacksonDeserializerTest {
	private AutoCloseable mocks;
	private @Mock ConverterMapper converterMapper;
	private JacksonDeserializer d;

	@BeforeEach
	void setUp() {
		mocks = MockitoAnnotations.openMocks(this);

		d = new JacksonDeserializer(converterMapper);
	}

	@AfterEach
	void tearDown() {
		assertDoesNotThrow(() -> {
			mocks.close();
		});
	}

	@Test
	void doesNotReadIfMapperThrowsReadException() {
		Reader reader = Reader.nullReader();
		Throwable cause = mock(StreamReadException.class);
		assertDoesNotThrow(() -> {
			when(converterMapper.readValue(reader, Object.class)).thenThrow(cause);
		});
		Exception exception = assertThrows(DeserializingException.class, () -> {
			d.read(reader, Object.class);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void doesNotReadIfMapperThrowsIOException() {
		Reader reader = Reader.nullReader();
		assertDoesNotThrow(() -> {
			when(converterMapper.readValue(reader, Object.class)).thenThrow(IOException.class);
		});
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			d.read(reader, Object.class);
		});
		assertInstanceOf(IOException.class, exception.getCause());
	}
}
