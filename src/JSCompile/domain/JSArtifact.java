package JSCompile.domain;

import java.util.ArrayList;

public class JSArtifact {

    public ArrayList<String> dependencies = new ArrayList<String>();

    private String name = null;
    private String namespace = null;

    private String source = null;

    public JSArtifact(){
        this.source = "";
    }

    public JSArtifact(String source){
        this.source = source;
    }

    public String getFullName() {
        return namespace + "." + name;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getNamespace(){
        return this.namespace;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
