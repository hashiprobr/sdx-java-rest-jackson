package br.pro.hashi.sdx.rest.jackson.mock;

public class ObjectWithTransient {
	public transient boolean field;

	public ObjectWithTransient() {
		this.field = false;
	}

	public boolean getField() {
		return field;
	}

	public void setField(boolean field) {
		this.field = field;
	}
}
