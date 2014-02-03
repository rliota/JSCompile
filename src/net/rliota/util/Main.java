package net.rliota.util;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
        String input = "/Users/Admin/IdeaProjects/Portfolio/js/example";
        String output = "/Users/Admin/IdeaProjects/JSCompile/src/test.js";
        JSParser jsP = new JSParser();
        jsP.compile(input, output);
    }
}
