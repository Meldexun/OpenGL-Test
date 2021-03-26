package opengltest;

public class FPSLimiter {

	private static long lastTime = System.nanoTime();

	/**
	 * Should be called every frame
	 * @param fps the target fps
	 */
	public static void syncFPS(int fps) {
		long waitTime = 1_000_000_000L / fps;
		long t = System.nanoTime();
		while (t - lastTime < waitTime) {
			t = System.nanoTime();
		}
		lastTime = t;
	}

}
