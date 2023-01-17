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
					node.setJavaParameter(factory.constructType(parameter));
				}
			} else {
				javaType = node.construct();
				stack.pop();
			}
		}

		return javaType;
	}

	private class Node {
		private final Type type;
		private final Type[] parameters;
		private final JavaType[] javaParameters;
		private int index;

		private Node(ParameterizedType type) {
			Type[] parameters = type.getActualTypeArguments();
			this.type = type.getRawType();
			this.parameters = parameters;
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
			return factory.constructParametricType((Class<?>) type, javaParameters);
		}
	}
}
