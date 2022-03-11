package neural.exception;

public class MatrixError extends RuntimeException {
    /**
     * Construct this neural.exception with a message.
     * @param message The other neural.exception.
     */
    public MatrixError(final String message) {
        super(message);
    }

    /**
     * Construct this neural.exception with another neural.exception.
     * @param t The other neural.exception.
     */
    public MatrixError(final Throwable t) {
        super(t);
    }
}
