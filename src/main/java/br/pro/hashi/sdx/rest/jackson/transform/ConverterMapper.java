package br.pro.hashi.sdx.rest.jackson.transform;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.pro.hashi.sdx.rest.jackson.constant.Types;
import br.pro.hashi.sdx.rest.transform.exception.SerializingException;

public class ConverterMapper {
	private final ConverterFactory converterFactory;
	private final ObjectMapper objectMapper;

	public ConverterMapper(ConverterFactory converterFactory, ObjectMapper objectMapper) {
		this.converterFactory = converterFactory;
		this.objectMapper = objectMapper
				.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE)
				.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
	}

	void writeValue(Writer writer, Object body, Type type) throws IOException {
		if (body instanceof JsonNode) {
			objectMapper.getFactory().createGenerator(writer).writeTree((JsonNode) body);
			return;
		}
		if (body instanceof JsonParser) {
			JsonParser parser = (JsonParser) body;
			JsonGenerator generator = createGenerator(writer);
			JsonToken token;
			while ((token = parser.nextToken()) != null) {
				switch (token) {
				case VALUE_NULL:
					generator.writeNull();
					break;
				case VALUE_FALSE:
					generator.writeBoolean(false);
					break;
				case VALUE_TRUE:
					generator.writeBoolean(true);
					break;
				case VALUE_NUMBER_INT:
					generator.writeNumber(parser.getBigIntegerValue());
					break;
				case VALUE_NUMBER_FLOAT:
					generator.writeNumber(parser.getDecimalValue());
					break;
				case VALUE_STRING:
					generator.writeString(parser.getValueAsString());
					break;
				case FIELD_NAME:
					generator.writeFieldName(parser.currentName());
					break;
				case START_ARRAY:
					generator.writeStartArray();
					break;
				case END_ARRAY:
					generator.writeEndArray();
					break;
				case START_OBJECT:
					generator.writeStartObject();
					break;
				case END_OBJECT:
					generator.writeEndObject();
					break;
				default:
					throw new SerializingException("Token %s is not supported".formatted(token.toString()));
				}
			}
			parser.close();
			generator.flush();
			return;
		}
		if (Types.instanceOfWriterConsumer(body, type)) {
			@SuppressWarnings("unchecked")
			Consumer<JsonGenerator> consumer = (Consumer<JsonGenerator>) body;
			consumer.accept(createGenerator(writer));
			return;
		}
		objectMapper.writerFor(converterFactory.constructType(type)).writeValue(writer, body);
	}

	private JsonGenerator createGenerator(Writer writer) throws IOException {
		return objectMapper.getFactory()
				.createGenerator(writer)
				.useDefaultPrettyPrinter();
	}

	<T> T readValue(Reader reader, Type type) throws IOException {
		if (type.equals(JsonNode.class)) {
			@SuppressWarnings("unchecked")
			T body = (T) objectMapper.readTree(reader);
			return body;
		}
		if (type.equals(JsonParser.class)) {
			@SuppressWarnings("unchecked")
			T body = (T) objectMapper.getFactory().createParser(reader);
			return body;
		}
		return objectMapper.readValue(reader, converterFactory.constructType(type));
	}
}
