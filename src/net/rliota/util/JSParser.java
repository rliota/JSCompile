package net.rliota.util;


import net.rliota.util.javascript.CommentParser;

import javax.script.ScriptEngine;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Admin on 2/1/14.
 */
public class JSParser {

    private static final char NEW_LINE = '\n';
    private static final String REGEX_DOT = "\\.";
    private static final Pattern COMMENT_START = Pattern.compile("/\\/\\*\\*(.*)/");


    private FileWriter outputFile = null;
    private HashMap<String, File> namespaceResourceMap = new HashMap<String, File>();
    private ArrayList<String> pathsToIgnore = new ArrayList<String>();
    private ScriptEngine jsParser = null;

    public void compile(String srcPath, String destinationPath)throws IOException{
        this.compile(srcPath, destinationPath, null);
    }

    public void compile(String rootPath, String destinationPath, ArrayList<String> pathsToIgnore) throws IOException{
        File rootDir = new File(rootPath);
        if(rootDir.isDirectory()){
            if(pathsToIgnore != null){
                this.pathsToIgnore = pathsToIgnore;
            }
            this.outputFile = new FileWriter(destinationPath);
            String namespace = rootDir.getName().trim().toUpperCase();
            this.outputFile.write(NEW_LINE + namespace + " = {};");
            this.processDirectory(rootDir, namespace);
            this.assembleJSFiles();
            this.outputFile.close();
        }else{
            System.out.println("Source path \"" + rootPath + "\" is not a directory.");
        }
    }

    private boolean shouldIgnore(File f)throws IOException{
        for(String path : pathsToIgnore){
            if(f.getCanonicalPath() == path){
                return true;
            }
        }
        return false;
    }

    private void processDirectory(File dir, String parentNamespace) throws IOException{
        if(dir != null){
            File[] children = dir.listFiles();
            for(File child : children){
                if(child.isDirectory() && !shouldIgnore(child) ){
                    String namespace = parentNamespace +"."+ child.getName();
                    this.outputFile.write(NEW_LINE + namespace + " = {};");
                    processDirectory(child, namespace);
                }else if(child.isFile() && !shouldIgnore(child)){
                    String namespace = parentNamespace + "." + child.getName().split(REGEX_DOT)[0];
                    this.namespaceResourceMap.put(namespace, child);
                }
            }
        }
    }
/*
    private void parseImports(File jsFile) throws FileNotFoundException{
        Scanner reader = new Scanner(jsFile);
        int readIndex = 0;
        boolean insideString = false;
        boolean insideCharArr = false;
        boolean insideComment = false;
        int slashCount = 0;
        int starCount = 0;
        int starCountDown = 0;
        int slashCountDown = 0;
        boolean annotationMode = true;
        String annotationCommand = "";
        boolean importStatementMode = false;
        String importStatment = "";
        while(reader.hasNext()){
            String nextChar = reader.next();
            if(importStatementMode){
                if(nextChar.equals(" ") || nextChar.equals(NEW_LINE)){
                    parseImportCommand(importStatment);
                }else{
                    importStatment += nextChar;
                }
            }else if(slashCount == 2){ // found a line comment
                if(annotationMode){
                    if(nextChar.equals(" ")){
                        annotationMode = false;
                        importStatementMode = true;
                    }else{
                        annotationCommand = annotationCommand + nextChar;
                    }
                }else if(nextChar.equals("@")){
                    annotationMode = true;
                }
            }else if(slashCountDown == 1 && starCountDown == 1){ // block comment has ended, go back to parsing
                slashCount = 0;
                slashCountDown = 0;
                starCount = 0;
                starCountDown = 0;
                annotationMode = false;
            }else if(slashCount == 1 && starCount == 1){ //inside block comment

            }else{
                if(!insideCharArr && !insideComment &&nextChar.equals("\"")){
                    insideString = !insideString;
                }else if( !insideString && !insideComment && nextChar.equals("'")){
                    insideCharArr = !insideCharArr;
                }else if(!insideCharArr && !insideString && nextChar.equals("/")){
                    slashCount++;
                }else if(!insideCharArr && !insideString && nextChar.equals("*")){
                    starCount++;
                }
            }
        }
    }*/

    private void processJSFile(File jsFile) throws FileNotFoundException, IOException{
        Reader inputStream = new FileReader(jsFile);
        StringBuilder js = new StringBuilder();
        int charIn;
        while((charIn = inputStream.read()) != -1){
            char input = (char)charIn;
            js.append(input);
        }
        CommentParser stripper = new CommentParser(js);

        ArrayList<String> comments = stripper.getImports();
        for(String imp : comments){
            File requisiteJS = namespaceResourceMap.get(imp);
            if(requisiteJS != null){
                processJSFile(requisiteJS);
            }
        }
        if(!shouldIgnore(jsFile)){
            outputFile.write(stripper.getStrippedJS());
            pathsToIgnore.add(jsFile.getCanonicalPath());
        }
    }

    private void assembleJSFiles() throws FileNotFoundException, IOException{
        Collection<File> jsFileCollection = this.namespaceResourceMap.values();
        Iterator<File> jsFiles = jsFileCollection.iterator();
        while (jsFiles.hasNext()){
            processJSFile(jsFiles.next());
        }
    }



}
