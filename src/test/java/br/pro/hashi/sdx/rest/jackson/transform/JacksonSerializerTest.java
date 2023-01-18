package br.pro.hashi.sdx.rest.jackson.transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.function.Consumer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Hint;
import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;
import br.pro.hashi.sdx.rest.transform.extension.Plumber;

class JacksonSerializerTest {
	private MockedConstruction<Plumber> construction;
	private ConverterMapper mapper;
	private Serializer s;
	private Plumber plumber;

	@BeforeEach
	void setUp() {
		construction = mockConstruction(Plumber.class);
		mapper = mock(ConverterMapper.class);
		s = new JacksonSerializer(mapper);
		plumber = construction.constructed().get(0);
	}

	@AfterEach
	void tearDown() {
		construction.close();
	}

	@Test
	void writesWhatMapperWrites() throws IOException {
		Object body = mockMapper();
		StringWriter writer = new StringWriter();
		s.write(body, writer);
		assertEqualsBody(writer);
	}

	@Test
	void writesWhatMapperWritesWithHint() throws IOException {
		Object body = mockMapper();
		StringWriter writer = new StringWriter();
		s.write(body, new Hint<Object>() {}.getType(), writer);
		assertEqualsBody(writer);
	}

	private void assertEqualsBody(StringWriter writer) {
		assertEqualsBody(writer.toString());
	}

	@Test
	void writeThrowsSerializingExceptionIfMapperThrowsDatabindException() throws IOException {
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
	void writeThrowsUncheckedIOExceptionIfMapperThrowsIOException() throws IOException {
		Object body = new Object();
		Throwable cause = new IOException();
		doThrow(cause).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		Writer writer = new StringWriter();
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.write(body, writer);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void readsWhatMapperWrites() throws IOException {
		Object body = mockMapper();
		mockPlumber();
		Reader reader = s.toReader(body);
		assertEqualsBody(reader);
	}

	@Test
	void readsWhatMapperWritesWithHint() throws IOException {
		Object body = mockMapper();
		mockPlumber();
		Reader reader = s.toReader(body, new Hint<Object>() {}.getType());
		assertEqualsBody(reader);
	}

	private void assertEqualsBody(Reader reader) throws IOException {
		int length;
		char[] chars = new char[4];
		int offset = 0;
		int remaining = chars.length;
		while (remaining > 0 && (length = reader.read(chars, offset, remaining)) != -1) {
			offset += length;
			remaining -= length;
		}
		assertEquals(-1, reader.read());
		assertEqualsBody(new String(chars));
		reader.close();
	}

	@Test
	void throwsIOExceptionIfMapperThrowsIOException() throws IOException {
		Object body = new Object();
		Throwable cause = new IOException();
		doThrow(cause).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		mockPlumber();
		Exception exception = assertThrows(Plumber.Exception.class, () -> {
			s.toReader(body);
		});
		assertSame(cause, exception.getCause());
	}

	@Test
	void throwsUncheckedIOExceptionIfPlumberThrowsIOException() throws IOException {
		Object body = mockMapper();
		Throwable cause = new IOException();
		when(plumber.connect(any())).thenThrow(cause);
		Exception exception = assertThrows(UncheckedIOException.class, () -> {
			s.toReader(body);
		});
		assertSame(cause, exception.getCause());
	}

	private Object mockMapper() throws IOException {
		Object body = new Object();
		doAnswer((invocation) -> {
			Appendable appendable = invocation.getArgument(0);
			appendable.append("body");
			return null;
		}).when(mapper).writeValue(any(Writer.class), eq(body), eq(Object.class));
		return body;
	}

	private void mockPlumber() throws IOException {
		when(plumber.connect(any())).thenAnswer((invocation) -> {
			Consumer<Writer> consumer = invocation.getArgument(0);
			Writer writer = new StringWriter();
			consumer.accept(writer);
			return new StringReader(writer.toString());
		});
	}

	private void assertEqualsBody(String content) {
		assertEquals("body", content);
	}
}
