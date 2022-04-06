package com.ch6.tsp;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Chapter 6: Training using a Genetic Algorithm
 *
 * GeneticTravelingSalesman: Attempt to solve the traveling salesman
 * problem using a genetic algorithm.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class GeneticTravelingSalesman extends JFrame implements Runnable,
        ComponentListener {

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 3849579247520213905L;

    /**
     * How many cities to use.
     */
    public static final int CITY_COUNT = 20;

    /**
     * How many chromosomes to use.
     */
    public static final int POPULATION_SIZE = 5000;

    /**
     * What percent of new-borns to mutate.
     */
    public static final double MUTATION_PERCENT = 0.10;

    /**
     * The main method.
     *
     * @param args
     *            Not used
     */
    public static void main(final String[] args) {
        (new GeneticTravelingSalesman()).setVisible(true);
    }

    /**
     * The part of the population eligable for mateing.
     */
    protected int matingPopulationSize = POPULATION_SIZE / 2;

    /**
     * The part of the population favored for mating.
     */
    protected int favoredPopulationSize = this.matingPopulationSize / 2;

    /**
     * A Map object that will display the city map.
     */
    protected WorldMap map = null;

    /**
     * The current status. Used to display the current status to the user.
     */
    protected JLabel status;

    /**
     * How much genetic material to take during a mating.
     */
    protected int cutLength = CITY_COUNT / 5;

    /**
     * The current generation, or epoc.
     */
    protected int generation;

    /**
     * The background worker thread.
     */
    protected Thread worker = null;

    /**
     * Is the thread started.
     */
    protected boolean started = false;

    /**
     * The list of cities.
     */
    protected City[] cities;

    protected TSPGeneticAlgorithm genetic;

    /**
     * The constructor
     */
    public GeneticTravelingSalesman() {
        addComponentListener(this);
        setSize(300, 300);
        setTitle("Traveling Salesman Problem");
    }

    /**
     * Not used, but required by Java.
     *
     * @param e
     *            The event.
     */
    public void componentHidden(final ComponentEvent e) {
    }

    /**
     * Not used, but required by Java.
     *
     * @param e
     *            The event.
     */
    public void componentMoved(final ComponentEvent e) {
    }

    /**
     * Not used, but required by Java.
     *
     * @param e
     *            The event.
     */
    public void componentResized(final ComponentEvent e) {
    }

    /**
     * Used to add necessary components.
     *
     * @param e
     *            The event.
     */
    public void componentShown(final ComponentEvent e) {
        getContentPane().setLayout(new BorderLayout());

        if (this.map == null) {
            this.map = new WorldMap(this);
            getContentPane().add(this.map, "Center");
            this.status = new JLabel("Starting up");
            getContentPane().add(this.status, "South");
        }

        // place the cities at random locations
        final int height = getBounds().height - 50;
        final int width = getBounds().width - 10;
        this.cities = new City[GeneticTravelingSalesman.CITY_COUNT];

        this.cities[0] = new City(69, 189);
        this.cities[1] = new City(251, 70);
        this.cities[2] = new City(289, 156);
        this.cities[3] = new City(46, 161);
        this.cities[4] = new City(38, 210);
        this.cities[5] = new City(284, 150);
        this.cities[6] = new City(103, 83);
        this.cities[7] = new City(170, 116);
        this.cities[8] = new City(252, 107);
        this.cities[9] = new City(290, 225);

        this.cities[10] = new City(330, 151);
        this.cities[11] = new City(13, 85);
        this.cities[12] = new City(17, 119);
        this.cities[13] = new City(210, 127);
        this.cities[14] = new City(340, 275);
        this.cities[15] = new City(160, 180);
        this.cities[16] = new City(170, 170);
        this.cities[17] = new City(130, 177);
        this.cities[18] = new City(90, 100);
        this.cities[19] = new City(87, 200);

/*
        for (int i = 0; i < GeneticTravelingSalesman.CITY_COUNT; i++) {
            this.cities[i] = new City((int) (Math.random() * width),
                    (int) (Math.random() * height));

            System.out.println(this.cities[i].xpos + ", " + this.cities[i].ypos);
        }
*/

        this.genetic = new TSPGeneticAlgorithm(this.cities,
                GeneticTravelingSalesman.POPULATION_SIZE,
                GeneticTravelingSalesman.MUTATION_PERCENT, 0.25, 0.5,
                GeneticTravelingSalesman.CITY_COUNT / 5);

        start();

    }

    public TSPChromosome getTopChromosome() {
        return this.genetic.getChromosome(0);
    }

    /**
     * The main loop for the background thread. It is here that most of the work
     * os orchestrated.
     */
    public void run() {

        double thisCost = 500.0;
        double oldCost = 0.0;
        int countSame = 0;

        this.map.update(this.map.getGraphics());

        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);

        while (countSame < 100) {

            this.generation++;

            System.out.println("Generation " + this.generation + " Cost "
                    + (int) thisCost + " Mutated " + nf.format(MUTATION_PERCENT) + "%");

            this.status.setText("Generation " + this.generation + " Cost "
                    + (int) thisCost + " Mutated " + nf.format(0) + "%");

            this.genetic.iteration();
            thisCost = this.getTopChromosome().getCost();

            if ((int) thisCost == (int) oldCost) {
                countSame++;
            } else {
                countSame = 0;
                oldCost = thisCost;
            }
            this.map.update(this.map.getGraphics());

        }
        this.status.setText("Solution found after " + this.generation
                + " generations.");
    }

    /**
     * Start the background thread.
     */
    public void start() {

        // create the initial chromosomes

        // start up the background thread
        this.started = true;
        this.map.update(this.map.getGraphics());

        this.generation = 0;

        if (this.worker != null) {
            this.worker = null;
        }
        this.worker = new Thread(this);
        // worker.setPriority(Thread.MIN_PRIORITY);
        this.worker.start();
    }

}
