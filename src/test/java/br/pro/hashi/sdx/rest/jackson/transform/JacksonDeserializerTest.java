package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UncheckedIOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.Hint;
import br.pro.hashi.sdx.rest.transform.exception.DeserializingException;

class JacksonDeserializerTest {
	private ConverterMapper mapper;
	private Deserializer d;

	@BeforeEach
	void setUp() {
		mapper = mock(ConverterMapper.class);
		d = new JacksonDeserializer(mapper);
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
		try {
			when(mapper.readValue(eq(reader), eq(Object.class))).thenReturn(body);
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
		return body;
	}

	@Test
	void doesNotReadIfMapperThrowsDatabindException() {
		Reader reader = newReader();
		Throwable cause = mock(DatabindException.class);
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
		try {
			when(mapper.readValue(eq(reader), eq(Object.class))).thenThrow(cause);
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
		return cause;
	}

	private Reader newReader() {
		return new StringReader("content");
	}
}
