package com.ch9.predict;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chapter 9: Predictive Neural Networks
 *
 * ActualData: Holds values from the sine wave.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class ActualData {

    public static double sinDEG(final double deg) {

        final double rad = deg * (Math.PI / 180);
        final double result = Math.sin(rad);
        return ((int) (result * 100000.0)) / 100000.0;

    }

    private final double[] actual;
    private final int inputSize;

    private final int outputSize;

    public ActualData(final int size, final int inputSize, final int outputSize, final int aPredecir) {
        this.actual = new double[size + aPredecir];
        this.inputSize = inputSize;
        this.outputSize = outputSize;

        List<Double> lista = new ArrayList<>();

        try {
            File myObj = new File("/home/kevin_cb/Documentos/DatosClima");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                lista.add(Double.valueOf(data));
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        for (int i = 0; i < lista.size(); i++){
            this.actual[i] = lista.get(i) / 100;
        }

        /*int angle = 0;
        for (int i = 0; i < this.actual.length; i++) {
            this.actual[i] = sinDEG(angle);
            angle += 10;
        }*/
    }

    public void getInputData(final int offset, final double[] target) {
        for (int i = 0; i < this.inputSize; i++) {
            target[i] = this.actual[offset + i];
        }
    }

    public void getOutputData(final int offset, final double[] target) {
        for (int i = 0; i < this.outputSize; i++) {
            target[i] = this.actual[offset + this.inputSize + i];
        }
    }

    public void setActual(int index, double value){

        this.actual[index] = value;

    }

}
