package neural.prune;

import java.util.Collection;

import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.backpropagation.Backpropagation;

/**
 * Prune: The prune class provides some basic help for determining
 * the correct number of neurons to put in a hidden layer.  Two
 * types of prune are supported.
 *
 * Selective - Try removing neurons from the hidden layers and if
 * the affect on the accuracy of the neural network is not great
 * then make the removal permanent.
 *
 * Incremental - Start with one neuron in the hidden layer and
 * increase until the neural network error is acceptable.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class Prune {

    /**
     * The neural network that is currently being processed.
     */
    protected FeedforwardNetwork currentNetwork;

    /**
     * The training set.
     */
    protected double[][] train;

    /**
     * The ideal results from the training set.
     */
    protected double[][] ideal;

    /**
     * The desired learning rate.
     */
    protected double rate;

    /**
     * The desired momentum.
     */
    protected double momentum;
    protected double maxError;
    protected double maxNeuronCount;

    /**
     * The current error.
     */
    protected double error;

    /**
     * Used to determine if training is still effective. Holds the error level
     * the last time the error level was tracked. This is 1000 cycles ago. If no
     * significant drop in error occurs for 1000 cycles, training ends.
     */
    protected double markErrorRate; // Si esto no cambia en 10,000 ciclos entonces se agrega una neurona

    /**
     * Used with markErrorRate. This is the number of cycles since the error was
     * last marked.
     */
    protected int sinceMark;

    /**
     * The number of cycles used.
     */
    protected int cycles;

    /**
     * The number of hidden neurons.
     */
    protected int hiddenNeuronCount;

    /**
     * Flag to indicate if the incremental prune process is done or not.
     */
    protected boolean done;

    Backpropagation backprop;

    /**
     * Constructor used to set up the prune object for an incremental prune.
     *
     * @param rate
     *            The desired learning rate.
     * @param momentum
     *            The desired momentum.
     * @param train
     *            The training data.
     * @param ideal
     *            The ideal results for the training data.
     * @param maxError
     *            The maximum error that is acceptable.
     */
    public Prune(final double rate, final double momentum,
                 final double[][] train, final double[][] ideal,
                 final double maxError, double maxNeuronCount) {
        this.rate = rate;
        this.momentum = momentum;
        this.train = train;
        this.ideal = ideal;
        this.maxError = maxError;
        this.maxNeuronCount = maxNeuronCount;
    }

    /**
     * Constructor that is designed to set up for a selective prune.
     *
     * @param network
     *            The neural network that we wish to prune.
     * @param train
     * @param ideal
     */
    public Prune(final FeedforwardNetwork network, final double[][] train,
                 final double[][] ideal, final double maxError) {
        this.currentNetwork = network;
        this.train = train;
        this.ideal = ideal;
        this.maxError = maxError;
    }

    /**
     * Internal method used to clip the hidden neurons.
     *
     * @param neuron
     *            The neuron to clip.
     * @return Returns the new neural network.
     */
    protected FeedforwardNetwork clipHiddenNeuron(final int neuron) {
        final FeedforwardNetwork result = (FeedforwardNetwork) this.currentNetwork
                .clone();
        final Collection<FeedforwardLayer> c = result.getHiddenLayers();
        final Object[] layers = c.toArray();
        ((FeedforwardLayer) layers[0]).prune(neuron);
        return result;
    }

    /**
     * Internal method to determine the error for a neural network.
     *
     * @param network
     *            The neural network that we are seeking an error rate for.
     * @return The error for the specified neural network.
     */
    protected double determineError(final FeedforwardNetwork network) {
        return network.calculateError(this.train, this.ideal);

    }

    /**
     * Internal method that will loop through all hidden neurons and prune them
     * if pruning the neuron does not cause too great of an increase in error.
     *
     * @return True if a prune was made, false otherwise.
     */
    protected boolean findNeuron() {

        for (int i = 0; i < this.currentNetwork.getHiddenLayerCount(); i++) {
            final FeedforwardNetwork trial = this.clipHiddenNeuron(i);
            final double e2 = determineError(trial);
            if (e2 < this.maxError) {
                this.currentNetwork = trial;
                return true;
            }
        }
        return false;
    }

    /**
     * Get the current neural network.
     *
     * @return The neural network.
     */
    public FeedforwardNetwork getCurrentNetwork() {
        return this.currentNetwork;
    }

    /**
     * Called to get the current number of cycles.
     *
     * @return The current number of cycles.
     */
    public int getCycles() {
        return this.cycles;
    }

    /**
     * Called to determine if we are done in an incremental prune.
     *
     * @return Returns true if we are done, false otherwise.
     */
    public boolean getDone() {
        return this.done;
    }

    /**
     * Called to get the current error.
     *
     * @return The current error.
     */
    public double getError() {
        return this.error;
    }

    /**
     * The current number of hidden neurons being evaluated.
     * @return The current number of hidden neurons.
     */
    protected int getHiddenCount() {
        final Collection<FeedforwardLayer> c = this.currentNetwork.getHiddenLayers();
        final Object layers[] = c.toArray();
        return ((FeedforwardLayer) layers[0]).getNeuronCount();
    }

    /**
     * Get the number of hidden neurons.
     *
     * @return The number of hidden neurons.
     */
    public double getHiddenNeuronCount() {
        return this.hiddenNeuronCount;
    }

    /**
     * Internal method that is called at the end of each incremental cycle.
     */
    protected void increment() {
        boolean doit = false; // Sirve para saber si se necesita agragar una neurona

        if (this.markErrorRate == 0) {
            this.markErrorRate = this.error;
            this.sinceMark = 1;
        } else {
            this.sinceMark++;
            if (this.sinceMark == 10000) {

                /*if ((this.markErrorRate - this.error) < 0.01) {
                    doit = true;
                }
                this.markErrorRate = this.error;*/

                if (this.getHiddenNeuronCount() < this.maxNeuronCount){

                    doit = true;


                }else {

                    this.done = true;

                }

                this.sinceMark = 1;

            }

        }

        if (this.error < this.maxError) {
            this.done = true;
        }

        if (doit) {
            this.cycles = 0;
            this.hiddenNeuronCount++;

            this.currentNetwork = new FeedforwardNetwork();
            this.currentNetwork.addLayer(new FeedforwardLayer(
                    this.train[0].length));
            this.currentNetwork.addLayer(new FeedforwardLayer(
                    this.hiddenNeuronCount));
            this.currentNetwork.addLayer(new FeedforwardLayer(
                    this.ideal[0].length));
            this.currentNetwork.reset();

            this.backprop = new Backpropagation(this.currentNetwork,
                    this.train, this.ideal, this.rate, this.momentum);
        }
    }

    /**
     * Method that is called to prune the neural network incrementally.
     */
    public void pruneIncramental() {
        if (this.done) {
            return;
        }

        this.backprop.iteration();

        this.error = this.backprop.getError();
        this.cycles++;
        increment();
    }

    /**
     * Called to complete the selective pruning process.
     *
     * @return The number of neurons that were pruned.
     */
    public int pruneSelective() {
        final int i = this.getHiddenCount();
        while (findNeuron()) {
            ;
        }
        return (i - this.getHiddenCount());
    }

    /**
     * Method that is called to start the incremental prune process.
     */
    public void startIncremental() {
        this.hiddenNeuronCount = 1;
        this.cycles = 0;
        this.done = false;

        this.currentNetwork = new FeedforwardNetwork();
        this.currentNetwork
                .addLayer(new FeedforwardLayer(this.train[0].length));
        this.currentNetwork.addLayer(new FeedforwardLayer(
                this.hiddenNeuronCount));
        this.currentNetwork
                .addLayer(new FeedforwardLayer(this.ideal[0].length));
        this.currentNetwork.reset();

        this.backprop = new Backpropagation(this.currentNetwork, this.train,
                this.ideal, this.rate, this.momentum);

    }

}