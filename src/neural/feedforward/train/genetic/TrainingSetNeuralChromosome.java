package neural.feedforward.train.genetic;

import neural.exception.NeuralNetworkError;
import neural.feedforward.FeedforwardNetwork;

/**
 * TrainingSetNeuralChromosome: Implements a chromosome that
 * allows a feedforward neural network to be trained using a
 * genetic algorithm.  The network is trained using training
 * sets.
 *
 * The chromosome for a feed forward neural network
 * is the weight and threshold matrix.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TrainingSetNeuralChromosome extends NeuralChromosome<TrainingSetNeuralGeneticAlgorithm> {

    public TrainingSetNeuralChromosome(
            final TrainingSetNeuralGeneticAlgorithm genetic,
            final FeedforwardNetwork network) throws NeuralNetworkError {
        this.setGeneticAlgorithm(genetic);
        this.setNetwork(network);

        initGenes(network.getWeightMatrixSize());
        updateGenes();
    }

    @Override
    public void calculateCost() throws NeuralNetworkError {
        // update the network with the new gene values
        this.updateNetwork();

        // update the cost with the new genes
        final double[][] input = this.getGeneticAlgorithm().getInput();
        final double[][] ideal = this.getGeneticAlgorithm().getIdeal();

        setCost(getNetwork().calculateError(input, ideal));

    }

    /**
     * Set all genes.
     *
     * @param list
     *            A list of genes.
     * @throws NeuralNetworkError
     */
    @Override
    public void setGenes(final Double[] list) throws NeuralNetworkError {

        // copy the new genes
        super.setGenes(list);

        calculateCost();

    }
}
