package com.ch3.console;

import neural.hopfield.HopfieldNetwork;

/**
 * Chapter 3: Using a Hopfield Neural Network
 *
 * ConsoleHopfield: Simple console application that shows how to
 * use a Hopfield Neural Network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class ConsoleHopfield {

    /**
     * Convert a boolean array to the form [T,T,F,F]
     *
     * @param b
     *            A boolen array.
     * @return The boolen array in string form.
     */
    public static String formatBoolean(final boolean b[]) {
        final StringBuilder result = new StringBuilder();
        result.append('[');
        for (int i = 0; i < b.length; i++) {
            if (b[i]) {
                result.append("T");
            } else {
                result.append("F");
            }
            if (i != b.length - 1) {
                result.append(",");
            }
        }
        result.append(']');
        return (result.toString());
    }

    /**
     * A simple main method to test the Hopfield neural network.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String args[]) {

        // Create the neural network.
        final HopfieldNetwork network = new HopfieldNetwork(4);
        // This pattern will be trained
        final boolean[] pattern1 = { true, true, false, false };
        // This pattern will be presented
        final boolean[] pattern2 = { true, false, false, false };
        boolean[] result;

        // train the neural network with pattern1
        System.out.println("Training Hopfield network with: "
                + formatBoolean(pattern1));
        network.train(pattern1);
        // present pattern1 and see it recognized
        result = network.present(pattern1);
        System.out.println("Presenting pattern:" + formatBoolean(pattern1)
                + ", and got " + formatBoolean(result));
        // Present pattern2, which is similar to pattern 1. Pattern 1
        // should be recalled.
        result = network.present(pattern2);
        System.out.println("Presenting pattern:" + formatBoolean(pattern2)
                + ", and got " + formatBoolean(result));

    }

}