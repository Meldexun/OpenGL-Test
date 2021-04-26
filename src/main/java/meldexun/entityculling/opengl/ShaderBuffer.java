package meldexun.entityculling.opengl;

import static org.lwjgl.opengl.GL46C.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ShaderBuffer {

	public final int size;
	public final int flags;
	public final int[] buffers;

	public ShaderBuffer(int size, int flags, int bufferCount) {
		this.size = size;
		this.flags = flags;
		this.buffers = new int[bufferCount];

		glCreateBuffers(this.buffers);
		for (int buffer : this.buffers) {
			glNamedBufferStorage(buffer, size, flags);
		}
	}

	public void delete() {
		for (int i = 0; i < this.buffers.length; i++) {
			glDeleteBuffers(this.buffers[i]);
			this.buffers[i] = 0;
		}
	}

	public static class ShaderBufferMap extends ShaderBuffer {

		public final ByteBuffer[] byteBuffers;
		public final FloatBuffer[] floatBuffers;
		public final IntBuffer[] intBuffers;

		public ShaderBufferMap(int size, int flags, int bufferCount) {
			super(size, flags, bufferCount);
			this.byteBuffers = new ByteBuffer[bufferCount];
			this.floatBuffers = new FloatBuffer[bufferCount];
			this.intBuffers = new IntBuffer[bufferCount];
		}

		public void map(int bufferNumber, long offset, long length, int access) {
			ByteBuffer byteBuffer = glMapNamedBufferRange(this.buffers[bufferNumber], offset, length, access, this.byteBuffers[bufferNumber]);
			this.byteBuffers[bufferNumber] = byteBuffer;
			this.floatBuffers[bufferNumber] = byteBuffer.asFloatBuffer();
			this.intBuffers[bufferNumber] = byteBuffer.asIntBuffer();
		}

		public void unmap(int bufferNumber) {
			glUnmapNamedBuffer(this.buffers[bufferNumber]);
		}

		public void delete() {
			for (int i = 0; i < this.buffers.length; i++) {
				this.byteBuffers[i] = null;
				this.floatBuffers[i] = null;
				this.intBuffers[i] = null;
			}
			super.delete();
		}

	}

	public static class ShaderBufferNormal extends ShaderBuffer {

		public ByteBuffer byteBuffer;
		public FloatBuffer floatBuffer;
		public IntBuffer intBuffer;

		public ShaderBufferNormal(int size, int flags, int bufferCount) {
			super(size, flags, bufferCount);
			this.byteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
			this.floatBuffer = this.byteBuffer.asFloatBuffer();
			this.intBuffer = this.byteBuffer.asIntBuffer();
		}

		public void getSubData(int bufferNumber, int offset) {
			glGetNamedBufferSubData(this.buffers[bufferNumber], offset, this.byteBuffer);
		}

		@Override
		public void delete() {
			this.byteBuffer = null;
			this.floatBuffer = null;
			this.intBuffer = null;
			super.delete();
		}

	}

}
