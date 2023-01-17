package br.pro.hashi.sdx.rest.jackson.mock;

import java.util.List;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class AddressConverter implements JacksonConverter<Address, List<String>> {
	@Override
	public List<String> to(Address source) {
		return List.of(source.getCity(), Integer.toString(source.getNumber()), source.getStreet());
	}

	@Override
	public Address from(List<String> target) {
		return new Address(target.get(2), Integer.parseInt(target.get(1)), target.get(0));
	}
}
