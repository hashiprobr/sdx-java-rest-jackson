package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.DatabindException;

import br.pro.hashi.sdx.rest.transform.Deserializer;
import br.pro.hashi.sdx.rest.transform.exception.DeserializingException;

public class JacksonDeserializer implements Deserializer {
	private final ConverterMapper converterMapper;

	public JacksonDeserializer(ConverterMapper converterMapper) {
		this.converterMapper = converterMapper;
	}

	@Override
	public <T> T read(Reader reader, Type type) {
		T body;
		try {
			body = converterMapper.readValue(reader, type);
		} catch (DatabindException exception) {
			throw new DeserializingException(exception);
		} catch (IOException exception) {
			throw new UncheckedIOException(exception);
		}
		return body;
	}
}
