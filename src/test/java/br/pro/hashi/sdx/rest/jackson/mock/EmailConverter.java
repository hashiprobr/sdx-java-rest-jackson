package br.pro.hashi.sdx.rest.jackson.mock;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class EmailConverter implements JacksonConverter<Email, String> {
	@Override
	public String to(Email source) {
		return "%s@%s".formatted(source.getLogin(), source.getDomain());
	}

	@Override
	public Email from(String target) {
		String[] items = target.split("@");
		Email email = new Email();
		email.setLogin(items[0]);
		email.setDomain(items[1]);
		return email;
	}
}
