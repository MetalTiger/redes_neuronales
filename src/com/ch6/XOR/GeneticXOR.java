package com.ch6.XOR;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.genetic.TrainingSetNeuralGeneticAlgorithm;

import java.time.Duration;
import java.time.Instant;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * XOR: Learn the XOR pattern with a feedforward neural network that
 * uses a genetic algorithm.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class GeneticXOR {

    public static double[][] XOR_INPUT = {
            { 1.0, 1.0 },
            { 1.0, 0.0 },
            { 0.0, 1.0 },
            { 0.0, 0.0 }
    };

    public static double[][] XOR_IDEAL = {
            { 0.0 },
            { 1.0 },
            { 1.0 },
            { 0.0 }
    };

    public static void main(final String[] args) {

        FeedforwardNetwork network = new FeedforwardNetwork();
        network.addLayer(new FeedforwardLayer(2));
        network.addLayer(new FeedforwardLayer(2));
        network.addLayer(new FeedforwardLayer(1));
        network.reset();

        // train the neural network
        final TrainingSetNeuralGeneticAlgorithm train = new TrainingSetNeuralGeneticAlgorithm(
                network, true, XOR_INPUT, XOR_IDEAL, 5000, 0.1, 0.25);

        Instant inst1 = Instant.now();

        int epoch = 1;

        do {

            train.iteration();

            System.out.println("Epoch #" + epoch + " Error:" + train.getError());

            epoch++;

        } while (train.getError() > 0.00001); //(epoch < 5000) && (train.getError() > 0.001)

        Instant inst2 = Instant.now();

        System.out.println("**Datos**");
        System.out.println("Tiempo de entrenamiento: " + Duration.between(inst1, inst2).getSeconds() + "."
                + Duration.between(inst1, inst2).getNano() + " segundos."
        );

        network = train.getNetwork();

        // test the neural network
        System.out.println("Neural Network Results:");
        for (int i = 0; i < XOR_IDEAL.length; i++) {
            final double[] actual = network.computeOutputs(XOR_INPUT[i]);
            System.out.println(XOR_INPUT[i][0] + "," + XOR_INPUT[i][1]
                    + ", actual=" + actual[0] + ",ideal=" + XOR_IDEAL[i][0]);
        }
    }
}
