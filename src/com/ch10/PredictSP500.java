package com.ch10;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import common.ReadCSV;
import neural.activation.ActivationFunction;
import neural.activation.ActivationTANH;
import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.Train;
import neural.feedforward.train.anneal.NeuralSimulatedAnnealing;
import neural.feedforward.train.backpropagation.Backpropagation;
import neural.util.ErrorCalculation;
import neural.util.SerializeObject;

/**
 * Chapter 10: Application to the Financial Markets
 *
 * PredictSP500: Attempt to predict the SP500 using a predictive
 * feedforward neural network.  The key word is "attempt".  The
 * neural network can guess some basic trends in the SP500, and is
 * meant only as a starting point.
 *
 * This class is in no way investment advice!
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class PredictSP500 {

    public final static int TRAINING_SIZE = 500;
    public final static int INPUT_SIZE = 10;
    public final static int OUTPUT_SIZE = 1;
    public final static int NEURONS_HIDDEN_1 = 20;
    public final static int NEURONS_HIDDEN_2 = 0;
    public final static double MAX_ERROR = 0.02;
    public final static double NUM_PREDECIR = 5;
    public final static Date PREDICT_FROM = ReadCSV.parseDate("2022-01-01"); // Empieza a predecir desde aqui
    //public final static Date PREDICT_FROM = ReadCSV.parseDate("2022-01-01");
    public final static Date LEARN_FROM = ReadCSV.parseDate("1980-01-01");  // Fecha hasta donde terminará de aprender
    //public final static Date LEARN_FROM = ReadCSV.parseDate("2014-01-19");

    public static void main(final String[] args) {

        final PredictSP500 predict = new PredictSP500();

        boolean full = false;

        if (full) {

            predict.run(true);

        } else {

            predict.run(false);

        }

        /*if (args.length > 0 && args[0].equalsIgnoreCase("full")) {

            predict.run(true);

        }else {

            predict.run(false);

        }*/

    }

    private double[][] input;

    private double[][] ideal;
    private FeedforwardNetwork network;

    private SP500Actual actual;

    public void createNetwork() {

        /*
        * -Le presenta a la red el cambio porcentual entre dos dias, asi hasta obtener 10 cambios porcentuales
        * -De la ultima fecha presenta el porcentaje
        * */

        final ActivationFunction threshold = new ActivationTANH();

        this.network = new FeedforwardNetwork();
        this.network.addLayer(new FeedforwardLayer(threshold,PredictSP500.INPUT_SIZE * 2)); // 20 entradas, el onceavo es
        this.network.addLayer(new FeedforwardLayer(threshold, PredictSP500.NEURONS_HIDDEN_1));

        if (PredictSP500.NEURONS_HIDDEN_2 > 0) {

            this.network.addLayer(new FeedforwardLayer(threshold, PredictSP500.NEURONS_HIDDEN_2));

        }

        this.network.addLayer(new FeedforwardLayer(threshold, PredictSP500.OUTPUT_SIZE));
        this.network.reset();
    }

    public void display() {

        /*
        * -Presenta los cambios porcentuales
        * -Se obtiene la nueva tasa dependiendo del dia, de forma que en el onceavo presenta
        * ++Tarea++
        * -Predecir el valor de cierre en los siguientes 5 días y buscar que la red sea precisa (Mover el error/entradas/ocultas)
        * -Mostrar el stock, se pued ehacer mediante regla de 3.
        * */

        final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(2);

        final double[] present = new double[INPUT_SIZE * 2];
        double[] predict = new double[OUTPUT_SIZE];
        final double[] actualOutput = new double[OUTPUT_SIZE];

        List<Double> stocksPredecidos = new ArrayList<>();

        double lastPercent = 0;
        double lastStock = 0;

        int index = 0;
        for (final FinancialSample sample : this.actual.getSamples()) {

            if (sample.getDate().after(PredictSP500.PREDICT_FROM)) {

                final StringBuilder str = new StringBuilder();
                //str.append(ReadCSV.displayDate(sample.getDate()));
                //str.append(index).append(":"); // Mostrar índice
                //str.append(":Start=");
                str.append(sample.getAmount());

                this.actual.getInputData(index - INPUT_SIZE, present);
                this.actual.getOutputData(index - INPUT_SIZE, actualOutput);

                predict = this.network.computeOutputs(present);

                /*
                str.append(",Actual % Change=");
                str.append(percentFormat.format(actualOutput[0]));
                str.append(",Predicted % Change= ");
                str.append(percentFormat.format(predict[0]));
                */

                lastPercent = predict[0];   // Se obtiene el ultimo porcentaje

                lastStock = sample.getAmount() + (sample.getAmount() * lastPercent);    // Se obtiene el ultimo stock

                stocksPredecidos.add(lastStock);

                //str.append(":Stock Predicho = ");
                str.append(":");
                str.append(lastStock);

                /*
                str.append(":Difference=");

                final ErrorCalculation error = new ErrorCalculation();
                error.updateError(predict, actualOutput);
                str.append(percentFormat.format(error.calculateRMS()));
                */

                System.out.println(str.toString());

            }

            index++;

        }

        FinancialSample sample = new FinancialSample(); // Se crea un objeto que contendrá la última muestra
        sample.setDate(new Date());
        sample.setAmount(lastStock);
        sample.setPercent(lastPercent);
        //sample.setRate();

        this.actual.getSamples().add(sample);   // Se añade la ultima muestra al arreglo actual

        int tamPredecidos = stocksPredecidos.size() - 1; // Se obtiene el indice del ultimo stock añadido

        for (int i = 0; i < NUM_PREDECIR; i++){

            this.actual.getInputData(index - INPUT_SIZE, present);
            this.actual.getOutputData(index - INPUT_SIZE, actualOutput);

            predict = this.network.computeOutputs(present);

            double stock = stocksPredecidos.get(tamPredecidos + i) +
                    stocksPredecidos.get(tamPredecidos + i) * predict[0]; // Se el stock a partir del ultimo sotck añadido y la fórmula

            stocksPredecidos.add(stock);

            FinancialSample muestra = (FinancialSample) this.actual.getSamples().toArray()[index]; // Se obtiene la ultima muestra

            System.out.println(muestra.getAmount() + ":" + stock); // Se imprime la ultima muestra y su predicción

            FinancialSample extraSample = new FinancialSample(); // Se crea un objeto que contendrá la ultima muestra obtenida
            extraSample.setDate(new Date());
            extraSample.setAmount(stock);
            extraSample.setPercent(predict[0]);

            this.actual.getSamples().add(extraSample); // Se añade la muestra nueva al arreglo actual

            index++;    // Se incrementa el índice de la lista de los actuales

        }

    }

    private void generateTrainingSets() {

        this.input = new double[TRAINING_SIZE][INPUT_SIZE * 2];
        this.ideal = new double[TRAINING_SIZE][OUTPUT_SIZE];

        // find where we are starting from
        int startIndex = 0;

        for (final FinancialSample sample : this.actual.getSamples()) {

            if (sample.getDate().after(LEARN_FROM)) {

                break;

            }

            startIndex++;

        }

        // create a sample factor across the training area
        final int eligibleSamples = TRAINING_SIZE - startIndex;
        if (eligibleSamples == 0) {

            System.out.println("Need an earlier date for LEARN_FROM or a smaller number for TRAINING_SIZE.");
            System.exit(0);

        }
        final int factor = eligibleSamples / TRAINING_SIZE;

        // grab the actual training data from that point
        for (int i = 0; i < TRAINING_SIZE; i++) {

            this.actual.getInputData(startIndex + (i * factor), this.input[i]);
            this.actual.getOutputData(startIndex + (i * factor), this.ideal[i]);

        }

    }

    public void loadNeuralNetwork() throws IOException, ClassNotFoundException {

        this.network = (FeedforwardNetwork) SerializeObject.load("/home/kevin_cb/RedNeuronalArtificial/sp500.net");

    }

    public void run(boolean full) {

        try {

            this.actual = new SP500Actual(INPUT_SIZE, OUTPUT_SIZE);
            this.actual.load("/home/kevin_cb/RedNeuronalArtificial/SP500_2.csv", "/home/kevin_cb/RedNeuronalArtificial/prime-rate-history.csv"); // S&P 500
            //this.actual.load("/home/kevin_cb/RedNeuronalArtificial/BTC-USD.csv", "/home/kevin_cb/RedNeuronalArtificial/prime-rate-history.csv"); // BTC

            System.out.println("Samples read: " + this.actual.size());

            if (full) {

                createNetwork();
                generateTrainingSets();

                trainNetworkBackprop();

                saveNeuralNetwork();

            } else {

                loadNeuralNetwork();

            }

            display();




        } catch (final Exception e) {

            e.printStackTrace();

        }
    }

    public void saveNeuralNetwork() throws IOException {

        SerializeObject.save("sp500.net", this.network);

    }

    private void trainNetworkBackprop() {

        final Train train = new Backpropagation(this.network, this.input,
                this.ideal, 0.00001, 0.1);
        double lastError = Double.MAX_VALUE;
        int epoch = 1;
        int lastAnneal = 0;

        do {

            train.iteration();
            double error = train.getError();

            System.out.println("Iteration(Backprop) #" + epoch + " Error:" + error);

            if( error>0.05 ) {

                if( (lastAnneal > 100) && (error > lastError || Math.abs(error - lastError) < 0.0001) ) {

                    trainNetworkAnneal();
                    lastAnneal = 0;

                }

            }

            lastError = train.getError();
            epoch++;
            lastAnneal++;

        } while (train.getError() > MAX_ERROR);

    }

    private void trainNetworkAnneal() {

        System.out.println("Training with simulated annealing for 5 iterations");
        // train the neural network
        final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(
                this.network, this.input, this.ideal, 10, 2, 100);

        int epoch = 1;

        for(int i = 1; i <= 5; i++) {

            train.iteration();
            System.out.println("Iteration(Anneal) #" + epoch + " Error:" + train.getError());
            epoch++;

        }
    }
}
