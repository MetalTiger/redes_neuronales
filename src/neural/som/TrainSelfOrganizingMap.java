package neural.som;

import neural.matrix.Matrix;
import neural.matrix.MatrixMath;

/**
 * TrainSelfOrganizingMap: Implements an unsupervised training algorithm for use
 * with a Self Organizing Map.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class TrainSelfOrganizingMap {

    /**
     * The learning method, either additive or subtractive.
     * @author jheaton
     *
     */
    public enum LearningMethod
    {
        ADDITIVE,
        SUBTRACTIVE
    }

    /**
     * The self organizing map to train.
     */
    private final SelfOrganizingMap som;

    /**
     * The learning method.
     */
    protected LearningMethod learnMethod;

    /**
     * The learning rate.
     */
    protected double learnRate;

    /**
     * Reduction factor.
     */
    protected double reduction = 0.99;

    /**
     * Mean square error of the network for the iteration.
     */
    protected double totalError;


    /**
     * Mean square of the best error found so far.
     */
    protected double globalError;

    /**
     * Keep track of how many times each neuron won.
     */
    int[] won;

    /**
     * The training sets.
     */
    double[][] train;

    /**
     * How many output neurons.
     */
    private final int outputNeuronCount;

    /**
     * How many input neurons.
     */
    private final int inputNeuronCount;

    /**
     * The best network found so far.
     */
    private final SelfOrganizingMap bestnet;


    /**
     * The best error found so far.
     */
    private double bestError;

    /**
     * The work matrix, used to calculate corrections.
     */
    private Matrix work;

    /**
     * The correction matrix, will be applied to the weight matrix after each
     * training iteration.
     */
    private Matrix correc;

    /**
     * Construct the trainer for a self organizing map.
     * @param som The self organizing map.
     * @param train The training method.
     * @param learnMethod The learning method.
     * @param learnRate The learning rate.
     */
    public TrainSelfOrganizingMap(final SelfOrganizingMap som, final double[][] train, LearningMethod learnMethod, double learnRate) {

        this.som = som;
        this.train = train;
        this.totalError = 1.0;
        this.learnMethod = learnMethod;
        this.learnRate = learnRate;

        this.outputNeuronCount = som.getOutputNeuronCount();
        this.inputNeuronCount = som.getInputNeuronCount();

        this.totalError = 1.0;

        for (int tset = 0; tset < train.length; tset++) {

            final Matrix dptr = Matrix.createColumnMatrix(train[tset]);

            if (MatrixMath.vectorLength(dptr) < SelfOrganizingMap.VERYSMALL) {

                throw (new RuntimeException("Multiplicative normalization has null training case"));

            }

        }

        this.bestnet = new SelfOrganizingMap(this.inputNeuronCount, this.outputNeuronCount, this.som.getNormalizationType());

        this.won = new int[this.outputNeuronCount];

        this.correc = new Matrix(this.outputNeuronCount, this.inputNeuronCount + 1); // + 1 porque almacena la entrada sintética

        // Si se usa el método aditivo como ajuste de pesos entonces se crea una matriz de trabajo
        if (this.learnMethod == LearningMethod.ADDITIVE) {

            this.work = new Matrix(1, this.inputNeuronCount + 1);

        } else {

            this.work = null;

        }

        initialize(); // Se inicializan los pesos de forma random

        this.bestError = Double.MAX_VALUE;
    }

    /**
     * Adjust the weights and allow the network to learn.
     * Applies the correction matrix to the actual weight matrix
     */
    protected void adjustWeights() {

        for (int i = 0; i < this.outputNeuronCount; i++) {

            // Si esta neurona no ha ganado entonces no ajustes sus pesos
            if (this.won[i] == 0) {

                continue;

            }

            double f = 1.0 / this.won[i]; // Se calcula el número recíproco de las veces que esta neurona ha ganado

            if (this.learnMethod == LearningMethod.SUBTRACTIVE) {

                f *= this.learnRate; // f se escala por la taza de aprendizaje

            }

            double length = 0.0;

            for (int j = 0; j <= this.inputNeuronCount; j++) {

                final double corr = f * this.correc.get(i, j);  // Correción a aplicar
                this.som.getOutputWeights().add(i, j, corr);    // Se suma la correción a los pesos
                length += corr * corr;

            }

        }
    }

    /**
     * Copy the weights from one matrix to another.
     * @param source The source SOM.
     * @param target The target SOM.
     */
    private void copyWeights(final SelfOrganizingMap source, final SelfOrganizingMap target) {

        MatrixMath.copy(source.getOutputWeights(), target.getOutputWeights());

    }


    /**
     * Evaludate the current error level of the network.
     */
    public void evaluateErrors() {

        this.correc.clear(); // Limpia la matriz de correcciones

        for (int i = 0; i < this.won.length; i++) {

            this.won[i] = 0;

        }

        this.globalError = 0.0;

        // loop through all training sets to determine correction
        for (int tset = 0; tset < this.train.length; tset++) {

            final NormalizeInput input = new NormalizeInput(this.train[tset], this.som.getNormalizationType());

            final int best = this.som.winner(input); // Neurona ganadora (Índice)

            this.won[best]++;

            final Matrix wptr = this.som.getOutputWeights().getRow(best); // Pesos de la neurona ganadora

            double length = 0.0;
            double diff;

            // Se recorre el training set que corresponde a cada neurona de entrada
            for (int i = 0; i < this.inputNeuronCount; i++) {

                diff = this.train[tset][i] * input.getNormfac() - wptr.get(0, i); // Diferencia del training set con los pesos de la neurona ganadora

                length += diff * diff;  // Magnitud (suma de cuadrados)

                if (this.learnMethod == LearningMethod.SUBTRACTIVE) {

                    this.correc.add(best, i, diff); // Se corrigen los pesos añadiendo la diferencia

                } else {
                    // Se usa una matriz de trabajo que contiene los valores a añadirse
                    this.work.set(0, i,
                            this.learnRate * this.train[tset][i] * input.getNormfac() + wptr.get(0, i)
                    ); // El set de entrenamiento es multiplicado por la taza de aprendizaje y se le suma el mejor peso correspondiente

                }

            }

            diff = input.getSynth() - wptr.get(0, this.inputNeuronCount); // Se aplica la entrada sintética al peso de la mejor neurona
            length += diff * diff;

            if (this.learnMethod == LearningMethod.SUBTRACTIVE) {

                this.correc.add(best, this.inputNeuronCount, diff); // Se suma la entrada sintética al peso (Se corrige)

            } else {

                this.work.set(0, this.inputNeuronCount,
                        this.learnRate * input.getSynth() + wptr.get(0, this.inputNeuronCount)
                );  // Se aplica la entrada sintética al mejor peso asi como la taza de aprendizaje

            }

            // Si la magnitud actual supera el error entonces este corresponde al mejor error
            if (length > this.globalError) {

                this.globalError = length;

            }

            if (this.learnMethod == LearningMethod.ADDITIVE) {

                // Se normalizan los pesos de la matriz de trabajo
                normalizeWeight(this.work, 0);

                // Se corrige cada peso
                for (int i = 0; i <= this.inputNeuronCount; i++) {

                    this.correc.add(best, i, this.work.get(0, i) - wptr.get(0, i));

                }

            }

        }

        System.out.println("*Marcador de neuronas*");

        for (int j = 0; j < this.won.length; j++) {

            System.out.println("Neurona #" + j + ": " + this.won[j]);

        }

        System.out.println();

        // Se calcula el RMS
        this.globalError = Math.sqrt(this.globalError);
    }

    /**
     * Force a win, if no neuron won.
     */
    protected void forceWin() {

        int best, which = 0;

        final Matrix outputWeights = this.som.getOutputWeights();

        // Loop over all training sets. Find the training set with the least output.
        double dist = Double.MAX_VALUE;

        for (int tset = 0; tset < this.train.length; tset++) {

            best = this.som.winner(this.train[tset]);
            final double[] output = this.som.getOutput();

            if (output[best] < dist) {

                dist = output[best];
                which = tset;

            }

        }

        final NormalizeInput input = new NormalizeInput(this.train[which], this.som.getNormalizationType());
        best = this.som.winner(input);
        final double[] output = this.som.getOutput();

        dist = Double.MIN_VALUE;
        int i = this.outputNeuronCount;

        while ((i--) > 0) {

            // Si esta neurona ya ha ganado entonces pasa a la siguiente
            if (this.won[i] != 0) {

                continue;

            }

            // Si la neurona es la mejor de todas entonces es elegida
            if (output[i] > dist) {

                dist = output[i];
                which = i;

            }

        }

        System.out.println("La neurona forzada a ganar fue la #" + which);

        System.out.println();

        // Se ajustan los pesos de la neurona elegida
        for (int j = 0; j < input.getInputMatrix().getCols(); j++) {

            outputWeights.set(which, j, input.getInputMatrix().get(0,j));

        }

        normalizeWeight(outputWeights, which);
    }

    /**
     * Get the best error so far.
     * @return The best error so far.
     */
    public double getBestError() {
        return this.bestError;
    }

    /**
     * Get the error for this iteration.
     * @return The error for this iteration.
     */
    public double getTotalError() {
        return this.totalError;
    }

    /**
     * Called to initialize the SOM.
     */
    public void initialize() {

        this.som.getOutputWeights().ramdomize(-1, 1);

        System.out.println("Pesos generados de forma random");

        for (int i = 0; i < this.som.getOutputWeights().getRows(); i++){

            for (int j = 0; j < this.som.getOutputWeights().getCols(); j++){


                System.out.print(this.som.getOutputWeights().get(i, j) + ", ");

            }

            System.out.println();

        }

        for (int i = 0; i < this.outputNeuronCount; i++) {

            normalizeWeight(this.som.getOutputWeights(), i);

        }

        System.out.println("Pesos normalizados");

        for (int i = 0; i < this.som.getOutputWeights().getRows(); i++){

            for (int j = 0; j < this.som.getOutputWeights().getCols(); j++){


                System.out.print(this.som.getOutputWeights().get(i, j) + ", ");

            }

            System.out.println();

        }

    }

    /**
     * This method is called for each training iteration. Usually this method is
     * called from inside a loop until the error level is acceptable.
     */
    public void iteration() {

        evaluateErrors();

        this.totalError = this.globalError;

        // Si este es el mejor error entonces almacena los pesos
        if (this.totalError < this.bestError) {

            this.bestError = this.totalError;
            copyWeights(this.som, this.bestnet);

        }

        int winners = 0;
        for (int i = 0; i < this.won.length; i++) {

            if (this.won[i] != 0) {

                winners++;

            }

        }

        // Si hay menos ganadores que neuronas de salida y menos ganadores que los set de entrenamiento, se fuerza un ganador
        if ((winners < this.outputNeuronCount) && (winners < this.train.length)) {

            forceWin();
            return;

        }

        adjustWeights();

        if (this.learnRate > 0.01) {

            this.learnRate *= this.reduction;
        }

    }


    /**
     * Normalize the specified row in the weight matrix.
     * @param matrix The weight matrix.
     * @param row The row to normalize.
     */
    protected void normalizeWeight(final Matrix matrix, final int row) {
        // Normalización multiplicativa
        double len = MatrixMath.vectorLength(matrix.getRow(row));
        len = Math.max(len, SelfOrganizingMap.VERYSMALL);

        len = 1.0 / len;

        for (int i = 0; i < this.inputNeuronCount; i++) {

            matrix.set(row, i, matrix.get(row, i) * len);

        }
        matrix.set(row, this.inputNeuronCount, 0);

    }
}