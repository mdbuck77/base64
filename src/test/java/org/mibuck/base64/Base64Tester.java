package org.mibuck.base64;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public final class Base64Tester {

	/**
	 * encode the specified file as Base64 and spit it out to stdout.
	 */
	@Test
	public void encode() throws URISyntaxException, IOException {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		final URL resourceUrl = contextClassLoader.getResource("AbstractCommandLineConverter.class");
		final File file = new File(resourceUrl.toURI());

		final PrintStream out = System.out;
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			System.setOut(new PrintStream(outputStream, true));
			Base64.main(file.toString());
		} finally {
			System.setOut(out);
		}

		final URL expectedUrl = contextClassLoader.getResource("AbstractCommandLineConverter.class.base64");
		final String expected = Files.readString(new File(expectedUrl.toURI()).toPath());

		assertThat(outputStream.toString()).isEqualTo(expected);

	}

	/**
	 * encode the specified file as Base64 and spit it out to stdout.
	 */
	@Test
	public void decode() throws URISyntaxException, IOException {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

		final Path tempFile = Files.createTempFile("Base64Tester-", "-decode");

		final byte[] bytes;
		{
			final URL resourceUrl = contextClassLoader.getResource("AbstractCommandLineConverter.class.base64");
			final File file = new File(resourceUrl.toURI());
			bytes = Files.readAllBytes(file.toPath());
		}

		final InputStream in = System.in;
		try {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
			System.setIn(byteArrayInputStream);

			Base64.main("-d", tempFile.toString());
		} finally {
			System.setIn(in);
		}

		final byte[] actual = Files.readAllBytes(tempFile);

		final URL resourceUrl = contextClassLoader.getResource("AbstractCommandLineConverter.class");
		final File file = new File(resourceUrl.toURI());
		final byte[] expected = Files.readAllBytes(file.toPath());

		assertThat(actual).isEqualTo(expected);
	}

}
