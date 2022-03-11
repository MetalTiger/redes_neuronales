package neural.exception;

public class NeuralNetworkError extends RuntimeException {

    /**
     * Construct a message exception.
     *
     * @param msg
     *            The exception message.
     */
    public NeuralNetworkError(final String msg) {
        super(msg);
    }

    /**
     * Construct an exception that holds another exception.
     *
     * @param t
     *            The other exception.
     */
    public NeuralNetworkError(final Throwable t) {
        super(t);
    }

}
