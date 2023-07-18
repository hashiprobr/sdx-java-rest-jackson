package br.pro.hashi.sdx.rest.jackson.mock;

public class ObjectWithString {
	private String value;

	ObjectWithString() {
	}

	public ObjectWithString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
