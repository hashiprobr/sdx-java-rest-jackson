package br.pro.hashi.sdx.rest.jackson.constant;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonGenerator;

import br.pro.hashi.sdx.rest.Hint;

public final class Types {
	private static final Type WRITER_CONSUMER = new Hint<Consumer<JsonGenerator>>() {}.getType();

	public static boolean instanceOfWriterConsumer(Object body, Type type) {
		return body != null && type.equals(WRITER_CONSUMER);
	}

	private Types() {
	}
}
