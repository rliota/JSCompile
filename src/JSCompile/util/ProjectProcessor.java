package jscompile.util;


import jscompile.domain.JSArtifact;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProjectProcessor {

    private static final String REGEX_DOT = "\\.";

    private ArrayList<String> namespaces = new ArrayList<String>();
    private HashMap<String, JSArtifact> artifacts = new HashMap<String, JSArtifact>();

    public ProjectProcessor(File projectRootDir) throws IOException{
        processDirectory(projectRootDir, null);
    }

    public String getCompiledProject(String projectName){
        ArtifactCompiler compiler = new ArtifactCompiler();
        return compiler.compile(artifacts, namespaces, projectName);
    }

    private void processJSFile(File file, String namespace, String name) throws IOException{
        FileReader inputStream = new FileReader(file);
        StringBuilder js = new StringBuilder();
        int charIn;
        while((charIn = inputStream.read()) != -1){
            js.append((char)charIn);
        }
        inputStream.close();
        CommentParser stripper = new CommentParser(js);

        JSArtifact artifact = new JSArtifact();
        artifact.dependencies = stripper.getImports();
        artifact.setSource(stripper.getStrippedJS());
        artifact.setNamespace(namespace);
        artifact.setName(name);
        artifacts.put(artifact.getFullName(), artifact);
        //namespaces.add(namespace + "." + name);
    }

    public void processFile(File file, String namespace) throws IOException{
        String fileName = file.getName();
        String fileExtension = "";

        String[] nameParts = fileName.split(REGEX_DOT);
        if(nameParts.length > 0){
            if(nameParts.length>1){
                fileExtension = nameParts[nameParts.length-1];
            }
        }

        if(fileExtension.equalsIgnoreCase("js")){
            String artifactName = fileName.substring(0, fileName.length()-3);
            processJSFile(file, namespace, artifactName.replace('.', '_'));
        }
    }
       
    
    public void processDirectory(File dir, String parentNamespace) throws IOException{
        String namespace = (parentNamespace != null ? (parentNamespace + ".") : "") + dir.getName().trim();

        namespaces.add(namespace);

        File[] children = dir.listFiles();
        if(children != null){
            for(File child : children){
                if(child.isDirectory()){
                    processDirectory(child, namespace);
                }else if(child.isFile()){
                    processFile(child, namespace);
                }
            }
        }
    }

}
