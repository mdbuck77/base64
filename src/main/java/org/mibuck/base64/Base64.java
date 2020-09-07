package org.mibuck.base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public final class Base64 implements Runnable {

	public static void main(String... args) {
		try {
			new Base64(Parameters.valueOf(args)).run();
		} catch (Throwable t) {
			System.err.println(t.getLocalizedMessage());
		}
	}

	private final Parameters parameters;

	public Base64(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		try {
			switch (this.parameters.mode()) {
				case ENCODE:
					final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
					try (final OutputStream outputStream = encoder.wrap(System.out)) {
						encoderConsumer(parameters.file()).accept(outputStream);
					}
					break;

				case DECODE:
					final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
					try (final InputStream inputStream = decoder.wrap(System.in)) {
						decoderConsumer(this.parameters.file()).accept(inputStream);
					}
					break;

				default:
					throw new UnsupportedOperationException(String.valueOf(this.parameters.mode()));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static Consumer<InputStream> decoderConsumer(final Path destination) {
		if (destination == null) {
			return inputStream -> copy(inputStream, System.out);
		}

		return inputStream -> {
			try {
				Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	private static Consumer<OutputStream> encoderConsumer(final Path source) {
		if (source == null) {
			return outputStream -> copy(System.in, outputStream);
		}

		return outputStream -> {
			try {
				Files.copy(source, outputStream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}

	private static void copy(final InputStream inputStream, final OutputStream outputStream) {
		final byte[] buffer = new byte[64 * 1024];
		int len;
		try {
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
