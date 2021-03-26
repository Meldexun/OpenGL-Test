package opengltest;

import static org.lwjgl.opengl.GL43C.*;

import java.util.function.Supplier;

public class Shader {

	private final Supplier<String> vertexShaderSupplier;
	private final Supplier<String> geometryShaderSupplier;
	private final Supplier<String> fragmentShaderSupplier;
	private boolean initialized;
	private int shaderProgramm;

	public Shader(Supplier<String> vertexShaderSupplier, Supplier<String> geometryShaderSupplier, Supplier<String> fragmentShaderSupplier) {
		this.vertexShaderSupplier = vertexShaderSupplier;
		this.fragmentShaderSupplier = fragmentShaderSupplier;
		this.geometryShaderSupplier = geometryShaderSupplier;
	}

	public void use() {
		if (!this.initialized) {
			this.shaderProgramm = glCreateProgram();

			int vertexShader = glCreateShader(GL_VERTEX_SHADER);
			glShaderSource(vertexShader, this.vertexShaderSupplier.get());
			glCompileShader(vertexShader);
			int i1 = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
			if (i1 != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(vertexShader));
				throw new RuntimeException("Failed to compile vertex shader: " + i1);
			}

			int geometryShader = glCreateShader(GL_GEOMETRY_SHADER);
			glShaderSource(geometryShader, this.geometryShaderSupplier.get());
			glCompileShader(geometryShader);
			int i2 = glGetShaderi(geometryShader, GL_COMPILE_STATUS);
			if (i2 != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(geometryShader));
				throw new RuntimeException("Failed to compile geometry shader: " + i2);
			}

			int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			glShaderSource(fragmentShader, this.fragmentShaderSupplier.get());
			glCompileShader(fragmentShader);
			int i3 = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
			if (i3 != GL_TRUE) {
				System.err.println(glGetShaderInfoLog(fragmentShader));
				throw new RuntimeException("Failed to compile fragment shader: " + i3);
			}

			glAttachShader(this.shaderProgramm, vertexShader);
			glAttachShader(this.shaderProgramm, fragmentShader);
			glAttachShader(this.shaderProgramm, geometryShader);
			glLinkProgram(this.shaderProgramm);
			int i4 = glGetProgrami(this.shaderProgramm, GL_LINK_STATUS);
			if (i4 != GL_TRUE) {
				System.err.println(glGetProgramInfoLog(this.shaderProgramm));
				throw new RuntimeException("Failed to link programm: " + i4);
			}
			glDeleteShader(vertexShader);
			glDeleteShader(fragmentShader);
			glDeleteShader(geometryShader);

			this.initialized = true;
		}
		if (this.initialized) {
			glUseProgram(this.shaderProgramm);
		}
	}

	public int shaderId() {
		return this.shaderProgramm;
	}

}
