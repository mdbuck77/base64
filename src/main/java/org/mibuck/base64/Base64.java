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
		new Base64(Parameters.valueOf(args)).run();
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
						decoderConsumer(parameters.file()).accept(inputStream);
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
			return in -> {
				try {
					in.transferTo(System.out);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};
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
			return out -> {
				try {
					System.in.transferTo(out);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			};
		}

		return outputStream -> {
			try {
				Files.copy(source, outputStream);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}
}
