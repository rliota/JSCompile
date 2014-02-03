package net.rliota.util.javascript;

/**
 * Created by Admin on 2/1/14.
 */
public class JSString {
    private StringBuilder contents = new StringBuilder();
    private int startIndex = -1;
    private int endIndex = -1;

    public JSString(){

    }

    public void setStartIndex(int start){
        this.startIndex = start;
    }

    public void setEndIndex(int end){
        this.endIndex = end;
    }

    public void append(String input){
        contents.append(input);
    }

    public String toString(){
        return contents.toString();
    }

}
