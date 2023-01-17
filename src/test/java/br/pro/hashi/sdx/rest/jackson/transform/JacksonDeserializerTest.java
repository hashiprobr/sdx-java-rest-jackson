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
	void returnsWhatMapperReturns() throws IOException {
		Reader reader = newReader();
		Object body = mockReturn(reader);
		assertSame(body, d.fromReader(reader, Object.class));
	}

	@Test
	void returnsWhatMapperReturnsWithHint() throws IOException {
		Reader reader = newReader();
		Object body = mockReturn(reader);
		assertSame(body, d.fromReader(reader, new Hint<Object>() {}.getType()));
	}

	@Test
	void throwsDeserializingExceptionIfMapperThrowsDatabindException() throws IOException {
		Reader reader = newReader();
		Throwable cause = mock(DatabindException.class);
		mockThrow(reader, cause);
		Exception exception = assertThrows(DeserializingException.class, () -> {
			d.fromReader(reader, Object.class);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void throwsDeserializingExceptionIfMapperThrowsIOException() throws IOException {
		Reader reader = newReader();
		Throwable cause = new IOException();
		mockThrow(reader, cause);
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			d.fromReader(reader, Object.class);
		});
		assertSame(cause, exception.getCause());
	}

	private Reader newReader() {
		return new StringReader("content");
	}

	private Object mockReturn(Reader reader) throws IOException {
		Object body = new Object();
		when(mapper.readValue(eq(reader), eq(Object.class))).thenReturn(body);
		return body;
	}

	private Throwable mockThrow(Reader reader, Throwable cause) throws IOException {
		when(mapper.readValue(eq(reader), eq(Object.class))).thenThrow(cause);
		return cause;
	}
}
