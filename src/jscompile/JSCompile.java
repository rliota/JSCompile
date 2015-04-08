package jscompile;


import jscompile.util.ProjectProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JSCompile {

    public static void main(String[] args) throws IOException{
        String projectPath = args[0];
        String outputPath = args[1];


        File rootDir = new File(projectPath);
        if(rootDir.isDirectory()){
            String projectName = null;
            if(args.length>2){
                projectName = args[2];
            }else{
                projectName = rootDir.getName();
            }
            ProjectProcessor processor = new ProjectProcessor(rootDir);
            String compiledJS = processor.getCompiledProject(projectName);

            FileWriter writer = new FileWriter(outputPath);
            writer.write(compiledJS);
            writer.close();

        }

    }
}
