package com.ejemplo;

import neural.activation.ActivationSigmoid;
import neural.matrix.Matrix;
import neural.matrix.MatrixMath;
import neural.util.ErrorCalculation;
import java.util.*;

public class BackPropagation {

    static ActivationSigmoid sigmoid = new ActivationSigmoid();

    static ErrorCalculation errorCalc = new ErrorCalculation();

    public static void main(String[] args) {

        double[][] inputData = {
                {0.8, -0.2, 1},
                {0.7, 0.005, 1},
                {1, 0.2, 1},
                {0.21, 0.43, 1},
                {-0.76, 0.55, 1},
                {0.12, -0.32, 1}
        };

        double[][] weightData = {
                {0.4, 0.2, 0.01},
                {0.1, 0.3, -0.5},
                {-0.2, -0.8, 0.72},
        };

        Matrix inputs = new Matrix(inputData);
        Matrix weights = new Matrix(weightData);

        double learningRate = 0.8;
        double momentum = 0.3;

        /*double hiddenNeurons = 2;
        double inputNeurons = 2;

        weights = NguyenWidow(weights, hiddenNeurons, inputNeurons);*/

        double error = 100.0;
        double epoca = 0;
        int epocas = 0;

        Scanner sc = new Scanner(System.in);
        System.out.println("**BackPropagation**");
        System.out.println("¿Como quieres que se realicen los cálculos? 1.-Épocas 2.-Error MSE");
        int opc = sc.nextInt();
        sc.nextLine();

        if (opc == 1){
            System.out.println("Ingrese el número de Épocas: ");
            epocas = sc.nextInt();
            sc.nextLine();

        }else{

            // 0.010109072701157 - 578
            // 0.000185983352908 - 7539
            System.out.println("Ingrese el grado de error MSE: ");
            error = sc.nextDouble();

        }

        double[] andIdeal = {1, 0, 0, 0};
        double[] andActual = new double[inputs.getRows()];

        double[] orIdeal = {1, 1, 1, 0};
        double[] orActual = new double[inputs.getRows()];

        double[] xorIdeal = {1, 1, 1, 0, 0, 0};
        double[] xorActual = new double[inputs.getRows()];

        double MSE;

        double[][] deltaWeightsData = new double[weights.getRows()][weights.getRows()];

        do {

            epoca++;

            for (int i = 0; i < inputs.getRows(); i++){

                andActual[i] = RNA_AND(inputs.getRow(i), weights.getRow(0));
                orActual[i] = RNA_OR(inputs.getRow(i), weights.getRow(1));
                xorActual[i] = RNA_XOR(inputs.getRow(i), weights);

            }

            errorCalc.reset();
            errorCalc.updateError(xorActual, xorIdeal);

            MSE = errorCalc.calculateMSE();

            double[] erroresXor = xorErrors(xorActual, xorIdeal);

            // Calculo de nodos Delta
            double[] deltaO1 = new double[inputs.getRows()];
            double[] deltaH1 = new double[inputs.getRows()];
            double[] deltaH2 = new double[inputs.getRows()];

            for (int i = 0; i < deltaO1.length; i++){

                deltaO1[i] = -erroresXor[i] * xorActual[i] * (1 - xorActual[i]);
                deltaH1[i] = andActual[i] * (1 - andActual[i]) * weights.get(2, 0) * deltaO1[i];
                deltaH2[i] = orActual[i] * (1 - orActual[i]) * weights.get(2,1) * deltaO1[i];

            }

            double[] gradients = new double[weights.getRows() * weights.getRows()];

            for (int i = 0; i < inputs.getRows(); i++){

                // A O1
                gradients[0] += deltaO1[i] * andActual[i]; // H1 -> O1
                gradients[1] += deltaO1[i] * orActual[i]; // H2 -> O1
                gradients[2] += deltaO1[i] * inputs.get(i, 2); // B2 -> O1

                // A H1
                gradients[3] += deltaH1[i] * inputs.get(i, 0); // I1 -> H1
                gradients[4] += deltaH1[i] * inputs.get(i, 1); // I2 -> H1
                gradients[5] += deltaH1[i] * inputs.get(i, 2); // B1 -> H1

                // A H2
                gradients[6] += deltaH2[i] * inputs.get(i, 0); // I1 -> H2
                gradients[7] += deltaH2[i] * inputs.get(i, 1); // I2 -> H2
                gradients[8] += deltaH2[i] * inputs.get(i, 2); // B1 -> H2

            }

            double[][] gradientSum = {
                    {gradients[3], gradients[4], gradients[5]},
                    {gradients[6], gradients[7], gradients[8]},
                    {gradients[0], gradients[1], gradients[2]}
            };

            for (int i = 0; i < deltaWeightsData.length; i++){

                for (int j = 0; j < deltaWeightsData[0].length; j++){

                    deltaWeightsData[i][j] = (learningRate * gradientSum[i][j]) + (momentum * deltaWeightsData[i][j]);

                }

            }

            Matrix deltaWeights = new Matrix(deltaWeightsData);

            weights = MatrixMath.add(weights, deltaWeights);

            System.out.println("Epoca " + epoca + " Error: " + MSE);

        } while(epoca < epocas || MSE > error);

        System.out.println("Resultados Red Neuronal");
        System.out.println("Número de épocas: " + epoca);

        for (int i = 0; i < inputs.getRows(); i++){
            System.out.println(
                    inputs.get(i,0) + "," + inputs.get(i,1)
                            + " actual = " + xorActual[i] + ", ideal = " + xorIdeal[i]
            );

        }

        System.out.printf("El MSE final fue de %.9f\n", MSE);

        System.out.println("**Matriz de Pesos Final**");
        for (int i = 0; i < weights.getRows(); i++){

            for (int j = 0 ; j < weights.getCols(); j++){

                System.out.print(weights.get(i, j) + ", ");

            }

            System.out.print("\n");

        }

        System.out.println("\n**Resultados con pesos nuevos**");
        for (int i = 0; i < inputs.getRows(); i++){

            andActual[i] = RNA_AND(inputs.getRow(i), weights.getRow(0));
            orActual[i] = RNA_OR(inputs.getRow(i), weights.getRow(1));
            xorActual[i] = RNA_XOR(inputs.getRow(i), weights);

            System.out.println(
                inputs.get(i, 0) +
                "\t" + inputs.get(i, 1) +
                "\t" + (RNA_XOR(inputs.getRow(i), weights) > 0.9 ? "Es un melon." : "Es una manzana.")
            );

        }

    }


    private static double[] xorErrors(double[] actual, double[] ideal){

        double[] errores = new double[actual.length];

        for (int i = 0; i < actual.length; i++){

            errores[i] = actual[i] - ideal[i];

        }

        return errores;
    }

    private static Matrix NguyenWidow(Matrix weights, double hiddenNeurons, double inputNeurons) {

        double beta = 0.7 * (Math.pow(hiddenNeurons, 1 / inputNeurons));

        double[] norms = new double[(int) hiddenNeurons];

        System.out.println("**Pesos**");
        for (int i = 0; i < hiddenNeurons; i++) {

            norms[i] = MatrixMath.vectorLength(weights.getRow(i));

            for (int j = 0; j < weights.getCols(); j++) {

                System.out.print("Peso[" + i +", " + j + "]: " + weights.get(i,j) + " - ");

                double weight = (beta * weights.get(i, j)) / norms[i];
                weights.set(i, j, weight);

                System.out.print(weights.get(i, j) + "\n");

            }

        }

        return weights;

    }

    private static double RNA_AND(Matrix input, Matrix weight){

        double[] threshold = {0.8};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_OR(Matrix input, Matrix weight){

        double[] threshold = {0.72};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_XOR(Matrix input, Matrix weight){

        double[] threshold = {0.6155803000154294};

        double[] sobranteData = {RNA_AND(input, weight.getRow(0)), RNA_OR(input, weight.getRow(1)), 1};

        Matrix sobrante = Matrix.createRowMatrix(sobranteData);

        double pp = MatrixMath.dotProduct(sobrante, weight.getRow(2));

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado == threshold[0] ? 1 : 0;
        return resultado;
    }

}
