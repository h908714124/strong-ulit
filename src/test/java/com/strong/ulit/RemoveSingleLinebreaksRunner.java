package com.strong.ulit;

import java.io.IOException;

class RemoveSingleLinebreaksRunner {

  public static void main(String[] args) throws IOException {
    RemoveSingleLinebreaks_Parser parser = RemoveSingleLinebreaks_Parser.create();
    RemoveSingleLinebreaks.run(parser.parseOrExit(args));
  }
}