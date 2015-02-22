package jscompileRENAME.exceptions;


public class UnknownImportException extends RuntimeException {

    public UnknownImportException(){}

    public UnknownImportException(String message){
        super(message);
    }
}
