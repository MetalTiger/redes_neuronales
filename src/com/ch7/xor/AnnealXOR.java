package com.ch7.xor;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.anneal.NeuralSimulatedAnnealing;

import java.time.Duration;
import java.time.Instant;

/**
 * Chapter 7: Training using Simulated Annealing
 *
 * XOR: Learn the XOR pattern with a feedforward neural network that
 * uses simulated annealing.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class AnnealXOR {
    public static double[][] XOR_INPUT = {
            { 0.8, -0.2 },
            { 0.7, 0.005 },
            { 1, 0.2 },
            { 0.21, 0.43 },
            { -0.76, 0.55 },
            { 0.12, -0.32 },
    };

    public static double[][] XOR_IDEAL = {
            { 1.0 },
            { 1.0 },
            { 1.0 },
            { 0.0 },
            { 0.0 },
            { 0.0 },
    };

    public static void main(final String[] args) {
        final FeedforwardNetwork network = new FeedforwardNetwork();
        network.addLayer(new FeedforwardLayer(2));
        network.addLayer(new FeedforwardLayer(2));
        network.addLayer(new FeedforwardLayer(1));
        network.reset();

        // train the neural network
        final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(
                network, XOR_INPUT, XOR_IDEAL, 10, 2, 100);

        int epoch = 1;

        Instant inst1 = Instant.now();

        do {

            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;

        } while ((train.getError() > 0.00001)); //(epoch < 100) &&

        // network = train.getNetwork();

        Instant inst2 = Instant.now();

        System.out.println("**Datos**");
        System.out.println("Tiempo de entrenamiento: " + Duration.between(inst1, inst2).getSeconds() + "."
                + Duration.between(inst1, inst2).getNano() + " segundos."
        );

        // test the neural network
        System.out.println("Neural Network Results:");
        for (int i = 0; i < XOR_IDEAL.length; i++) {
            final double[] actual = network.computeOutputs(XOR_INPUT[i]);
            System.out.println(XOR_INPUT[i][0] + "," + XOR_INPUT[i][1]
                    + ", actual=" + actual[0] + ",ideal=" + XOR_IDEAL[i][0]);
        }
    }
}
