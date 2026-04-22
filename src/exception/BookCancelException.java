package exception;

public class BookCancelException extends RuntimeException {

    public BookCancelException(String message) {
        super(message);
    }

}
