#base64
base64 - base64 encode/decode data and print to standard output
## Synopsis
base64 [OPTION]... [FILE]
## Description

Base64 encode or decode FILE, or standard input, to standard output.

```
-w, --wrap=COLS
    Wrap encoded lines after COLS character (default 76). Use 0 to disable line wrapping. 
-d, --decode
    Decode data. 
-i, --ignore-garbage
    When decoding, ignore non-alphabet characters. 
--help
    display this help and exit 
--version
    output version information and exit
```
With no FILE, or when FILE is -, read standard input.

The data are encoded as described for the base64 alphabet in RFC 3548. When decoding, the input may contain newlines in addition to the bytes of the formal base64 alphabet. Use --ignore-garbage to attempt to recover from any other non-alphabet bytes in the encoded stream. 

Taken from [Linux](https://linux.die.net/man/1/base64)