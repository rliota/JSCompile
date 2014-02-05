package net.rliota.util;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException{
        String input = args[0];
        String output = args[1];
        JSParser jsP = new JSParser();
        jsP.compile(input, output);
    }
}
