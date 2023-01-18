package br.pro.hashi.sdx.rest.jackson.mock;

import java.util.ArrayList;
import java.util.List;

import br.pro.hashi.sdx.rest.jackson.JacksonConverter;

public class SheetConverter implements JacksonConverter<Sheet, List<Address>> {
	@Override
	public List<Address> to(Sheet source) {
		List<Address> target = new ArrayList<>();
		for (List<String> row : source.getRows()) {
			target.add(new Address(row.get(0), Integer.parseInt(row.get(1)), row.get(2)));
		}
		return target;
	}

	@Override
	public Sheet from(List<Address> target) {
		Sheet sheet = new Sheet();
		for (Address address : target) {
			sheet.addRow(address.getStreet(), address.getNumber(), address.getCity());
		}
		return sheet;
	}
}
