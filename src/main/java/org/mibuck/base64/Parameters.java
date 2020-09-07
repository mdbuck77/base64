package org.mibuck.base64;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class Parameters {

	public enum Mode {
		DECODE,
		ENCODE;
	}

	public static Parameters valueOf(final String... args) {
		Objects.requireNonNull(args, "Specified args cannot be null.");

		Mode mode = Mode.ENCODE;
		Path file = null;

		for (String arg : args) {
			final String stripped = arg.strip();
			if ("-d".equals(stripped)) {
				mode = Mode.DECODE;
			} else {
				file = Paths.get(arg);
			}
		}

		if (file == null) {
			usage();
			System.exit(-1);
		}

		return new Parameters(mode, file);
	}

	private static void usage() {
		// todo: implement
		throw new UnsupportedOperationException();
	}

	private final Mode mode;
	private final Path file;

	private Parameters(Mode mode, Path file) {
		this.mode = mode;
		this.file = file;
	}

	public Mode mode() {
		return mode;
	}

	public Path file() {
		return file;
	}
}
