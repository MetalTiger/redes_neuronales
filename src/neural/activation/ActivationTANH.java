package neural.activation;

import neural.util.BoundNumbers;

/**
 * ActivationTANH: The hyperbolic tangent activation function takes the
 * curved shape of the hyperbolic tangent.  This activation function produces
 * both positive and negative output.  Use this activation function if
 * both negative and positive output is desired.
 *
 */
public class ActivationTANH implements ActivationFunction {
    /**
     * A threshold function for a neural network.
     * @param d The input to the function.
     * @return The output from the function.
     */
    public double activationFunction(double d) {
        final double result = (BoundNumbers.exp(d*2.0)-1.0)/(BoundNumbers.exp(d*2.0)+1.0);
        return result;
    }

    /**
     * Some training methods require the derivative.
     * @param d The input.
     * @return The output.
     */
    public double derivativeFunction(double d) {
        return( 1.0-Math.pow(activationFunction(d), 2.0) );
    }
}
