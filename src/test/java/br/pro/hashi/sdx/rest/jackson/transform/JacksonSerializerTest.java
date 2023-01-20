package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Hint;
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
	void writes() throws IOException {
		Object body = mockMapperReturn();
		StringWriter writer = new StringWriter();
		s.write(body, writer);
		assertEqualsBody(writer);
	}

	@Test
	void writesWithHint() throws IOException {
		Object body = mockMapperReturn();
		StringWriter writer = new StringWriter();
		s.write(body, new Hint<Object>() {}.getType(), writer);
		assertEqualsBody(writer);
	}

	private void assertEqualsBody(StringWriter writer) {
		assertEquals("body", writer.toString());
	}

	@Test
	void doesNotWriteIfCloseThrows() throws IOException {
		Object body = mockMapperReturn();
		Writer writer = spy(new StringWriter());
		Throwable cause = new IOException();
		doThrow(cause).when(writer).close();
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.write(body, writer);
		});
		assertSame(cause, exception.getCause());
	}

	private Object mockMapperReturn() throws IOException {
		Object body = new Object();
		doAnswer((invocation) -> {
			Writer writer = invocation.getArgument(0);
			writer.write("body");
			return null;
		}).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		return body;
	}

	@Test
	void doesNotWriteIfMapperThrowsDatabindException() throws IOException {
		Object body = new Object();
		Throwable cause = mock(DatabindException.class);
		doThrow(cause).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		Writer writer = new StringWriter();
		Exception exception = assertThrows(SerializingException.class, () -> {
			s.write(body, writer);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void doesNotWriteIfMapperThrowsIOException() throws IOException {
		Object body = new Object();
		Throwable cause = new IOException();
		doThrow(cause).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		Writer writer = new StringWriter();
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.write(body, writer);
		});
		assertSame(cause, exception.getCause());
	}
}
