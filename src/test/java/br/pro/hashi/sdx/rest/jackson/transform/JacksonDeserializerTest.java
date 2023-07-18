package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamReadException;

import br.pro.hashi.sdx.rest.Hint;
import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.exception.DeserializingException;

class JacksonDeserializerTest {
	private ConverterMapper converterMapper;
	private Deserializer d;

	@BeforeEach
	void setUp() {
		converterMapper = mock(ConverterMapper.class);
		d = new JacksonDeserializer(converterMapper);
	}

	@Test
	void reads() {
		Reader reader = newReader();
		Object body = mockMapperReturn(reader);
		assertSame(body, d.read(reader, Object.class));
	}

	@Test
	void readsWithHint() {
		Reader reader = newReader();
		Object body = mockMapperReturn(reader);
		assertSame(body, d.read(reader, new Hint<Object>() {}.getType()));
	}

	private Object mockMapperReturn(Reader reader) {
		Object body = new Object();
		assertDoesNotThrow(() -> {
			when(converterMapper.readValue(reader, Object.class)).thenReturn(body);
		});
		return body;
	}

	@Test
	void doesNotReadIfMapperThrowsStreamReadException() {
		Reader reader = newReader();
		Throwable cause = mock(StreamReadException.class);
		mockMapperThrow(reader, cause);
		Exception exception = assertThrows(DeserializingException.class, () -> {
			d.read(reader, Object.class);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void doesNotReadIfMapperThrowsIOException() {
		Reader reader = newReader();
		Throwable cause = new IOException();
		mockMapperThrow(reader, cause);
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			d.read(reader, Object.class);
		});
		assertSame(cause, exception.getCause());
	}

	private Throwable mockMapperThrow(Reader reader, Throwable cause) {
		assertDoesNotThrow(() -> {
			when(converterMapper.readValue(reader, Object.class)).thenThrow(cause);
		});
		return cause;
	}

	private Reader newReader() {
		return new StringReader("content");
	}
}
