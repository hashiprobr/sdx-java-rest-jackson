package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ConverterMapper {
	private final ConverterFactory factory;
	private final ObjectMapper mapper;

	public ConverterMapper(ConverterFactory factory, ObjectMapper mapper) {
		this.factory = factory;
		this.mapper = mapper;
	}

	void writeValue(Writer writer, Object body, Type type) throws DatabindException, IOException {
		mapper.writerFor(factory.constructType(type)).writeValue(writer, body);
	}

	<T> T readValue(Reader reader, Type type) throws DatabindException, IOException {
		return mapper.readValue(reader, factory.constructType(type));
	}
}
