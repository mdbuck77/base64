package org.mibuck.base64;

//import org.apache.commons.io.IOUtils;
import picocli.CommandLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

//Name
//base64 - base64 encode/decode data and print to standard output
//Synopsis
//base64 [OPTION]... [FILE]
//Description
//
//Base64 encode or decode FILE, or standard input, to standard output.
//
//-w, --wrap=COLS
//    Wrap encoded lines after COLS character (default 76). Use 0 to disable line wrapping.
//-d, --decode
//    Decode data.
//-i, --ignore-garbage
//    When decoding, ignore non-alphabet characters.
//--help
//    display this help and exit
//--version
//    output version information and exit
//
//With no FILE, or when FILE is -, read standard input.
//
//The data are encoded as described for the base64 alphabet in RFC 3548. When decoding, the input may contain newlines in addition to the bytes of the formal base64 alphabet. Use --ignore-garbage to attempt to recover from any other non-alphabet bytes in the encoded stream.
// todo: create and use version provider to get the version from the .jar file but be careful of running from IDE as no jar will exist
@CommandLine.Command(
				name = "base64",
				description = "Encodes a file to Base64 or decodes a file from Base64.",
				version = "1.0",
				mixinStandardHelpOptions = true

)
public final class Base64 implements Callable<Integer> {

	@CommandLine.Parameters(
					index = "0",
					description = "The file to encode or decode",
					arity = "0..1"
	)
	private Path file;

	@CommandLine.Option(
					names = {"-d", "--decode"},
					description = "Decode data. ",
					arity = "0..1",
					defaultValue = "false",
					fallbackValue = "true"
	)
	private boolean decode;

	public static void main(String... args) {
		new CommandLine(new Base64()).execute(args);
	}

	@Override
	public Integer call() throws Exception {
//		System.out.println("this = " + this);
		if (this.decode) {
			final java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
			try (final InputStream inputStream = decoder.wrap(System.in)) {
				decoderConsumer(this.file).accept(inputStream);
			}
		} else {
			final java.util.Base64.Encoder encoder = java.util.Base64.getEncoder();
			try (final OutputStream outputStream = encoder.wrap(System.out)) {
				encoderConsumer(this.file).accept(outputStream);
			}
		}

		return 0;
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

		return outputStream ->  {
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

	@Override
	public String toString() {
		return "Base64{" +
						"file=" + file +
						", decode=" + decode +
						'}';
	}
}
