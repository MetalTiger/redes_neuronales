package com.ch9.predict;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import neural.activation.ActivationFunction;
import neural.activation.ActivationLinear;
import neural.activation.ActivationTANH;
import neural.feedforward.FeedforwardLayer;
import neural.feedforward.FeedforwardNetwork;
import neural.feedforward.train.Train;
import neural.feedforward.train.anneal.NeuralSimulatedAnnealing;
import neural.feedforward.train.backpropagation.Backpropagation;
import neural.matrix.Matrix;
import neural.util.ErrorCalculation;

/**
 * Chapter 9: Predictive Neural Networks
 *
 * SinWave: Use a neural network to predict the sine wave.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class SinWave {
    public final static int ACTUAL_SIZE = 731; // Clima:757, Natalidad:730, Mortandad:731,
    public final static int TRAINING_SIZE = 366;    //Clima:378, Natalidad:365, Mortandad: 366,
    public final static int INPUT_SIZE = 5;
    public final static int OUTPUT_SIZE = 1;
    public final static int NEURONS_HIDDEN_1 = 7;
    public final static int NEURONS_HIDDEN_2 = 0;
    public final static boolean USE_BACKPROP = true;
    public final static int NUM_PREDECIR = 5; // Número de datos a predecir después

    public static void main(final String[] args) {
        final SinWave wave = new SinWave();
        wave.run();
    }

    private ActualData actual;
    private double[][] input;

    private double[][] ideal;

    private FeedforwardNetwork network;

    public void createNetwork() {

        final ActivationFunction threshold = new ActivationLinear();
        this.network = new FeedforwardNetwork();
        this.network.addLayer(new FeedforwardLayer(INPUT_SIZE));
        this.network.addLayer(new FeedforwardLayer(SinWave.NEURONS_HIDDEN_1));

        if (SinWave.NEURONS_HIDDEN_2 > 0) {
            this.network.addLayer(new FeedforwardLayer(SinWave.NEURONS_HIDDEN_2));
        }

        this.network.addLayer(new FeedforwardLayer(OUTPUT_SIZE));

        this.network.reset();

    }

    private void display() {

        final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMinimumFractionDigits(6);
        final double[] input = new double[SinWave.INPUT_SIZE];
        final double[] output = new double[SinWave.OUTPUT_SIZE];
        int tamActual = SinWave.ACTUAL_SIZE; // 757
        int tamFinal = tamActual + NUM_PREDECIR; //761
        int flag = tamActual - 1; // Bandera usada para incrementar el valor de tamActual dentro del ciclo
        int valorFuturo = flag + 1; // 757

        List<Double> actuales = new ArrayList<>();
        List<Double> predichos = new ArrayList<>();

        for (int i = SinWave.INPUT_SIZE; i < tamActual; i++) {

            if (i > flag) {

                /*switch (i) {

                    case 757 -> this.actual.setActual(757, predichos.get(751));
                    case 758 -> this.actual.setActual(758, predichos.get(752));
                    case 759 -> this.actual.setActual(759, predichos.get(753));
                    case 760 -> this.actual.setActual(760, predichos.get(754));

                }*/

                if (i == valorFuturo){

                    this.actual.setActual(valorFuturo, predichos.get(valorFuturo - 6));

                    valorFuturo++;

                }

            }

            this.actual.getInputData(i - SinWave.INPUT_SIZE, input);
            this.actual.getOutputData(i - SinWave.INPUT_SIZE, output);

            final StringBuilder str = new StringBuilder();
            /*
            str.append(i);
            str.append(":"); // Actual
            */
            for (int j = 0; j < output.length; j++) {
                if (j > 0) {
                    str.append(',');
                }
                str.append(output[j]);
                actuales.add(output[j]);
            }

            final double[] predict = this.network.computeOutputs(input);

            str.append(":"); // Prediccion
            for (int j = 0; j < output.length; j++) {
                if (j > 0) {
                    str.append(',');
                }
                str.append(predict[j]);
                predichos.add(predict[j]);

                //System.out.println("Tamañin " + predichos.size());

            }

            //str.append(":"); // Error

            final ErrorCalculation error = new ErrorCalculation();
            error.updateError(predict, output);
            //str.append(error.calculateRMS());

            System.out.println(str.toString());

            if (i == flag){
                tamActual += SinWave.NUM_PREDECIR;
            }


        }

        /*for (double actual: actuales) {

            System.out.println(actual);

        }*/


    }

    public void displayTraining() {
        for (int i = 0; i < this.input.length; i++) {
            final StringBuilder str = new StringBuilder();
            for (int j = 0; j < this.input[0].length; j++) {
                if (j > 0) {
                    str.append(',');
                }
                str.append(this.input[i][j]);
            }
            str.append("=>");
            for (int j = 0; j < this.ideal[0].length; j++) {
                if (j > 0) {
                    str.append(',');
                }
                str.append(this.ideal[i][j]);
            }
            System.out.println(str.toString());
        }

    }

    private void generateActual() {
        this.actual = new ActualData(SinWave.ACTUAL_SIZE, SinWave.INPUT_SIZE, SinWave.OUTPUT_SIZE, SinWave.NUM_PREDECIR);
    }

    private void generateTrainingSets() {
        this.input = new double[TRAINING_SIZE][INPUT_SIZE];
        this.ideal = new double[TRAINING_SIZE][OUTPUT_SIZE];

        for (int i = 0; i < TRAINING_SIZE; i++) {
            this.actual.getInputData(i, this.input[i]);
            this.actual.getOutputData(i, this.ideal[i]);
        }
    }

    public void run() {
        generateActual();
        createNetwork();
        generateTrainingSets();

        if (SinWave.USE_BACKPROP) {
            trainNetworkBackprop();
        } else {
            trainNetworkAnneal();
        }
        display();

    }

    private void trainNetworkAnneal() {
        // train the neural network
        final NeuralSimulatedAnnealing train = new NeuralSimulatedAnnealing(
                this.network, this.input, this.ideal, 10, 2, 100);

        int epoch = 1;

        do {
            train.iteration();
            System.out.println("Iteration #" + epoch + " Error:"
                    + train.getError());
            epoch++;
        } while ((train.getError() > 0.0001));
    }

    private void trainNetworkBackprop() {
        final Train train = new Backpropagation(this.network, this.input,
                this.ideal, 0.01, 0.1); // learn: 0.001, momentum: 0.1

        /*int epoch = 1;

        do {
            train.iteration();
            System.out.println("Iteration #" + epoch + " Error:" + train.getError());
            epoch++;
        } while ((epoch < 10000) &&(train.getError() > 0.0001)); //

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

        }*/

        // Clima Iteration #250,000 Error:3.286997632801485E-4 learnRate: 0.01
        double[][] dataEntrada1 = {
                {-0.6115475739923365, -0.48044568731276815, 0.9640008809358805, 0.09330937886835992, -0.10152474067804881, 0.1796886875536483, -0.6526768727006096,},
                {-0.2751434138221723, 1.0806038016162367, -0.5724962985075714, -0.4112985542837972, -1.1591632889195467, 0.821902069689863, 0.9248948121596042,},
                {-0.028246236251094167, -0.3731354313701606, -0.21757711575428917, -0.3198518341152102, -0.1623701646784918, 0.7554749877217738, -0.6133190530380638,},
                {-0.25479661462086256, 0.822439199322896, 0.6519135939212534, -0.5365652468346389, 0.2772061951049606, -0.7502198418522961, -0.45366564963680844,},
                {-2.661989840938887, -1.0887743267039263, 0.23875002375701482, -2.9744525127200365, -0.345230873524142, -0.6282951641051214, -1.32035118678565,},
                {0.202501187060761, -0.3274053471602739, -0.5007395595718254, 0.22732386085629352, -0.37335031830984544, 0.5026955003783855, 0.375505561216463,},
        };

        double[][] dataOcultas1 = {
                {-2.304369030326628},
                {-1.0607678435422363},
                {0.43096476628917746},
                {-2.3445375878625634},
                {-0.6213683950546859},
                {-0.15070168001900525},
                {-1.1326449153573368},
                {1.443091587026824},

        };

        // Natalidad Épocas: 250,000 Error: 1.5756755089499673E-4 learnRate: 0.01
        double[][] dataEntrada2 = {
                {-1.2146349418808184, 1.0742338023943532, -0.37928219308459066, -3.93787074159716, 0.9634590467624096, 0.5410951668422399, 1.477845067877716,},
                {-1.5335974441672786, -0.20634403572336735, 0.6417284543202827, -3.5064694162645567, 0.05276934186760399, 1.2037857896391386, 1.7494215130288298,},
                {-3.0385230540143207, -0.3068349856739891, -1.2281595189180254, -2.555071176285205, -1.7748312314342716, 0.7706801677992534, 1.2768371554264375,},
                {-1.804668478906584, 1.092476803556693, 0.9617685111355961, -3.5474063117653785, 2.130306547933405, 0.9001810337502864, -0.20029112947239444,},
                {-3.5291173180831814, -0.41614322613994553, -1.4308290746408452, -4.141780604393109, -2.723609365069918, -0.8407909108225448, 1.387793956638846,},
                {-0.7205805109855229, -0.785735646967521, -0.6247140233945286, -0.7461232599536345, 0.2191685091314282, -0.9365709331965335, 1.113369309147599,},
        };

        double[][] dataOcultas2 = {
                {-3.6429712809971475},
                {-1.3201406573410313},
                {-1.6805330047047542},
                {-5.309843279658063},
                {-3.394455842157845},
                {-1.072723876621417},
                {2.0376872565277817},
                {0.1431964520719495},

        };

        // Natalidad Épocas: 500,000  Error:0.0023794641326284733 learnRate: 0.1
        double[][] dataEntrada3 = {
                {-15.901082722727821, 1.727347030917137, -0.02558653197658379, -1.2508055530213489, -27.195178625236977, -2.170224194945098, 0.5109804569874903,},
                {4.059094802200717, 8.61169742386998, -10.862649681754812, -3.2379379873237126, -0.32715742542812504, -6.260178584156028, -3.6580253313243722,},
                {12.104512266662443, -1.13975458215007, -10.172500675083468, -5.7888209277778, 2.0943807705053334, 2.212825781011414, -24.149351578526225,},
                {-19.033502251147393, -22.596838444412857, 7.956685632474644, 14.567979768093926, 27.552706196322777, 24.59833173172561, -10.089988737317704,},
                {-22.62946309620262, -8.3068574158968, -8.101704388874854, 7.415337782714194, -10.254786531876722, 1.4971935143111126, 8.367338645396055,},
                {6.677697149841988, 5.428177650760398, 4.295181061185223, -10.214472734843726, 3.135341191666836, -16.5678235914444, 3.667857275676527,},
        };

        double[][] dataOcultas3 = {
                {1.7337533595205237},
                {-2.0877965739089865},
                {-1.155676622417159},
                {8.324398233893964},
                {-0.7239421470853356},
                {-11.620385491966218},
                {1.245571353454685},
                {0.3762314411571568},
        };

        Matrix pesosEntrada = new Matrix(dataEntrada3);
        Matrix pesosOcultas = new Matrix(dataOcultas3);


        train.getNetwork().getLayers().get(0).setMatrix(pesosEntrada);
        train.getNetwork().getLayers().get(1).setMatrix(pesosOcultas);

    }
}
