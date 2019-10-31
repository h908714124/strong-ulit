package com.strong.ulit;

import net.jbock.CommandLineArguments;
import net.jbock.Parameter;
import net.jbock.PositionalParameter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@CommandLineArguments
abstract class RemoveSingleLinebreaks {

  @Parameter(longName = "charset",
      mappedBy = CharsetMapper.class)
  abstract Optional<Charset> charset();

  @PositionalParameter(position = 1)
  abstract Path input();

  @PositionalParameter(position = 2)
  abstract Optional<Path> output();

  static void run(RemoveSingleLinebreaks args) throws IOException {
    Path output = args.output().orElse(args.input().resolveSibling(args.input().getFileName() + "_output.txt"));
    output.toFile().delete();
    try (BufferedReader in = Files.newBufferedReader(args.input(), args.charset().orElse(StandardCharsets.UTF_8));
         PrintWriter out = new PrintWriter(Files.newBufferedWriter(output))) {
      String line;
      StringBuilder sb = new StringBuilder();
      while ((line = in.readLine()) != null) {
        if (line.isEmpty()) {
          out.println(sb);
          out.println("");
          sb = new StringBuilder();
        } else {
          if (line.startsWith(" ") || line.startsWith("\t")) {
            if (sb.length() >= 1) {
              out.println(sb);
            }
            out.println(line);
            sb = new StringBuilder();
          } else {
            sb.append(line).append(" ");
          }
        }
      }
    }
  }

  static class CharsetMapper implements Supplier<Function<String, Charset>> {

    @Override
    public Function<String, Charset> get() {
      return Charset::forName;
    }
  }
}
