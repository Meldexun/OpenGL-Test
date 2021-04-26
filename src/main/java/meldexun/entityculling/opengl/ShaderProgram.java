package meldexun.entityculling.opengl;

import static org.lwjgl.opengl.GL46C.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ShaderProgram {

	private int shaderProgram;

	private ShaderProgram(Map<Integer, Supplier<String>> shaderMap) {
		this.shaderProgram = glCreateProgram();

		List<Integer> shaderList = new ArrayList<>();
		for (Map.Entry<Integer, Supplier<String>> entry : shaderMap.entrySet()) {
			int shader = glCreateShader(entry.getKey());
			glShaderSource(shader, entry.getValue().get());
			glCompileShader(shader);
			int compileStatus = glGetShaderi(shader, GL_COMPILE_STATUS);
			if (compileStatus != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(shader));
				throw new RuntimeException("Failed to compile shader: " + compileStatus);
			}
			shaderList.add(shader);
		}

		shaderList.forEach((shader -> glAttachShader(ShaderProgram.this.shaderProgram, shader)));
		glLinkProgram(this.shaderProgram);
		int linkStatus = glGetProgrami(this.shaderProgram, GL_LINK_STATUS);
		if (linkStatus != GL_TRUE) {
			System.err.println(glGetProgramInfoLog(this.shaderProgram));
			throw new RuntimeException("Failed to link programm: " + linkStatus);
		}
		shaderList.forEach((shader -> glDeleteShader(shader)));
	}

	public void use() {
		glUseProgram(this.shaderProgram);
	}

	public void delete() {
		glDeleteProgram(this.shaderProgram);
	}

	public int getShaderProgram() {
		return this.shaderProgram;
	}

	public static class Builder {

		private final Map<Integer, Supplier<String>> shaderMap = new HashMap<>();

		public ShaderProgram.Builder addShader(int type, Supplier<String> source) {
			this.shaderMap.put(type, source);
			return this;
		}

		public ShaderProgram build() {
			return new ShaderProgram(this.shaderMap);
		}

	}

}
