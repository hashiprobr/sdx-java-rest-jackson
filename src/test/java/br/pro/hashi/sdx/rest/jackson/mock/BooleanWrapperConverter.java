package br.pro.hashi.sdx.rest.jackson.mock;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class BooleanWrapperConverter implements JacksonConverter<Wrapper<Boolean>, String> {
	@Override
	public String to(Wrapper<Boolean> source) {
		return Boolean.toString(source.getValue());
	}

	@Override
	public Wrapper<Boolean> from(String target) {
		return new Wrapper<>(Boolean.parseBoolean(target));
	}
}
