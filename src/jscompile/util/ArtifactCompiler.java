package jscompile.util;

import jscompile.domain.JSArtifact;
import jscompile.exceptions.CircularDependencyException;
import jscompile.exceptions.UnknownImportException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ArtifactCompiler {

    private HashMap<String, JSArtifact> artifactsAwaitingProcessing = new HashMap<String, JSArtifact>();
    private HashMap<String, JSArtifact> processedArtifacts = new HashMap<String, JSArtifact>();
    private HashMap<String, JSArtifact>  artifactsToProcess = null;
    private Concatenator concatenator;

    public void processArtifact(JSArtifact artifact){

        if( ! processedArtifacts.containsKey(artifact.getFullName())){

            /**
             * (We don't ever clean this flag up because once an artifact is
             * processed it should never be able to reach this check.)
             */
            if(artifactsAwaitingProcessing.containsKey(artifact.getFullName())){
                throw new CircularDependencyException(
                        "Please check "+artifact.getFullName()+"'s dependency chain for issues and try compiling again.");
            }

            /**
             * Mark this artifact as being processed so that if an attempt is made to
             * import it again from its own dependency chain, we can inform the user
             * about the circular dependency.
             */
            artifactsAwaitingProcessing.put(artifact.getFullName(), artifact);

            ArrayList<String> dependencies = artifact.dependencies;
            for(String dependency : dependencies){

                // if the artifact hasn't yet been processed
                if( ! processedArtifacts.containsKey(dependency) ){
                    JSArtifact nextArtifact = artifactsToProcess.get(dependency);
                    if(nextArtifact == null){
                        throw new UnknownImportException("Couldn't find the file representing the imported object \""+dependency+"\" in the project path. Dependent file: "+artifact.getFullName());
                    }
                    processArtifact(nextArtifact);
                }
            }

            concatenator.add(artifact);
            processedArtifacts.put(artifact.getFullName(), artifact);

        }

    }

    public String compile(HashMap<String, JSArtifact> artifacts, ArrayList<String> namespaces, String packageName){
        this.concatenator = new Concatenator(packageName, namespaces);
        this.artifactsToProcess = artifacts;
        Collection<JSArtifact> artifactList = artifacts.values();
        for(JSArtifact artifact : artifactList){
            if( ! processedArtifacts.containsKey(artifact.getFullName()) ){
                processArtifact(artifact);
            }
        }
        return this.concatenator.export();
    }

}
