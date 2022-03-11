package com.ejemplo;

import neural.activation.ActivationSigmoid;
import neural.matrix.Matrix;
import neural.matrix.MatrixMath;
import neural.matrix.BiPolarUtil;
import neural.util.ErrorCalculation;

import java.util.*;

public class Main {

    static ActivationSigmoid sigmoide = new ActivationSigmoid();

    static ErrorCalculation errorCalc = new ErrorCalculation();

    public static void main(String[] args) {

        double learningRate = 0.7;
        double momentum = 0.3;

        double[][] inputData = {{0, 0, 1}, {0, 1, 1}, {1, 0, 1}, {1, 1, 1}};

        double[][] weightData = {{-0.07, 0.22, -0.46}, {0.94, 0.46, 0.1}, {0.58, -0.22, 0.78}, {-1, 1, 0}};

        Matrix inputs = new Matrix(inputData);
        Matrix weights = new Matrix(weightData);

        double hiddenNeurons = 2;
        double inputNeurons = 2;

        //weights = NguyenWidow(weights, hiddenNeurons, inputNeurons);

        double[] andIdeal = {0, 0, 0, 1};
        double[] andActual = new double[4];

        double[] orIdeal = {0, 1, 1, 1};
        double[] orActual = new double[4];

        double[] xorIdeal = {0, 1, 1, 0};
        double[] xorActual = new double[4];

        System.out.println("A\tB\tAND\tOR\t¬A\t¬B\tXOR");

        for (int i = 0; i < inputs.getRows(); i++){

            andActual[i] = RNA_AND(inputs.getRow(i), weights.getRow(0));
            //System.out.println("AND sigmoid " + i + ": " + andActual[i]);
            orActual[i] = RNA_OR(inputs.getRow(i), weights.getRow(1));
            xorActual[i] = RNA_XOR(inputs.getRow(i), weights);

            System.out.println(
                    ((int) inputs.get(i, 0)) +
                    "\t" + ( (int) inputs.get(i, 1)) +
                    "\t" + (RNA_AND(inputs.getRow(i), weights.getRow(0)) > 0.8 ? 1 : 0) +
                    "\t" + (RNA_OR(inputs.getRow(i), weights.getRow(1)) > 0.72 ? 1 : 0) +
                    "\t" + RNA_NA(inputs.get(i,0), weights.getRow(3)) +
                    "\t" + RNA_NA(inputs.get(i,1), weights.getRow(3)) +
                    "\t" + RNA_XOR(inputs.getRow(i), weights)
            );

        }

        System.out.println("**Errores**");
        errorCalc.updateError(andActual, andIdeal);
        System.out.print("AND\tMSE: " + (errorCalc.calculateMSE() * 100) + " %,\tESS: " + errorCalc.calculateESS() + ",\tRMS: " + (errorCalc.calculateRMS() * 100) + " %\n");
        errorCalc.reset();
        errorCalc.updateError(orActual, orIdeal);
        System.out.print("OR\tMSE: " + (errorCalc.calculateMSE() * 100) + " %,\tESS: " + errorCalc.calculateESS() + ",\tRMS: " + (errorCalc.calculateRMS() * 100) + " %\n");
        errorCalc.reset();
        errorCalc.updateError(xorActual, xorIdeal);
        System.out.print("XOR\tMSE: " + (errorCalc.calculateMSE() * 100) + " %,\tESS: " + errorCalc.calculateESS() + ",\tRMS: " + (errorCalc.calculateRMS() * 100) + " %\n");

        //patternMatch();
        //Hopfield_NA();

        /*List<String> lista = new ArrayList<String>(){
            {
                add("asd");
                add("asd");
                add("3");

            }

        };

        String a = "el, asd, hace. asd";

        a = a.toLowerCase(Locale.ROOT).replaceAll("[^a-zA-Z]", " ");

        a = a.replaceAll("\\s+", " ");

        String[] palabras = a.split(" ");

        for (String palabra: palabras) {

            System.out.println(palabra);

        }

        Map<String, Integer> mapa = new HashMap<String, Integer>();

        for (String elemento: lista) {

            if (!mapa.containsKey(elemento)){

                mapa.put(elemento, 1);

            }else{

                mapa.put(elemento, mapa.get(elemento) + 1);

            }

        }

        for (int i = 0; i < lista.size(); i++){
            if (!mapa.containsKey(lista.get(i))){

                mapa.put(lista.get(i), 1);

            }else{

                mapa.put(lista.get(i), mapa.get(lista.get(i)) + 1);

            }

        }

        System.out.println(mapa);
*/

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

    private static void patternMatch() {

        //double [] entradaData = {1, 0, 1, 0, 1, 1}; No match
        double [] entradaData = {1, 1, 0, 1, 0, 1}; //SI match
        //double [] entradaData = {0, 0, 1, 0, 1, 0}; // SI match

        Matrix entrada = Matrix.createColumnMatrix(entradaData);

        double[][] redData = {
                {0, 1, -1, 1, -1, 1}, {1, 0, -1, 1, -1, 1},
                {-1, -1, 0, -1, 1, -1}, {1, 1, -1, 0, -1, 1},
                {-1, -1, 1, -1, 0, -1}, {1, 1, -1, 1, -1, 0}
        };

        Matrix red = new Matrix(redData);

        Matrix resultado = MatrixMath.multiply(red, entrada);

        for (int i = 0; i < resultado.getRows(); i++){

            if (resultado.get(i,0) > 0){

                resultado.set(i,0, 1);

            }else{

                resultado.set(i,0, 0);

            }

            System.out.println(resultado.get(i,0));

        }

        if (resultado.equals(entrada)) {

            System.out.println("Si reconoce el patrón.");

        }else{

            System.out.println("No reconoce el patrón.");

        }

    }

    private static double RNA_AND(Matrix input, Matrix weight){

        double[] threshold = {0.8};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoide.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_OR(Matrix input, Matrix weight){

        double[] threshold = {0.72};

        double pp = MatrixMath.dotProduct(input, weight);

        double resultado = sigmoide.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado > threshold[0] ? 1 : 0;
        return resultado;

    }

    private static double RNA_XOR(Matrix input, Matrix weight){

        double[] threshold = {0.6155803000154294};

        double[] sobranteData = {RNA_AND(input, weight.getRow(0)), RNA_OR(input, weight.getRow(1)), 1};

        Matrix sobrante = Matrix.createRowMatrix(sobranteData);

        double pp = MatrixMath.dotProduct(sobrante, weight.getRow(2));

        double resultado = sigmoide.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        //return resultado == threshold[0] ? 1 : 0;
        return resultado;
    }

    private static int RNA_NA(double input, Matrix weight){

        double[] threshold = {0.5};

        input = BiPolarUtil.toBiPolar(input); // Se convierte a bipolar

        double[] inputsData = {input, 1, 0};

        Matrix inputs = Matrix.createRowMatrix(inputsData);

        double pp = MatrixMath.dotProduct(inputs, weight);

        double resultado = sigmoide.activationFunction(pp);

        //System.out.println("El resultado es " + resultado);

        return resultado > threshold[0] ? 1 : 0;
    }

    private static void Hopfield_NA(){

        Scanner sc = new Scanner(System.in);

        System.out.println("**Hopfield**");

        System.out.println("Ingrese el patrón inicial: ");

        String cadena = sc.nextLine();

        double[] inputData = new double[cadena.length()];
        double[] inputBiData = new double[cadena.length()];

        System.out.println("asdasd" + inputBiData.toString());


        // Paso 1.- Convertir a Bipolar
        for (int i = 0; i < cadena.length(); i++) {

            inputData[i] = Double.parseDouble( cadena.substring(i, i + 1) );
            inputBiData[i] = BiPolarUtil.toBiPolar(inputData[i]);

        }

        Matrix input = Matrix.createColumnMatrix(inputData);
        Matrix inputBi = Matrix.createColumnMatrix(inputBiData);

        // Paso 2.- Obtener la transpuesta y multiplicar
        Matrix inputBiTrans = MatrixMath.transpose(inputBi);
        Matrix matriz = MatrixMath.multiply(inputBi, inputBiTrans);

        // Paso 3.- Restar la matriz identidad
        Matrix matrizIdentidad = MatrixMath.identity(input.getRows());

        Matrix RN = MatrixMath.subtract(matriz, matrizIdentidad);

        System.out.println("**Red Neuronal**");

        for (int i = 0; i < RN.getRows(); i++) {

            for (int j = 0; j < RN.getCols(); j++) {

                System.out.print(RN.get(i, j) + "  ");

            }

            System.out.println("");

        }

        // Prueba
        int opc = 1;

        while(opc == 1){

            System.out.println("**Reconocimiento de Patrones**");
            System.out.println("Introduzca el patrón a comprobar: ");
            String userPattern = sc.nextLine();

            if (userPattern.length() == cadena.length()){

                double[] patternData = new double[userPattern.length()];
                double[] patternBiData = new double[userPattern.length()];

                for (int i = 0; i < userPattern.length(); i++) {

                    patternData[i] = Double.parseDouble( userPattern.substring(i, i + 1) );
                    patternBiData[i] = BiPolarUtil.toBiPolar(patternData[i]);

                }

                Matrix pattern = Matrix.createColumnMatrix(patternData);
                Matrix patternBi = Matrix.createColumnMatrix(patternBiData);

                Matrix resultado = MatrixMath.multiply(RN,patternBi);

                for (int i = 0; i < resultado.getRows(); i++){

                    if (resultado.get(i,0) > 0){

                        resultado.set(i,0, 1);

                    }else{

                        resultado.set(i,0, 0);

                    }

                    //System.out.println(resultado.get(i,0));

                }

                if (resultado.equals(pattern)) {

                    System.out.println("Si reconoce el patrón.");

                }else{

                    System.out.println("No reconoce el patrón.");

                }


            }else{

                System.out.println("El patron ingresado no es del mismo tamaño que el patrón inicial.");

            }

            System.out.println("¿Quiere probar con otro patrón? 1.- Si 2.- No");
            opc = sc.nextInt();
            sc.nextLine();

        }


    }

}
