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
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CommandLineArguments
abstract class RemoveSingleLinebreaks {

  @Parameter(longName = "charset",
      mappedBy = CharsetMapper.class)
  abstract Optional<Charset> charset();

  @PositionalParameter(position = 1)
  abstract Path input();

  static void run(RemoveSingleLinebreaks args) throws IOException {
    Path input = args.input();
    Charset charset = args.charset().orElse(StandardCharsets.UTF_8);
    if (input.toFile().isDirectory()) {
      Path outputDir = input.resolveSibling(input.getFileName() + "_output");
      outputDir.toFile().mkdirs();
      try (Stream<Path> pathStream = Files.find(input, Integer.MAX_VALUE, (path, attr) -> attr.isRegularFile())) {
        List<Path> inputs = pathStream
            .filter(p -> p.getFileName().toString().endsWith(".txt"))
            .collect(Collectors.toList());
        for (Path file : inputs) {
          doFile(charset, file, outputDir.resolve(file.getFileName()));
        }
      }
    } else {
      doFile(charset, input, input.resolveSibling(input.getFileName() + "_output.txt"));
    }
  }

  private static void doFile(Charset charset, Path input, Path output) throws IOException {
    output.toFile().delete();
    output.getParent().toFile().mkdirs();
    try (BufferedReader in = Files.newBufferedReader(input, charset);
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
