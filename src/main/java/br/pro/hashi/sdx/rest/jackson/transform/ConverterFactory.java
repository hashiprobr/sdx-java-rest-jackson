package br.pro.hashi.sdx.rest.jackson.transform;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Stack;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class ConverterFactory {
	private final TypeFactory typeFactory;

	public ConverterFactory(ObjectMapper objectMapper) {
		this.typeFactory = objectMapper.getTypeFactory();
	}

	JavaType constructType(Type type) {
		JavaType javaType;
		if (type instanceof ParameterizedType) {
			javaType = constructParametricType((ParameterizedType) type);
		} else {
			javaType = typeFactory.constructType(type);
		}
		return javaType;
	}

	private JavaType constructParametricType(ParameterizedType type) {
		JavaType javaType = null;

		Stack<Node> stack = new Stack<>();
		stack.push(new Node(type));

		while (!stack.isEmpty()) {
			Node node = stack.peek();

			if (javaType != null) {
				node.setJavaParameter(javaType);
				javaType = null;
			}

			if (node.hasNext()) {
				Type parameter = node.getParameter();
				if (parameter instanceof ParameterizedType) {
					stack.push(new Node((ParameterizedType) parameter));
				} else {
					node.setJavaParameter(typeFactory.constructType(parameter));
				}
			} else {
				javaType = node.construct();
				stack.pop();
			}
		}

		return javaType;
	}

	private class Node {
		private final Class<?> type;
		private final Type[] parameters;
		private final JavaType[] javaParameters;
		private int index;

		private Node(ParameterizedType type) {
			this.type = (Class<?>) type.getRawType();
			this.parameters = type.getActualTypeArguments();
			this.javaParameters = new JavaType[parameters.length];
			this.index = 0;
		}

		private boolean hasNext() {
			return index < parameters.length;
		}

		private Type getParameter() {
			return parameters[index];
		}

		private void setJavaParameter(JavaType type) {
			javaParameters[index] = type;
			index++;
		}

		private JavaType construct() {
			return typeFactory.constructParametricType(type, javaParameters);
		}
	}
}
