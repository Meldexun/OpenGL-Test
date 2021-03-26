package opengltest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class FileSupplier implements Supplier<String> {

	private final File file;

	public FileSupplier(File file) {
		this.file = file;
	}

	@Override
	public String get() {
		StringBuilder sb = new StringBuilder();

		try (Stream<String> stream = Files.lines(this.file.toPath())) {
			stream.forEach(s -> {
				sb.append(s);
				sb.append('\n');
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

}
