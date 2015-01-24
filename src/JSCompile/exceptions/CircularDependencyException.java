package JSCompile.exceptions;

public class CircularDependencyException extends RuntimeException{

    public CircularDependencyException(){}

    public CircularDependencyException(String message){
        super(message);
    }

}
