package exception;

public class PastMovieBookingException extends RuntimeException {

    public PastMovieBookingException(String message) {
        super(message);
    }

}