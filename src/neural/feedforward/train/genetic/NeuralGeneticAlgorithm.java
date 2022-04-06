package neural.feedforward.train.genetic;

import neural.feedforward.FeedforwardNetwork;
import neural.genetic.GeneticAlgorithm;

/**
 * NeuralGeneticAlgorithm: Implements a genetic algorithm that
 * allows a feedforward neural network to be trained using a
 * genetic algorithm.  This algorithm is for a feed forward neural
 * network.
 *
 * This class is abstract.  If you wish to train the neural
 * network using training sets, you should use the
 * TrainingSetNeuralGeneticAlgorithm class.  If you wish to use
 * a cost function to train the neural network, then
 * implement a subclass of this one that properly calculates
 * the cost.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class NeuralGeneticAlgorithm<GA_TYPE extends GeneticAlgorithm<?>>
        extends GeneticAlgorithm<NeuralChromosome<GA_TYPE>> {

    /**
     * Get the current best neural network.
     * @return The current best neural network.
     */
    public FeedforwardNetwork getNetwork() {
        final NeuralChromosome<GA_TYPE> c = getChromosome(0);
        c.updateNetwork();
        return c.getNetwork();
    }

}
