package br.pro.hashi.sdx.rest.jackson.transform;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class ConverterModule extends Module {
	private final ConverterSerializers serializers;
	private final ConverterDeserializers deserializers;

	public ConverterModule(ConverterFactory factory) {
		this.serializers = new ConverterSerializers(factory);
		this.deserializers = new ConverterDeserializers(factory);
	}

	public void addConverter(JacksonConverter<?, ?> converter) {
		serializers.addSerializer(converter);
		deserializers.addDeserializer(converter);
	}

	@Override
	public String getModuleName() {
		return getClass().getName();
	}

	@Override
	public Version version() {
		return Version.unknownVersion();
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addSerializers(serializers);
		context.addDeserializers(deserializers);
	}
}
