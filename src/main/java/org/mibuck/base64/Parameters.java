package org.mibuck.base64;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class Parameters {
  private static final String USAGE_TEXT =
      "base64 - base64 encode/decode data and print to standard output\n" +
          "Synopsis\n" +
          "base64 [OPTION]... [FILE]\n" +
          "Description\n" +
          "\n" +
          "Base64 encode or decode FILE, or standard input, to standard output.\n" +
          "\n" +
//          "-w, --wrap=COLS\n" +
//          "    Wrap encoded lines after COLS character (default 76). Use 0 to disable line wrapping.\n" +
          "-d, --decode\n" +
          "    Decode data.\n" +
//          "-i, --ignore-garbage\n" +
//          "    When decoding, ignore non-alphabet characters.\n" +
          "--help\n" +
          "    display this help and exit\n" +
          "--version\n" +
          "    output version information and exit\n" +
          "\n" +
          "With no FILE, or when FILE is -, read standard input.\n" +
          "\n" +
          "The data are encoded as described for the base64 alphabet in RFC 3548. When decoding, the input may contain newlines in addition to the bytes of the formal base64 alphabet. Use --ignore-garbage to attempt to recover from any other non-alphabet bytes in the encoded stream.\n";

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
      } else if ("--version".equals(arg)) {
        displayVersion();
        System.exit(0);
      } else if ("--help".equals(arg)) {
        usage();
        System.exit(0);
      } else {
        if (!"-".equals(arg)) {
          file = Paths.get(arg);
        }
      }
    }

    if (mode == Mode.ENCODE && file != null && Files.notExists(file)) {
      System.err.println(file + " does not exist.");
      usage();
      System.exit(-1);
    }

    return new Parameters(mode, file);
  }

  private static void displayVersion() {
    // todo: get from jar / manifest file?
    System.out.println("base64 v1.00.00");
  }

  private static void usage() {
    System.out.println(USAGE_TEXT);
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
