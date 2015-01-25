package JSCompile;


import JSCompile.util.ProjectProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSCompile {

    public static void main(String[] args) throws IOException{
        String projectPath = args[0];
        String outputPath = args[1];

        String projectName = null;
        if(args.length>2){
            projectName = args[2];
        }else{

            String[] outputPathParts = outputPath.split("\\.js");
            if(outputPathParts.length>0){
                outputPathParts = outputPathParts[0].split("[\\/]");
                if(outputPathParts.length > 0){
                    projectName = outputPathParts[outputPathParts.length-1];
                }
            }
        }
        if(projectName == null){
            projectName = "UntitledJSProject";
        }

        File rootDir = new File(projectPath);
        if(rootDir.isDirectory()){
            ProjectProcessor processor = new ProjectProcessor(rootDir);
            String compiledJS = processor.getCompiledProject(projectName);

            FileWriter writer = new FileWriter(outputPath);
            writer.write(compiledJS);
            writer.close();

        }

    }
}
