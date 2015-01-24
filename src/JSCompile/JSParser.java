package JSCompile;


import java.io.*;
import java.util.*;

public class JSParser {

    private static final char NEW_LINE = '\n';
    private static final String REGEX_DOT = "\\.";
    private static final String SEPARATOR = "[\\/]";
    private static final String INJECTION_HEADER_START = "\n(function(";
    private static final String INJECTION_HEADER_END = "){\n";
    private static final String INJECTION_FUNCTION_END = "}(\n";
    private static final String INJECTION_FOOTER_START = "";
    private static final String INJECTION_FOOTER_END = ");\n";

    private FileWriter outputFile = null;
    private HashMap<String, File> namespaceResourceMap = new HashMap<String, File>();
    private ArrayList<String> pathsToIgnore = new ArrayList<String>();

    private ArrayList<File> processedFiles = new ArrayList<File>();

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
            String namespace = rootDir.getName().trim();

            String[] pathStr = destinationPath.split(SEPARATOR);
            String functionName = pathStr[pathStr.length-1];
            functionName = functionName.split("[.]")[0];
            String header = "//Generated on " + new Date() + "\nfunction initialize_" + functionName + "(){";
            this.outputFile.write(header + NEW_LINE);
            //this.outputFile.write("\nvar __define;");
            this.outputFile.write("\nvar " + namespace + " = {};");
            this.processDirectory(rootDir, namespace);
            this.assembleJSFiles();
            this.outputFile.write("return " + namespace + ";");
            this.outputFile.write("\n}");
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

    private String getObjectDefinitionHeader(String namespace, ArrayList<String> imports){
        StringBuilder js = new StringBuilder();
        js.append(NEW_LINE);
        js.append(namespace).append(" = (function(");
        String delim = "";
        for(String i : imports){
            String[] impArr = i.split(REGEX_DOT);
            if(impArr.length > 0){
                String imp = impArr[impArr.length-1];
                js.append(delim).append(imp);
                delim = ", ";
            }
        }
        js.append("){\n");
        return js.toString();
    }

    private String getObjectDefinitionFooter(String objectName, ArrayList<String> imports){
        StringBuilder js = new StringBuilder();
        js.append("return ").append(objectName).append(";\n");
        js.append("})(");
        String delim = "";
        for(String i : imports){
            js.append(delim).append(i);
            delim = ", ";
        }
        js.append(");\n");
        return js.toString();
    }

    private String getInjectionHeader(ArrayList<String> imports){
        StringBuilder js = new StringBuilder();
        js.append(INJECTION_HEADER_START);
        String delim = "";
        for(String i : imports){
            String[] impArr = i.split(REGEX_DOT);
            if(impArr.length > 0){
                String imp = impArr[impArr.length-1];
                js.append(delim).append(imp);
                delim = ", ";
            }
        }
        js.append(INJECTION_HEADER_END);
        js.append(NEW_LINE);
        return js.toString();
    }

    private String getInjectionFooter(String objectName, String namespace, ArrayList<String> imports){
        StringBuilder js = new StringBuilder();
        js.append(NEW_LINE);
        js.append("return ").append(objectName).append(";\n");
        js.append(INJECTION_FUNCTION_END);
        js.append(namespace).append(" = ");
        js.append(INJECTION_FOOTER_START);
        String delim = "";
        for(String i : imports){
            js.append(delim).append(i);
            delim = ", ";
        }
        js.append(INJECTION_FOOTER_END);
        return js.toString();
    }

    private void processJSFile(File jsFile) throws IOException{
        processedFiles.add(jsFile);
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
            if(requisiteJS != null && !processedFiles.contains(requisiteJS)){

                processJSFile(requisiteJS);
            }
        }
        if(!shouldIgnore(jsFile)){
            String namespace = jsFile.getPath().split(REGEX_DOT)[0];
            namespace = namespace.replaceAll(SEPARATOR, ".");
            String[] objectNameArr = jsFile.getName().split(REGEX_DOT);


            outputFile.write(getObjectDefinitionHeader(namespace, comments));
            outputFile.write(stripper.getStrippedJS());
            outputFile.write(getObjectDefinitionFooter(objectNameArr[0], comments));
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
