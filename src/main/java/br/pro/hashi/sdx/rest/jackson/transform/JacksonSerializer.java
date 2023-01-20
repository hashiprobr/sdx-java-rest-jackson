package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

public class JacksonSerializer implements Serializer {
	private final ConverterMapper mapper;

	public JacksonSerializer(ConverterMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void write(Object body, Type type, Writer writer) {
		try {
			mapper.writeValue(writer, body, type);
		} catch (DatabindException exception) {
			throw new SerializingException(exception);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		} finally {
			try {
				writer.close();
			} catch (IOException exception) {
				throw new UncheckedIOException(exception);
			}
		}
	}
}
