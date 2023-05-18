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

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

class JacksonSerializerTest {
	private ConverterMapper mapper;
	private Serializer s;

	@BeforeEach
	void setUp() {
		mapper = mock(ConverterMapper.class);
		s = new JacksonSerializer(mapper);
	}

	@Test
	void writes() {
		Object body = new Object();
		assertDoesNotThrow(() -> {
			doAnswer((invocation) -> {
				Writer writer = invocation.getArgument(0);
				writer.write("body");
				return null;
			}).when(mapper).writeValue(any(), eq(body), eq(Object.class));
		});
		StringWriter writer = new StringWriter();
		s.write(body, writer);
		assertContentEquals("body", writer);
	}

	@Test
	void writesNull() {
		StringWriter writer = new StringWriter();
		s.write(null, writer);
		assertContentEquals("", writer);
	}

	private void assertContentEquals(String expected, StringWriter writer) {
		assertEquals(expected, writer.toString());
	}

	@Test
	void doesNotWriteIfMapperThrowsDatabindException() {
		Object body = new Object();
		Throwable cause = mock(DatabindException.class);
		mockMapperThrow(body, cause);
		Writer writer = new StringWriter();
		Exception exception = assertThrows(SerializingException.class, () -> {
			s.write(body, writer);
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
			s.write(body, writer);
		});
		assertSame(cause, exception.getCause());
	}

	private Throwable mockMapperThrow(Object body, Throwable cause) {
		try {
			doThrow(cause).when(mapper).writeValue(any(), eq(body), eq(Object.class));
		} catch (IOException exception) {
			throw new AssertionError(exception);
		}
		return cause;
	}
}
