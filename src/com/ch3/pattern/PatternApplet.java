package com.ch3.pattern;

import neural.hopfield.HopfieldNetwork;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.LongUnaryOperator;

/**
 * Chapter 3: Using a Hopfield Neural Network
 *
 * PatternApplet: An applet that displays an 8X8 grid that presents patterns to a Hopfield
 * neural network.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class PatternApplet extends JFrame implements MouseListener, ActionListener
{

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = 1882626251007826502L;

    public final int GRID_X = 6;
    public final int GRID_Y = 7;

    public final int CELL_WIDTH = 20;
    public final int CELL_HEIGHT = 20;

    private int marginX;
    private int marginY;

    public HopfieldNetwork hopfield;

    private boolean[] grid;
    private final Button buttonTrain;
    private final Button buttonGo;
    private final Button buttonClear;
    private final Button buttonClearMatrix;

    public void actionPerformed(final ActionEvent e) {

        if (e.getSource().equals(this.buttonClear)) {
            clear();
        } else if (e.getSource() == this.buttonClearMatrix) {
            clearMatrix();
        } else if (e.getSource() == this.buttonGo) {
            go();
        } else if (e.getSource() == this.buttonTrain) {
            train();
        }
    }

    /**
     * Clear the grid.
     */
    public void clear() {

        int index = 0;

        for (int y = 0; y < this.GRID_Y; y++) {
            for (int x = 0; x < this.GRID_X; x++) {
                this.grid[index++] = false;

            }

        }

        repaint();
    }

    /**
     * Clear the weight matrix.
     */
    private void clearMatrix() {
        this.hopfield.getMatrix().clear();
    }

    /**
     * Run the neural network.
     */
    public void go() {

        boolean[] letterA = {false, true, true, true, true, false, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, true, true, true, false, false, false, false, true, true, false, false, false, false, true, true, false, false, false, false, true};

        boolean[] letterB = {true, true, true, true, true, false, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, true, false, true, false, false, false, false, true, true, false, false, false, false, true, true, true, true, true, true, false };

        boolean[] letterC = {false, true, true, true, true, true, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, true, false, false, false, false, false, false, true, true, true, true, true};

        boolean[] letterD = {true, true, true, true, false, false, true, false, false, false, true, false, true, false, false, false, false, true, true, false, false, false, false, true, true, false, false, false, false, true, true, false, false, false, true, false, true, true, true, true, false, false};

        this.hopfield.train(letterA);
        this.hopfield.train(letterB);
        this.hopfield.train(letterC);
        this.hopfield.train(letterD);

        this.grid = this.hopfield.present(this.grid);

        double contA = 0;
        double contB = 0;
        double contC = 0;
        double contD = 0;

        for (int i = 0; i < 42; i++) {

            if (this.grid[i] && letterA[i]) {

                contA++;
            }

            if (this.grid[i] && letterB[i]) {

                contB++;
            }

            if (this.grid[i] && letterC[i]) {

                contC++;
            }

            if (this.grid[i] && letterD[i]) {

                contD++;
            }

        }

        //double[] porc = {(contA / 20) * 100,(contB / 23) * 100, (contC / 15) * 100, (contD / 18) * 100};
        double[] porc = {(contA / 42) * 100, (contB / 42) * 100, (contC / 42) * 100, (contD / 42) * 100};

        int indice = 0;

        double mayor = 0;

        System.out.println("**Porcentajes**");

        for (int i = 0; i < porc.length; i++) {

            System.out.println(porc[i]);

            if (porc[i] >= mayor){

                mayor = porc[i];
                indice = i;

            }

        }

        System.out.println("El indice es " + indice);

        switch (indice) {
            case 0 -> {
                System.out.println("Se parece a A por un " + porc[0] + "%");
                break;
            }
            case 1 -> {
                System.out.println("Se parece a B por un " + porc[1] + "%");
                break;
            }
            case 2 -> {
                System.out.println("Se parece a C por un " + porc[2] + "%");
                break;
            }
            case 3 -> {
                System.out.println("Se parece a D por un " + porc[3]+ "%");
                break;
            }
        }

        repaint();

    }

    public PatternApplet() {

        //Tamaño de la ventana
        setBounds(50,50,300,300);

        //Título
        setTitle("Hopfield Pattern");

        //Cerrar proceso al cerrar la ventana
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        this.grid = new boolean[this.GRID_X * this.GRID_Y];
        this.addMouseListener(this);
        this.buttonTrain = new Button("Train");
        this.buttonGo = new Button("Go");
        this.buttonClear = new Button("Clear");
        this.buttonClearMatrix = new Button("Clear Matrix");
        this.setLayout(new BorderLayout());
        Panel buttonPanel = new Panel();
        buttonPanel.add(this.buttonTrain);
        buttonPanel.add(this.buttonGo);
        buttonPanel.add(this.buttonClear);
        buttonPanel.add(this.buttonClearMatrix);
        this.add(buttonPanel, BorderLayout.SOUTH);

        this.buttonTrain.addActionListener(this);
        this.buttonGo.addActionListener(this);
        this.buttonClear.addActionListener(this);
        this.buttonClearMatrix.addActionListener(this);

        this.hopfield = new HopfieldNetwork(this.GRID_X * this.GRID_Y);

        setVisible(true);
    }

    public void mouseClicked(final MouseEvent event) {
        // not used

    }

    public void mouseEntered(final MouseEvent e) {
        // not used

    }

    public void mouseExited(final MouseEvent e) {
        // not used

    }

    public void mousePressed(final MouseEvent e) {
        // not used

    }

    public void mouseReleased(final MouseEvent e) {
        final int x = ((e.getX() - this.marginX) / this.CELL_WIDTH);
        final int y = ((e.getY() - this.marginY) / this.CELL_HEIGHT);

        if (((x >= 0) && (x < this.GRID_X)) && ((y >= 0) && (y < this.GRID_Y))) {
            final int index = (y * this.GRID_X) + x;

            //System.out.println("El valor de index es " + index);

            this.grid[index] = !this.grid[index];
        }

        repaint();

    }

    @Override
    public void paint(final Graphics g) {
        this.marginX = (this.getWidth() - (this.CELL_WIDTH * this.GRID_X)) / 2;
        this.marginY = (this.getHeight() - (this.CELL_HEIGHT * this.GRID_Y)) / 2;

        int index = 0;
        for (int y = 0; y < this.GRID_Y; y++) {
            for (int x = 0; x < this.GRID_X; x++) {

                if (this.grid[index++]) {

                    g.fillRect(this.marginX + (x * this.CELL_WIDTH),
                            this.marginY + (y * this.CELL_HEIGHT),
                            this.CELL_WIDTH,
                            this.CELL_HEIGHT);

                } else {

                    g.clearRect(this.marginX + (x * this.CELL_WIDTH),
                            this.marginY + (y * this.CELL_HEIGHT),
                            this.CELL_WIDTH,
                            this.CELL_HEIGHT);


                    g.drawRect(this.marginX + (x * this.CELL_WIDTH),
                            this.marginY + (y * this.CELL_HEIGHT),
                            this.CELL_WIDTH,
                            this.CELL_HEIGHT);
                }

            }
        }
    }

    /**
     * Train the neural network.
     */
    public void train() {

        this.hopfield.train(this.grid);

    }

    public static void main(String[] args) {

        new PatternApplet();

    }

}
