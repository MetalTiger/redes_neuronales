package com.ch6.TicTacToe.players.neural;

import neural.exception.NeuralNetworkError;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.genetic.NeuralChromosome;
import neural.feedforward.train.genetic.NeuralGeneticAlgorithm;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * TicTacToeGenetic: Use a genetic algorithm to teach a neural network
 * to play Tic-Tac-Toe.  The cost of each chromosome is calculated
 * by playing the neural network against a computer player.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TicTacToeGenetic extends NeuralGeneticAlgorithm<TicTacToeGenetic> {

    private Class<?> opponent;

    public TicTacToeGenetic(final FeedforwardNetwork network,
                            final boolean reset, final int populationSize,
                            final double mutationPercent, final double percentToMate,
                            final Class<?> opponent) throws NeuralNetworkError {

        this.setOpponent(opponent);
        this.setMutationPercent(mutationPercent);
        this.setMatingPopulation(percentToMate * 2);
        this.setPopulationSize(populationSize);
        this.setPercentToMate(percentToMate);

        setChromosomes(new TicTacToeChromosome[getPopulationSize()]);
        for (int i = 0; i < getChromosomes().length; i++) {
            final FeedforwardNetwork chromosomeNetwork = (FeedforwardNetwork) network
                    .clone();
            if (reset) {
                chromosomeNetwork.reset();
            }

            final TicTacToeChromosome c = new TicTacToeChromosome(this,
                    chromosomeNetwork);
            c.updateGenes();
            setChromosome(i, c);
        }
        sortChromosomes();
    }

    /**
     * @return the opponent
     */
    public Class<?> getOpponent() {
        return this.opponent;
    }

    public double getScore() {
        final NeuralChromosome<TicTacToeGenetic> c = getChromosome(0);
        return c.getCost();
    }

    /**
     * @param opponent
     *            the opponent to set
     */
    public void setOpponent(final Class<?> opponent) {
        this.opponent = opponent;
    }

}
