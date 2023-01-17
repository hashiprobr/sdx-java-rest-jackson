package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Serializer;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;
import br.pro.hashi.sdx.rest.transform.extension.Plumber;

public class JacksonSerializer implements Serializer {
	private final Plumber plumber;
	private final ConverterMapper mapper;

	public JacksonSerializer(ConverterMapper mapper) {
		this.plumber = new Plumber();
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
		}
	}

	@Override
	public Reader toReader(Object body, Type type) {
		Reader reader;
		Consumer<Writer> consumer = (writer) -> {
			try {
				mapper.writeValue(writer, body, type);
			} catch (IOException exception) {
				throw new Plumber.Exception(exception);
			}
		};
		try {
			reader = plumber.connect(consumer);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
		return reader;
	}
}
