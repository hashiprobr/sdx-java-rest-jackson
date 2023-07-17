package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.exc.StreamWriteException;

import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

class JacksonSerializerTest {
	private ConverterMapper converterMapper;
	private Serializer s;

	@BeforeEach
	void setUp() {
		converterMapper = mock(ConverterMapper.class);
		s = new JacksonSerializer(converterMapper);
	}

	@Test
	void writes() {
		Object body = new Object();
		assertDoesNotThrow(() -> {
			doAnswer((invocation) -> {
				Writer writer = invocation.getArgument(0);
				writer.write("body");
				return null;
			}).when(converterMapper).writeValue(any(), eq(body), eq(Object.class));
		});
		StringWriter writer = new StringWriter();
		s.write(body, Object.class, writer);
		assertContentEquals("body", writer);
	}

	private void assertContentEquals(String expected, StringWriter writer) {
		assertEquals(expected, writer.toString());
	}

	@Test
	void doesNotWriteIfMapperThrowsStreamWriteException() {
		Object body = new Object();
		Throwable cause = mock(StreamWriteException.class);
		mockMapperThrow(body, cause);
		Writer writer = new StringWriter();
		Exception exception = assertThrows(SerializingException.class, () -> {
			s.write(body, Object.class, writer);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void doesNotWriteIfMapperThrowsIOException() {
		Object body = new Object();
		Throwable cause = new IOException();
		mockMapperThrow(body, cause);
		Writer writer = new StringWriter();
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.write(body, Object.class, writer);
		});
		assertSame(cause, exception.getCause());
	}

	private Throwable mockMapperThrow(Object body, Throwable cause) {
		try {
			doThrow(cause).when(converterMapper).writeValue(any(), eq(body), eq(Object.class));
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
		return cause;
	}
}
