package neural.feedforward.train;

import neural.feedforward.FeedforwardNetwork;

/**
 * Train: Interface for all feedforward neural network training
 * methods.  There are currently three training methods define:
 *
 * Backpropagation
 * Genetic Algorithms
 * Simulated Annealing
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public interface Train {

    /**
     * Get the current error percent from the training.
     * @return The current error.
     */
    double getError();

    /**
     * Get the current best network from the training.
     * @return The best network.
     */
    FeedforwardNetwork getNetwork();

    /**
     * Perform one iteration of training.
     */
    void iteration();


}
