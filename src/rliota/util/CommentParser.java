package rliota.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Admin on 2/1/14.
 */
public class CommentParser {

    private ArrayList<String> comments = new ArrayList<String>();
    private static final char SLASH = '/';
    private static final char APOSTROPHE = '\'';
    private static final char QUOTE = '\"';
    private static final char SPLAT = '*';
    private static final char ESCAPE = '\\';

    private static final char NEW_LINE = '\n';

    private int slashes = 0;
    private int splats = 0;

    private StringBuilder jsString = null;
    private int currentIndex = 0;

    private static final Pattern IMPORT_PATTERN = Pattern.compile("@import\\s[^\\s]+");

    public CommentParser(StringBuilder jsString){
        this.jsString = jsString;
        delegateTraversal();
    }

    public ArrayList<String> getComments(){
        return this.comments;
    }

    public String getStrippedJS(){
        return jsString.toString();
    }

    private void resetState(){
        slashes = 0;
        splats = 0;
    }

    private void delegateTraversal(){
        char current;
        while(currentIndex<jsString.length()){
            current = jsString.charAt(currentIndex++);
            if(current == SLASH){
                slashes++;
                if(slashes == 2){
                    currentIndex -= 2;
                    traverseLineComment();
                    resetState();
                }
            }else if(current == SPLAT){
                splats++;
                if(splats == 1 && slashes ==1){
                    currentIndex -= 2;
                    traverseBlockComment();
                    resetState();
                }
            }else{
                resetState();
                if(current == APOSTROPHE){
                    traverseCharArray();
                }else if(current == QUOTE){
                    traverseString();
                }
            }
        }
    }

    private void traverseString(){
        boolean precedingEscape = false;
        char next = ' '; // initialize to valid char
        while(currentIndex<jsString.length()){
            next = jsString.charAt(currentIndex++);
            if(next == QUOTE && !precedingEscape){
                break;
            }
            if(next == ESCAPE){
                precedingEscape = true;
            }
        }
        delegateTraversal();
    }

    private void traverseCharArray(){
        boolean precedingEscape = false;
        char next = ' '; // initialize to valid char
        while(currentIndex<jsString.length()){
            next = jsString.charAt(currentIndex++);
            if(next == APOSTROPHE && !precedingEscape){
                break;
            }
            if(next == ESCAPE){
                precedingEscape = true;
            }
        }
        delegateTraversal();
    }

    private void traverseLineComment(){
        int commentStart = currentIndex;
        String comment = "";
        char next = ' ';
        while(currentIndex<jsString.length()){
            next = jsString.charAt(currentIndex++);
            comment += next;
            if(next == NEW_LINE){
                break;
            }
        }
        resetState();
        comments.add(comment);
        jsString.replace(commentStart, currentIndex, "");
        currentIndex = commentStart;
        delegateTraversal();
    }

    private void traverseBlockComment() {
        int commentStart = currentIndex;
        String comment = "";
        boolean precedingSplat = false;
        char next;
        while(currentIndex < jsString.length()){
            next = jsString.charAt(currentIndex++);
            comment += next;
            if(next == SLASH && precedingSplat){
                break;
            }
            precedingSplat = (next == SPLAT);
        }
        resetState();
        comments.add(comment);
        jsString.replace(commentStart, currentIndex, "");
        currentIndex = commentStart;
        delegateTraversal();
    }

    public ArrayList<String> getImports(){
        ArrayList<String> importList = new ArrayList<String>();
        for(String comment : comments){
            ArrayList<String> imports = parseImports(comment);
            for(String importResource : imports){
                importList.add(importResource);
            }
        }
        return importList;
    }

    private ArrayList<String> parseImports(String comment){
        ArrayList<String> importList = new ArrayList<String>();
        if(comment.contains("@import")){
            Matcher m = IMPORT_PATTERN.matcher(comment);
            while(m.find()){
                String importStr = comment.substring(m.start(),m.end());
                importStr = importStr.split("@import ")[1];
                importList.add(importStr);
            }
        }
        return importList;
    }

}
