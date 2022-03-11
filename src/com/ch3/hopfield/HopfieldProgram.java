package com.ch3.hopfield;

import neural.hopfield.HopfieldNetwork;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * Chapter 3: Using a Hopfield Neural Network
 * <p>
 * HopfieldApplet: Applet that allows you to work with a Hopfield
 * network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class HopfieldProgram extends JFrame implements ActionListener, FocusListener {

    static int tam = 5;

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 6244453822526901486L;
    HopfieldNetwork network = new HopfieldNetwork(tam);

    Label label1 = new Label();
    // Matriz de pesos
    TextField[][] matrix = new TextField[tam][tam];
    // Input
    Choice[] input = new Choice[tam];
    // Output
    TextField[] output = new TextField[tam];
    Label label2 = new Label();
    Label label3 = new Label();
    Button go = new Button();
    Button train = new Button();
    Button clear = new Button();

    /**
     * Called when the user clicks one of the buttons.
     *
     * @param event The event.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {

        final Object object = event.getSource();

        if (object == this.go) {
            runNetwork();
        } else if (object == this.clear) {
            clear();
        } else if (object == this.train) {
            train();
        }

    }

    /**
     * Clear the neural network.
     */
    void clear() {
        this.network.getMatrix().clear();
        this.setMatrixValues();
    }

    /**
     * Collect the matrix values from the applet and place inside the weight
     * matrix for the neural network.
     */
    private void collectMatrixValues() {

        for (int row = 0; row < tam; row++) {

            for (int col = 0; col < tam; col++) {

                final String str = this.matrix[row][col].getText();

                int value = 0;

                try {

                    value = Integer.parseInt(str);

                } catch (final NumberFormatException e) {
                    // let the value default to zero,
                    // which it already is by this point.
                }

                // do not allow neurons to self-connect
                if (row == col) {

                    this.network.getMatrix().set(row, col, 0);

                } else {

                    this.network.getMatrix().set(row, col, value);

                }

            }
        }

    }

    @Override
    public void focusGained(final FocusEvent e) {
        // don't care

    }

    @Override
    public void focusLost(final FocusEvent e) {
        this.collectMatrixValues();
        this.setMatrixValues();
    }

    /**
     * Setup the applet.
     */
    public HopfieldProgram() {


        setLayout(null);

        //Tamaño de la ventana
        setBounds(10, 10, 600, 600);

        //Título
        setTitle("Hopfield RN");

        //Cerrar proceso al cerrar la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setBackground(Color.lightGray);

        this.label1.setText("Enter the activation weight matrix:");
        add(this.label1);
        this.label1.setBounds(24, 12, 240, 12);

        for (int row = 0; row < tam; row++) {

            for (int col = 0; col < tam; col++) {

                this.matrix[row][col] = new TextField();
                add(this.matrix[row][col]);

                this.matrix[row][col].setBounds(
                        24 + (col * 60),
                        36 + (row * 38),
                        48,
                        24
                );

                this.matrix[row][col].setText("0");
                this.matrix[row][col].addFocusListener(this);

                if (row == col) {

                    this.matrix[row][col].setEnabled(false);

                }

            }

        }


        this.label2.setText("Input pattern to run or train:");
        add(this.label2);
//        this.label2.setBounds(24, 180, 192, 12);
        this.label2.setBounds(24, 222, 192, 12);


        int alturaIC = 230;

        for (int i = 0; i < tam; i++) {

            // Choices
            this.input[i] = new Choice();
            this.input[i].add("0");
            this.input[i].add("1");
            this.input[i].select(0);
            add(this.input[i]);
            this.input[i].setBounds(24 + (i * 60), alturaIC, 48, 24);

            // Outputs
            this.output[i] = new TextField();
            this.output[i].setEditable(false);
            this.output[i].setText("0");
            this.output[i].setEnabled(true);
            add(this.output[i]);
            this.output[i].setBounds(24 + (i * 60), alturaIC + 100, 48, 24);


        }

        this.label3.setText("The output is:");
        add(this.label3);
        this.label3.setBounds(24, 276, 192, 12);

        this.go.setLabel("Run");
        add(this.go);
        this.go.setBackground(Color.lightGray);
        this.go.setBounds(10, 240, 50, 24);

        this.train.setLabel("Train");
        add(this.train);
        this.train.setBackground(Color.lightGray);
        this.train.setBounds(110, 240, 50, 24);

        this.clear.setLabel("Clear");
        add(this.clear);
        this.clear.setBackground(Color.lightGray);
        this.clear.setBounds(210, 240, 50, 24);

        this.go.addActionListener(this);
        this.clear.addActionListener(this);
        this.train.addActionListener(this);

        setVisible(true);

    }

    /**
     * Collect the input, present it to the neural network, then display the
     * results.
     */
    void runNetwork() {

        final boolean[] pattern = new boolean[tam];

        // Read the input into a boolean array.
        for (int row = 0; row < tam; row++) {

            final int i = this.input[row].getSelectedIndex();

            if (i == 0) {
                pattern[row] = false;
            } else {
                pattern[row] = true;
            }

        }

        // Present the input to the neural network.
        final boolean[] result = this.network.present(pattern);

        // Display the result.
        for (int row = 0; row < tam; row++) {

            if (result[row]) {
                this.output[row].setText("1");
            } else {
                this.output[row].setText("0");
            }

            // If the result is different than the input, show in yellow.
            if (result[row] == pattern[row]) {
                this.output[row].setBackground(java.awt.Color.white);
            } else {
                this.output[row].setBackground(java.awt.Color.yellow);
            }
        }

    }

    /**
     * Set the matrix values on the applet from the matrix values stored in the
     * neural network.
     */
    private void setMatrixValues() {

        for (int row = 0; row < tam; row++) {

            for (int col = 0; col < tam; col++) {

                this.matrix[row][col].setText("" + (int) this.network.getMatrix().get(row, col));

            }

        }
    }

    /**
     * Called when the train button is clicked. Train for the current pattern.
     */
    void train() {

        final boolean[] booleanInput = new boolean[tam];

        // Collect the input pattern.
        for (int x = 0; x < tam; x++) {
            booleanInput[x] = (this.input[x].getSelectedIndex() != 0);
        }

        // Train the input pattern.
        this.network.train(booleanInput);
        this.setMatrixValues();

    }

    public static void main(String[] args) {

        new HopfieldProgram();

    }

}