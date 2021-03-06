package com.ch8.pruneincremental;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.prune.Prune;

/**
 * Chapter 8: Pruning a Neural Network
 *
 * IncrementalApp: Prune a simple XOR map using the incremental method.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class IncrementalApp extends JFrame implements ActionListener, Runnable {

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 6828318042181226758L;

    /**
     * The number of input neurons.
     */
    protected final static int NUM_INPUT = 2;

    /**
     * The number of output neurons.
     */
    protected final static int NUM_OUTPUT = 1;

    /**
     * The number of hidden neurons.
     */
    protected final static int NUM_HIDDEN = 3;

    /**
     * The learning rate.
     */
    protected final static double RATE = 0.7;

    /**
     * The learning momentum.
     */
    protected final static double MOMENTUM = 0.3;

    /**
     * The main function, just display the JFrame.
     *
     * @param args
     *            No arguments are used.
     */
    public static void main(final String[] args) {
        (new IncrementalApp()).setVisible(true);
    }

    /**
     * The train button.
     */
    JButton btnTrain;

    /**
     * The run button.
     */
    JButton btnRun;

    /**
     * The quit button.
     */
    JButton btnQuit;

    /**
     * The status line.
     */
    JLabel status;

    /**
     * The background worker thread.
     */
    protected Thread worker = null;

    /**
     * The training data that the user enters. This represents the inputs and
     * expected outputs for the XOR problem.
     */
    protected JTextField[][] data = new JTextField[4][4];

    /**
     * The neural network.
     */
    protected FeedforwardNetwork network;

    /**
     * Constructor. Set up the components.
     */
    public IncrementalApp() {
        setTitle("XOR Solution - Incremental Prune");

        this.network = new FeedforwardNetwork();
        this.network.addLayer(new FeedforwardLayer(NUM_INPUT));
        this.network.addLayer(new FeedforwardLayer(NUM_HIDDEN));
        this.network.addLayer(new FeedforwardLayer(NUM_OUTPUT));
        this.network.reset();

        final Container content = getContentPane();

        final GridBagLayout gridbag = new GridBagLayout();
        final GridBagConstraints c = new GridBagConstraints();
        content.setLayout(gridbag);

        c.fill = GridBagConstraints.NONE;
        c.weightx = 1.0;

        // Training input label
        c.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.anchor = GridBagConstraints.NORTHWEST;
        content.add(new JLabel("Enter training data:"), c);

        final JPanel grid = new JPanel();
        grid.setLayout(new GridLayout(5, 4));
        grid.add(new JLabel("IN1"));
        grid.add(new JLabel("IN2"));
        grid.add(new JLabel("Expected OUT   "));
        grid.add(new JLabel("Actual OUT"));

        for (int i = 0; i < 4; i++) {
            final int x = (i & 1);
            final int y = (i & 2) >> 1;
            grid.add(this.data[i][0] = new JTextField("" + y));
            grid.add(this.data[i][1] = new JTextField("" + x));
            grid.add(this.data[i][2] = new JTextField("" + (x ^ y)));
            grid.add(this.data[i][3] = new JTextField("??"));
            this.data[i][0].setEditable(false);
            this.data[i][1].setEditable(false);
            this.data[i][3].setEditable(false);
        }

        content.add(grid, c);

        // the button panel
        final JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(this.btnTrain = new JButton("Train and Prune"));
        buttonPanel.add(this.btnRun = new JButton("Run"));
        buttonPanel.add(this.btnQuit = new JButton("Quit"));
        this.btnTrain.addActionListener(this);
        this.btnRun.addActionListener(this);
        this.btnQuit.addActionListener(this);

        // Add the button panel
        c.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.anchor = GridBagConstraints.CENTER;
        content.add(buttonPanel, c);

        // Training input label
        c.gridwidth = GridBagConstraints.REMAINDER; // end row
        c.anchor = GridBagConstraints.NORTHWEST;
        content.add(this.status = new JLabel("Click train to begin training..."), c);

        // adjust size and position
        pack();
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getScreenSize();
        setLocation((int) (d.width - this.getSize().getWidth()) / 2,
                (int) (d.height - this.getSize().getHeight()) / 2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        this.btnRun.setEnabled(false);
    }

    /**
     * Called when the user clicks one of the three buttons.
     *
     * @param e
     *            The event.
     */
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == this.btnQuit) {
            System.exit(0);
        } else if (e.getSource() == this.btnTrain) {
            train();
        } else if (e.getSource() == this.btnRun) {
            evaluate();
        }
    }

    /**
     * Called when the user clicks the run button.
     */
    protected void evaluate() {
        final double[][] xorData = getGrid();

        for (int i = 0; i < 4; i++) {
            final NumberFormat nf = NumberFormat.getInstance();
            final double[] d = this.network.computeOutputs(xorData[i]);
            this.data[i][3].setText(nf.format(d[0]));
        }
    }

    /**
     * Called to generate an array of doubles based on the training data that
     * the user has entered.
     *
     * @return An array of doubles
     */
    double[][] getGrid() {
        final double[][] array = new double[4][2];

        for (int i = 0; i < 4; i++) {
            array[i][0] = Float.parseFloat(this.data[i][0].getText());
            array[i][1] = Float.parseFloat(this.data[i][1].getText());
        }

        return array;
    }

    /**
     * Called to the ideal values that the neural network should return for each
     * of the grid training values.
     *
     * @return The ideal results.
     */
    double[][] getIdeal() {
        final double[][] array = new double[4][1];

        for (int i = 0; i < 4; i++) {
            array[i][0] = Float.parseFloat(this.data[i][2].getText());
        }

        return array;
    }

    /**
     * The thread worker, used for training
     */
    public void run() {
        final double[][] xorData = getGrid();
        final double[][] xorIdeal = getIdeal();
        int update = 0;

        final Prune prune = new Prune(RATE, MOMENTUM, xorData, xorIdeal, 0.005, 2);

        Instant inst1 = Instant.now();

        prune.startIncremental();

        while (!prune.getDone()) {
            prune.pruneIncramental();
            update++;
            if (update == 10) {
                this.status.setText("Cycles:" + prune.getCycles()
                        + ",Hidden Neurons:" + prune.getHiddenNeuronCount()
                        + ", Current Error=" + prune.getError());
                update = 0;

            }

/*
            System.out.println("Cycles:" + prune.getCycles()
                    + ",Hidden Neurons:" + prune.getHiddenNeuronCount()
                    + ", Current Error=" + prune.getError());
*/

        }

        Instant inst2 = Instant.now();

        Duration tiempo = Duration.between(inst1, inst2);

        System.out.println("El tiempo fue de " + tiempo.getSeconds() + "." + tiempo.getNano() + " segundos.");

        this.status.setText("Best network found:" + prune.getHiddenNeuronCount()
                        + ",error = " + prune.getError());
        this.network = prune.getCurrentNetwork();
        this.btnRun.setEnabled(true);
    }

    /**
     * Called when the user clicks the train button.
     */
    protected void train() {
        if (this.worker != null) {
            this.worker = null;
        }
        this.worker = new Thread(this);
        this.worker.setPriority(Thread.MIN_PRIORITY);
        this.worker.start();
    }
}
