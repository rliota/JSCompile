package net.rliota.util.javascript;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Admin on 2/1/14.
 */
public class Token {

    private ArrayList<Token> contents = new ArrayList<Token>();
    private int type = 0;

    public static final char STRING_CHAR = '\"';
    public static final char CHARACTER_CHAR = '\'';

    public static final int COMMENT = 1;
    public static final int NUMBER = 2;
    public static final int STRING = 3;
    public static final int VAR = 4;
    public static final int FUNCTION = 5;

    public Token(int type){
        this.type = type;
    }

    public int getType(){
        return this.type;
    }

    public void append(Token t){
        this.contents.add(t);
    }

}

