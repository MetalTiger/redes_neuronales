package com.proyecto;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.Serial;
import java.text.NumberFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import neural.matrix.Matrix;
import neural.som.SelfOrganizingMap;
import neural.som.TrainSelfOrganizingMap;
import neural.som.NormalizeInput.NormalizationType;
import neural.som.TrainSelfOrganizingMap.LearningMethod;
import neural.util.SerializeObject;

public class Proyecto extends JFrame implements Runnable {


    /**
     * How many input neurons to use.
     */
    public static final int INPUT_COUNT = 6;

    /**
     * How many output neurons to use.
     */
    public static final int OUTPUT_COUNT = 3;

    /**
     * How many random samples to generate.
     */
    public static final int SAMPLE_COUNT = 20000;

    /**
     * Startup the program.
     *
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) throws IOException, ClassNotFoundException {

        boolean entrenar = false;

        final Proyecto app = new Proyecto();
        app.setVisible(true);

        if (entrenar){

            final Thread t = new Thread(app);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();

        } else {

            loadNetwork();

            probarDatos();

        }



    }

    private static void probarDatos() {

        for (int i = 0; i < 5; i++){

            System.out.print("Persona " + i + " Datos: ");

            double[] respuestas = new double[6];

            /*respuestas[0] = 0.7;
            respuestas[1] = 0.7;
            respuestas[2] = 0.3;
            respuestas[3] = 0.3;
            respuestas[4] = 0.8;
            respuestas[5] = 0.8;*/


            for (int j = 0; j < respuestas.length; j++) {
                respuestas[j] = Math.random();

                System.out.print(respuestas[j] + ", ");
            }

            int winnerNeuron = net.winner(respuestas);

            //String mensaje = "A la persona " + i + " se le recomendará el canal de ";
            String mensaje = "se le recomendará el canal de ";

            switch (winnerNeuron) {

                case 0:
                    mensaje += "Deportes";
                    break;

                case 1:
                    mensaje += "Noticias";
                    break;

                case 2:
                    mensaje += "Caricaturas";
                    break;

            }

            System.out.print(mensaje);
            System.out.println();

        }

    }

    /**
     * The unit length in pixels, which is the max of the height and width of
     * the window.
     */
    protected int unitLength;

    /**
     * How many retries so far.
     */
    protected int retry = 1;

    /**
     * The current error percent.
     */
    protected double totalError = 0;
    /**
     * The best error percent.
     */
    protected double bestError = 0;

    /**
     * The neural network.
     */

    protected static SelfOrganizingMap net;
    protected double[][] input;

    /**
     * The offscreen image. Used to prevent flicker.
     */
    protected Image offScreen;

    /**
     * The constructor sets up the position and size of the window.
     */
    Proyecto() {
        setTitle("Training a Self Organizing Map Neural Network");
        setSize(400, 450);

        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getScreenSize();

        setLocation(
                (int) (d.width - this.getSize().getWidth()) / 2,
                (int) (d.height - this.getSize().getHeight()) / 2
        );

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    /**
     * Display the progress of the neural network.
     *
     * @param g
     *            A graphics object.
     */
    @Override
    public void paint(Graphics g) {

        if (this.net == null) {

            return;

        }

        if (this.offScreen == null) {

            this.offScreen = this.createImage(
                    (int) getBounds().getWidth(),
                    (int) getBounds().getHeight()
            );

        }

        g = this.offScreen.getGraphics();
        final int width = getContentPane().getWidth();
        final int height = getContentPane().getHeight();
        this.unitLength = Math.min(width, height);
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        // plot the weights of the output neurons
        g.setColor(Color.white);
        final Matrix outputWeights = this.net.getOutputWeights();

        for (int y = 0; y < outputWeights.getRows(); y++) {

            /*// Mostrar pesos directamente
            System.out.println("Pesos Neurona " + y);
            System.out.println("Peso I1 " + outputWeights.get(y, 0));
            System.out.println("Peso I2 " + outputWeights.get(y, 1));

            System.out.println("Posicion I1 " + (outputWeights.get(y, 0) * this.unitLength));
            System.out.println("Posicion I2 " + (outputWeights.get(y, 1) * this.unitLength));
            */


            g.fillRect(
                    (int) (outputWeights.get(y, 0) * this.unitLength),
                    (int) (outputWeights.get(y, 1) * this.unitLength),
                    10,
                    10

            );

            g.setColor(Color.MAGENTA);

            g.drawString("#" + y,
                    (int) (outputWeights.get(y, 0) * this.unitLength) - 18,
                    (int) (outputWeights.get(y, 1) * this.unitLength) + 10
            );

            /*//Mostrar posición de la neurona en base a sus pesos
            g.drawString((outputWeights.get(y, 0) * this.unitLength) + ", " + (outputWeights.get(y, 1) * this.unitLength),
                    (int) (outputWeights.get(y, 0) * this.unitLength) - 18,
                    (int) (outputWeights.get(y, 1) * this.unitLength) + 20
            );
            */

            g.setColor(Color.white);

        }

        // plot a grid of samples to test the net with
        g.setColor(Color.green);

        int separadorX = 20;
        int separadorY = 10;

        for (int i = 0; i < 10; i++){

            System.out.print("Persona " + i + " Datos: ");

            double[] respuestas = new double[6];

            for (int j = 0; j < respuestas.length; j++) {
                respuestas[j] = Math.random();

                System.out.print(respuestas[j] + ", ");
            }

            int winnerNeuron = this.net.winner(respuestas);

            //String mensaje = "A la persona " + i + " se le recomendará el canal de ";
            String mensaje = "se le recomendará el canal de ";

            switch (winnerNeuron){

                case 0:
                    mensaje += "0";
                    break;

                case 1:
                    mensaje += "1";
                    break;

                case 2:
                    mensaje += "2";
                    break;

            }

            final int x2 = (int) (outputWeights.get(winnerNeuron, 0) * this.unitLength);
            final int y2 = (int) (outputWeights.get(winnerNeuron, 1) * this.unitLength);

            g.fillOval(x2 + separadorX, y2 + separadorY, 5, 5);
            g.setColor(Color.white);

            g.drawString(String.valueOf(i),
                    x2 + separadorX,
                    y2 + separadorY
            );

            g.setColor(Color.green);

            g.drawLine(x2 + separadorX, y2 + separadorY, x2, y2);

            System.out.print(mensaje);
            System.out.println();

            separadorX += 10;

        }

        /*for (int y = 0; y < this.unitLength; y += 100) {

            for (int x = 0; x < this.unitLength; x += 100) {

                g.fillOval(x, y, 5, 5);
                final double[] d = new double[INPUT_COUNT];
                d[0] = x;
                d[1] = y;

                final int c = this.net.winner(d);

                //System.out.println("Posiciones X: " + x + ", Y: " + y + " pertenecen a la neurona #" + c);

                if (c == 0 || c == 1){

                    g.setColor(Color.white);

                    g.drawString(x + "," + y,
                            x,
                            y + 10
                    );

                }

                g.setColor(Color.green);

                final int x2 = (int) (outputWeights.get(c, 0) * this.unitLength);
                final int y2 = (int) (outputWeights.get(c, 1) * this.unitLength);

                g.drawLine(x, y, x2, y2);


            }

        }*/

        // display the status info
        g.setColor(Color.white);
        final NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);

        g.drawString("retry = " + this.retry + ",current error = "
                        + nf.format(this.totalError * 100) + "%, best error = "
                        + nf.format(this.bestError * 100) + "%",
                0,
                (int) getContentPane().getBounds().getHeight()
        );

        getContentPane().getGraphics().drawImage(this.offScreen, 0, 0, this);

    }

    /**
     * Called to run the background thread. The background thread sets up the
     * neural network and training data and begins training the network.
     */
    public void run() {
        // build the training set
        this.input = new double[SAMPLE_COUNT][INPUT_COUNT];

        //System.out.println("Training Set");
        for (int i = 0; i < SAMPLE_COUNT; i++) {

            for (int j = 0; j < INPUT_COUNT; j++) {
                this.input[i][j] = Math.random();

                //System.out.print(this.input[i][j] + ",");

            }
            System.out.println();

        }

        System.out.println("Training set (Matriz) de: " + input.length + " x " + input[0].length);

        // build and train the neural network
        this.net = new SelfOrganizingMap(INPUT_COUNT, OUTPUT_COUNT, NormalizationType.MULTIPLICATIVE);

        final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
                this.net, this.input, LearningMethod.SUBTRACTIVE,0.3
        );

        train.initialize();

        double lastError = Double.MAX_VALUE;
        int errorCount = 0;

        while (errorCount < 25) {

            train.iteration();
            this.retry++;
            System.out.println("Época " + retry);
            this.totalError = train.getTotalError();
            this.bestError = train.getBestError();
            System.out.println("Error " + this.bestError);
            paint(getGraphics());

            if (this.bestError < lastError) {

                lastError = this.bestError;
                errorCount = 0;

            } else {

                errorCount++;

            }

        }

        System.out.println("*Weights*");
        Matrix pesos = this.net.getOutputWeights();

        System.out.println("Matriz de " + pesos.getRows() + " x " + pesos.getCols());


        for (int i = 0; i < pesos.getRows(); i++){

            System.out.print("Pesos de neurona #" + i + ": ");

            for (int j = 0; j < pesos.getCols(); j++){

                if (j == 0){

                    System.out.print(pesos.get(i, j) + "(x= " + (pesos.get(i, j) * this.unitLength) + ")" + ", ");

                }else{

                    System.out.print(pesos.get(i, j) + "(y= " + (pesos.get(i, j) * this.unitLength) + ")" + ", ");

                }

            }

            System.out.println();

        }

        try {
            SerializeObject.save("./proyecto.net", net);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void loadNetwork() throws IOException, ClassNotFoundException {

        SelfOrganizingMap mapa = (SelfOrganizingMap) SerializeObject.load("./proyecto.net");

        net = mapa;

    }

}
