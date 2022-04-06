package com.ch6.TicTacToe.players.neural;

import com.ch6.TicTacToe.game.ScorePlayer;
import com.ch6.TicTacToe.players.Player;
import neural.exception.NeuralNetworkError;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.genetic.NeuralChromosome;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * TicTacToeChromosome: Implements a chromosome for the neural network
 * that plays tic-tac-toe.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TicTacToeChromosome extends NeuralChromosome<TicTacToeGenetic> {

    public TicTacToeChromosome(final TicTacToeGenetic genetic,
                               final FeedforwardNetwork network) throws NeuralNetworkError {
        this.setGeneticAlgorithm(genetic);
        this.setNetwork(network);

        initGenes(network.getWeightMatrixSize());
        updateGenes();
    }

    @Override
    public void calculateCost() {

        try {
            // update the network with the new gene values
            this.updateNetwork();

            final PlayerNeural player1 = new PlayerNeural(getNetwork());
            Player player2;

            player2 = (Player) this.getGeneticAlgorithm().getOpponent()
                    .newInstance();
            final ScorePlayer score = new ScorePlayer(player1, player2, false);
            setCost(score.score());
        } catch (final InstantiationException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
