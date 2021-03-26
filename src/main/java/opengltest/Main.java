package opengltest;

import static org.lwjgl.opengl.GL43C.*;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

public class Main {

	private static long window;

	public static void main(String[] args) {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

		window = GLFW.glfwCreateWindow(800, 450, "OpenGL - Test", 0, 0);
		if (window == 0) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
		});

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
			GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		}

		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(0);
		GLFW.glfwShowWindow(window);
		GL.createCapabilities();

		try {
			prepareRendering();
			while (!GLFW.glfwWindowShouldClose(window)) {
				loop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		GLFW.glfwTerminate();
	}

	private static Shader shader;
	private static int projection;
	private static int view;
	private static int model;
	private static int vbo;
	private static ByteBuffer vboBuffer = ByteBuffer.allocateDirect(0xFFFF).order(ByteOrder.nativeOrder());
	private static int ssbo;
	private static ByteBuffer ssboBuffer = ByteBuffer.allocateDirect(0xFF).order(ByteOrder.nativeOrder());
	private static ByteBuffer oldBuffer;

	private static void prepareRendering() {
		shader = new Shader(new FileSupplier(new File("shader.vs")), new FileSupplier(new File("shader.gs")), new FileSupplier(new File("shader.fs")));
		shader.use();

		projection = glGetUniformLocation(shader.shaderId(), "projection");
		view = glGetUniformLocation(shader.shaderId(), "view");
		model = glGetUniformLocation(shader.shaderId(), "model");

		vboBuffer.asIntBuffer().put(0, 1);
		vboBuffer.asIntBuffer().put(1, 4);
		vbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, vboBuffer, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);

		ssbo = glGenBuffers();
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
		glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 2, ssbo);
		glBufferData(GL_SHADER_STORAGE_BUFFER, ssboBuffer, GL_DYNAMIC_READ);
		glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
	}

	private static void loop() {
		glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		glEnable(GL11.GL_DEPTH_TEST);
		glDepthFunc(GL11.GL_LEQUAL);

		// update matrices
		{
			glUniformMatrix4fv(projection, false, new float[] {
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					0, 0, 0, 1
			});
			glUniformMatrix4fv(view, false, new float[] {
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					0, 0, 0, 1
			});
			glUniformMatrix4fv(model, false, new float[] {
					1, 0, 0, 0,
					0, 1, 0, 0,
					0, 0, 1, 0,
					0, 0, 0, 1
			});
		}

		// render AABBs
		{
			// draw vbo
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glEnableVertexAttribArray(0);
			glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, 0, 0);
			if (counter < 4 || counter >= 8) {
				// draw 16
				glDrawArrays(GL_POINTS, 1, 1);
			} else {
				// draw 2
				glDrawArrays(GL_POINTS, 0, 1);
			}
			glDisableVertexAttribArray(0);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}

		// update ssbo
		{
			glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
			glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
		
			if (counter < 1 || counter >= 12) {
				// wait
			} else {
				// read ssbo
				oldBuffer = glMapBuffer(GL_SHADER_STORAGE_BUFFER, GL_READ_ONLY, ssboBuffer.capacity(), oldBuffer);
				ssboBuffer.rewind();
				ssboBuffer.put(oldBuffer);
				glUnmapBuffer(GL_SHADER_STORAGE_BUFFER);
				System.out.println("read " + ssboBuffer.getInt(0));
			}
			
			// clear ssbo
			glClearBufferData(GL_SHADER_STORAGE_BUFFER, GL_R8, GL_RED, GL_BYTE, (ByteBuffer) null);

			glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
			glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
		}

		GLFW.glfwSwapBuffers(window);
		GLFW.glfwPollEvents();

		FPSLimiter.syncFPS(60);

		counter++;
	}

	static int counter;
}
