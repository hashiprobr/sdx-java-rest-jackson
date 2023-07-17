package br.pro.hashi.sdx.rest.jackson.transform;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class ConverterModule extends Module {
	private final ConverterSerializers serializers;
	private final ConverterDeserializers deserializers;

	public ConverterModule(ConverterFactory converterFactory) {
		this.serializers = new ConverterSerializers(converterFactory);
		this.deserializers = new ConverterDeserializers(converterFactory);
	}

	public void registerConverter(JacksonConverter<?, ?> converter) {
		serializers.addConverter(converter);
		deserializers.addConverter(converter);
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
