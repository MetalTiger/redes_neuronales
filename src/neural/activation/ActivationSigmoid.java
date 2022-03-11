package neural.activation;

import neural.util.BoundNumbers;

/**
 * ActivationSigmoid: The sigmoid activation function takes on a
 * sigmoidal shape.  Only positive numbers are generated.  Do not
 * use this activation function if negative number output is desired.
 *
 */
public class ActivationSigmoid implements ActivationFunction {

    /**
     * A threshold function for a neural network.
     * @param d The input to the function.
     * @return The output from the function.
     */
    public double activationFunction(final double d) {
        return 1.0 / (1 + BoundNumbers.exp(-1.0 * d));
    }

    /**
     * Some training methods require the derivative.
     * @param d The input.
     * @return The output.
     */
    public double derivativeFunction(double d) {
        return d*(1.0-d);
    }
}
