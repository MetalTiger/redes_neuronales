package com.ch5;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.Train;
import neural.feedforward.train.backpropagation.Backpropagation;
import neural.matrix.Matrix;

import java.time.Duration;
import java.time.Instant;

public class XOR {

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
        network.reset(); // Inicializa los pesos

        // train the neural network
        final Train train = new Backpropagation(network, XOR_INPUT, XOR_IDEAL, 0.3, 0.7);

        /*
        double[][] weightDataI = {
                {0.4, 0.1},
                {0.2, 0.3},
                {0.01, -0.5}
        };

        double[][] weightDataO = {
                {-0.2},
                {-0.8},
                {0.72}
        };

        Matrix weightMatrixI = new Matrix(weightDataI);
        Matrix weightMatrixO = new Matrix(weightDataO);

        train.getNetwork().getLayers().get(0).setMatrix(weightMatrixI);
        train.getNetwork().getLayers().get(1).setMatrix(weightMatrixO);
        */

        Instant inst1 = Instant.now();

        int epoch = 1;

        do {

            train.iteration();

            System.out.println("Epoch #" + epoch + " Error:" + train.getError());

            epoch++;

        } while (train.getError() > 0.00001); // (epoch <= 800) && (train.getError() > 0.00001)

        Instant inst2 = Instant.now();

        System.out.println("**Datos**");
        System.out.println("Tiempo de entrenamiento: " + Duration.between(inst1, inst2).getSeconds() + "."
                + Duration.between(inst1, inst2).getNano() + " segundos."
        );

        System.out.println("Numero de capas: " + train.getNetwork().getLayers().size());


        System.out.println("Neural Network weights:");

        for (int x = 0; x < train.getNetwork().getLayers().size(); x++){

            Matrix a = train.getNetwork().getLayers().get(x).getMatrix();

            if (a != null){

                //System.out.println("Matriz de " + a.getRows() + " x " + a.getCols());

                for (int i = 0; i < a.getRows(); i++) {

                    for (int j = 0;  j< a.getCols(); j++) {

                        System.out.print(a.get(i, j) + ", ");

                    }

                    System.out.println();

                }

            }

        }

        // test the neural network
        System.out.println("Neural Network Results:");

        for (int i = 0; i < XOR_IDEAL.length; i++) {

            final double[] actual = network.computeOutputs(XOR_INPUT[i]);
            System.out.println(
                    XOR_INPUT[i][0] + "," + XOR_INPUT[i][1]
                    + ", actual=" + actual[0] + ",ideal=" + XOR_IDEAL[i][0]
            );

        }

    }

}
