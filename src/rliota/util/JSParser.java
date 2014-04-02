package rliota.util;


import java.io.*;
import java.util.*;

public class JSParser {

    private static final char NEW_LINE = '\n';
    private static final String REGEX_DOT = "\\.";
    private static final String SEPARATOR = File.separator;

    private FileWriter outputFile = null;
    private HashMap<String, File> namespaceResourceMap = new HashMap<String, File>();
    private ArrayList<String> pathsToIgnore = new ArrayList<String>();

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

            String[] pathStr = destinationPath.split(SEPARATOR);
            String functionName = pathStr[pathStr.length-1];
            functionName = functionName.split("[.]")[0];
            String header = "//Generated on " + new Date() + "\nfunction initialize_" + functionName + "(){";
            this.outputFile.write(header + NEW_LINE + "window." + namespace + " = {};");
            this.processDirectory(rootDir, namespace);
            this.assembleJSFiles();
            this.outputFile.write("}");
            this.outputFile.close();
        }else{
            System.out.println("Source path \"" + rootPath + "\" is not a directory.");
        }
    }

    private boolean shouldIgnore(File f)throws IOException{
        for(String path : pathsToIgnore){
            if(f.getCanonicalPath().equals(path)){
                return true;
            }
        }
        return false;
    }

    private void processDirectory(File dir, String parentNamespace) throws IOException{
        if(dir != null){
            File[] children = dir.listFiles();
            if(children != null){
                for(File child : children){
                    if(child.isDirectory() && !shouldIgnore(child) ){
                        String namespace = parentNamespace +"."+ child.getName();
                        this.outputFile.write(NEW_LINE + namespace + " = {};");
                        processDirectory(child, namespace);
                    }else if(child.isFile() && !shouldIgnore(child)){
                        String[] childName = child.getName().split(REGEX_DOT);
                        if(childName.length>1){
                            if(childName[1].equals("js")){
                                String namespace = parentNamespace + "." + childName[0];
                                this.namespaceResourceMap.put(namespace, child);
                            }
                        }
                    }
                }
            }
        }
    }

    private void processJSFile(File jsFile) throws IOException{
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

    private void assembleJSFiles() throws IOException{
        Collection<File> jsFileCollection = this.namespaceResourceMap.values();
        for(File jsFile : jsFileCollection){
            processJSFile(jsFile);
        }
    }



}
