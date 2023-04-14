package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.transform.Hint;

public class ConverterMapper {
	private final ConverterFactory factory;
	private final ObjectMapper mapper;
	private final Type consumerType;

	public ConverterMapper(ConverterFactory factory, ObjectMapper mapper) {
		this.factory = factory;
		this.mapper = mapper
				.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)
				.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		this.consumerType = new Hint<Consumer<JsonGenerator>>() {}.getType();
	}

	void writeValue(Writer writer, Object body, Type type) throws IOException {
		if (type.equals(consumerType)) {
			@SuppressWarnings("unchecked")
			Consumer<JsonGenerator> consumer = (Consumer<JsonGenerator>) body;
			JsonGenerator generator = mapper.getFactory().createGenerator(writer);
			consumer.accept(generator);
		} else {
			mapper.writerFor(factory.constructType(type)).writeValue(writer, body);
		}
	}

	@SuppressWarnings("unchecked")
	<T> T readValue(Reader reader, Type type) throws IOException {
		T body;
		if (type.equals(JsonParser.class)) {
			body = (T) mapper.getFactory().createParser(reader);
		} else {
			body = mapper.readValue(reader, factory.constructType(type));
		}
		return body;
	}
}
