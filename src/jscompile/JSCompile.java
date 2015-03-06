package jscompile;


import jscompile.util.ProjectProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


public class JSCompile {


    private static final Logger log = Logger.getLogger("CompilerLog");

    public static void main(String[] args) throws IOException{
        String projectPath = args[0];
        String outputPath = args[1];

        String projectName = null;
        if(args.length>2){
            projectName = args[2];
        }else{
            String[] projectPathParts = projectPath.split("[\\/]");
            if(projectPathParts.length>0){
                projectName = projectPathParts[projectPathParts.length-1];
            }
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
