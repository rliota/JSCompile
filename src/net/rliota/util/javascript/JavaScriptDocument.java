package net.rliota.util.javascript;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Admin on 2/1/14.
 */
public class JavaScriptDocument {

    private ArrayList<String> imports = null;
    private String source = null;

    public JavaScriptDocument(File jsFile) throws FileNotFoundException, IOException{
        Reader inputStream = new FileReader(jsFile);
        StringBuilder js = new StringBuilder();
        int charIn;
        while((charIn = inputStream.read()) != -1){
            char input = (char)charIn;
            js.append(input);
        }
        CommentParser stripper = new CommentParser(js);
        imports = stripper.getImports();
        String reducedSizeJS = stripper.getStrippedJS();
        reducedSizeJS.split("");
    }

}
