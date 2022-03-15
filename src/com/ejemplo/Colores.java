package com.ejemplo;

import neural.activation.ActivationSigmoid;
import neural.matrix.Matrix;
import neural.matrix.MatrixMath;
import neural.util.ErrorCalculation;

import java.util.Scanner;

public class Colores {

    static ActivationSigmoid sigmoid = new ActivationSigmoid();

    static ErrorCalculation errorCalc = new ErrorCalculation();

    public static void main(String[] args) {

        double[][] inputData = {
                {0, 0, 0, 1},
                {1, 1, 1, 1},
                {0, 1, 0, 1},
                {0, 0.43, 1, 1},
                {1, 0, 0, 1}
        };

        double[][] weightData = {
                {-0.27, 0.32, 0.37, -0.46},
                {0.84, 0.49, -0.78, 0.16},
                {0.24, -0.89, 0.17, 0.53},
                {-0.42, 0.59, 0.91, 0.74}
        };

        Matrix inputs = new Matrix(inputData);
        Matrix weights = new Matrix(weightData);

        double learningRate = 0.7;
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

        double[] H1Ideal = {1, 0, 0, 0};
        double[] H1Actual = new double[5];

        double[] H2Ideal = {1, 1, 1, 0};
        double[] H2Actual = new double[5];

        double[] H3Ideal = {1, 1, 1, 0};
        double[] H3Actual = new double[5];

        double[] coloresIdeal = {1, 0, 0, 1, 1};
        double[] coloresActual = new double[5];

        double MSE;

        double[][] deltaWeightsData = new double[weights.getRows()][weights.getRows()];

        do {

            epoca++;

            for (int i = 0; i < inputs.getRows(); i++){

                H1Actual[i] = RNA_H1(inputs.getRow(i), weights.getRow(0));
                H2Actual[i] = RNA_H2(inputs.getRow(i), weights.getRow(1));
                H3Actual[i] = RNA_H3(inputs.getRow(i), weights.getRow(2));
                coloresActual[i] = RNA_O1(inputs.getRow(i), weights);

            }

            errorCalc.reset();
            errorCalc.updateError(coloresActual, coloresIdeal);

            MSE = errorCalc.calculateMSE();

            double[] erroresColor = colorErrors(coloresActual, coloresIdeal);

            // Calculo de nodos Delta
            double[] deltaO1 = new double[inputs.getRows()];
            double[] deltaH1 = new double[inputs.getRows()];
            double[] deltaH2 = new double[inputs.getRows()];
            double[] deltaH3 = new double[inputs.getRows()];

            for (int i = 0; i < deltaO1.length; i++){

                deltaO1[i] = -erroresColor[i] * coloresActual[i] * (1 - coloresActual[i]);
                deltaH1[i] = H1Actual[i] * (1 - H1Actual[i]) * weights.get(weights.getRows() - 1, 0) * deltaO1[i];
                deltaH2[i] = H2Actual[i] * (1 - H2Actual[i]) * weights.get(weights.getRows() - 1,1) * deltaO1[i];
                deltaH3[i] = H3Actual[i] * (1 - H3Actual[i]) * weights.get(weights.getRows() - 1,2) * deltaO1[i];

            }

            double[] gradients = new double[weights.getRows() * weights.getRows()];

            for (int i = 0; i < inputs.getRows(); i++){

                // A O1
                gradients[0] += deltaO1[i] * H1Actual[i]; // H1 -> O1
                gradients[1] += deltaO1[i] * H2Actual[i]; // H2 -> O1
                gradients[2] += deltaO1[i] * H3Actual[i]; // H3 -> O1
                gradients[3] += deltaO1[i] * inputs.get(i, 3); // B2 -> O1

                // A H1
                gradients[4] += deltaH1[i] * inputs.get(i, 0); // I1 -> H1
                gradients[5] += deltaH1[i] * inputs.get(i, 1); // I2 -> H1
                gradients[6] += deltaH1[i] * inputs.get(i, 2); // I3 -> H1
                gradients[7] += deltaH1[i] * inputs.get(i, 3); // B1 -> H1

                // A H2
                gradients[8] += deltaH2[i] * inputs.get(i, 0); // I1 -> H2
                gradients[9] += deltaH2[i] * inputs.get(i, 1); // I2 -> H2
                gradients[10] += deltaH2[i] * inputs.get(i, 2); // I3 -> H2
                gradients[11] += deltaH2[i] * inputs.get(i, 3); // B1 -> H2

                // A H3
                gradients[12] += deltaH3[i] * inputs.get(i, 0); // I1 -> H3
                gradients[13] += deltaH3[i] * inputs.get(i, 1); // I2 -> H3
                gradients[14] += deltaH3[i] * inputs.get(i, 2); // I3 -> H3
                gradients[15] += deltaH3[i] * inputs.get(i, 3); // B1 -> H3

            }

            double[][] gradientSum = {
                    {gradients[4], gradients[5], gradients[6], gradients[7]},
                    {gradients[8], gradients[9], gradients[10], gradients[11]},
                    {gradients[12], gradients[13], gradients[14], gradients[15]},
                    {gradients[0], gradients[1], gradients[2], gradients[3]}
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
                    inputs.get(i,0) + "," + inputs.get(i,1) + ", " + inputs.get(i,2)
                            + "\tactual = " + coloresActual[i] + ", ideal = " + coloresIdeal[i]
            );

        }

        System.out.printf("El MSE final fue de %.9f\n", MSE);

        System.out.println("**Matriz de Pesos Final**");
        for (int i = 0; i < weights.getRows(); i++){

            System.out.print("{");

            for (int j = 0 ; j < weights.getCols(); j++){

                if (j == (weights.getCols() - 1)){

                    System.out.print(weights.get(i, j));

                }else{

                    System.out.print(weights.get(i, j) + ", ");

                }

            }

            System.out.print("},\n");

        }


        System.out.println("**Resultados con pesos nuevos**");
        System.out.println("A\tB\tC ");
        for (int i = 0; i < inputs.getRows(); i++){

            H1Actual[i] = RNA_H1(inputs.getRow(i), weights.getRow(0));
            H2Actual[i] = RNA_H2(inputs.getRow(i), weights.getRow(1));
            H3Actual[i] = RNA_H3(inputs.getRow(i), weights.getRow(2));
            coloresActual[i] = RNA_O1(inputs.getRow(i), weights);

            System.out.println(
                    ((int) inputs.get(i, 0)) +
                    "\t" + ((int) inputs.get(i, 1)) +
                    "\t" + ((int) inputs.get(i,2)) +
                    "\tActual= " + RNA_O1(inputs.getRow(i), weights) +
                    ",\tIdeal= " + coloresIdeal[i]
            );

        }

    }

    private static double[] colorErrors(double[] actual, double[] ideal){

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

    private static double RNA_H1(Matrix input, Matrix weight){

        double[] threshold = {0.8};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_H2(Matrix input, Matrix weight){

        double[] threshold = {0.72};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_H3(Matrix input, Matrix weight){

        double[] threshold = {0.8};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_O1(Matrix input, Matrix weight){

        double[] threshold = {0.6155803000154294};

        double[] sobranteData = {
                RNA_H1(input, weight.getRow(0)),
                RNA_H2(input, weight.getRow(1)),
                RNA_H3(input, weight.getRow(2)),
                1
        };

        Matrix sobrante = Matrix.createRowMatrix(sobranteData);

        double pp = MatrixMath.dotProduct(sobrante, weight.getRow(3));

        double resultado = sigmoid.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado == threshold[0] ? 1 : 0;
        return resultado;
    }


}
