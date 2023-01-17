package br.pro.hashi.sdx.rest.jackson.transform;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Stack;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ConverterFactory {
	private final TypeFactory factory;

	public ConverterFactory(ObjectMapper mapper) {
		this.factory = mapper.getTypeFactory();
	}

	JavaType constructType(Type type) {
		JavaType javaType;
		if (type instanceof ParameterizedType) {
			javaType = constructType((ParameterizedType) type);
		} else {
			javaType = factory.constructType(type);
		}
		return javaType;
	}

	private JavaType constructType(ParameterizedType type) {
		Stack<StackNode> stack = new Stack<>();
		stack.push(new StackNode(type));
		JavaType javaType = null;
		while (!stack.isEmpty()) {
			StackNode node = stack.peek();
			if (javaType != null) {
				node.setJavaType(javaType);
				javaType = null;
			}
			if (node.hasNext()) {
				Type nextType = node.getType();
				if (nextType instanceof ParameterizedType) {
					stack.push(new StackNode((ParameterizedType) nextType));
				} else {
					node.setJavaType(factory.constructType(nextType));
				}
			} else {
				javaType = node.construct();
				stack.pop();
			}
		}
		return javaType;
	}

	private class StackNode {
		private final Class<?> rawType;
		private final Type[] types;
		private final JavaType[] javaTypes;
		private int index;

		private StackNode(ParameterizedType type) {
			this.rawType = (Class<?>) type.getRawType();
			this.types = type.getActualTypeArguments();
			this.javaTypes = new JavaType[this.types.length];
			this.index = 0;
		}

		private boolean hasNext() {
			return index < types.length;
		}

		private Type getType() {
			return types[index];
		}

		private void setJavaType(JavaType type) {
			javaTypes[index] = type;
			index++;
		}

		private JavaType construct() {
			return factory.constructParametricType(rawType, javaTypes);
		}
	}
}
