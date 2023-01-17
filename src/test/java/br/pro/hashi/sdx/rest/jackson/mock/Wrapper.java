package br.pro.hashi.sdx.rest.jackson.mock;

public class Wrapper<T> {
	T value;

	Wrapper() {
	}

	public Wrapper(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}
}
