package com.ch12.ocr;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import neural.som.SelfOrganizingMap;
import neural.som.TrainSelfOrganizingMap;
import neural.som.NormalizeInput.NormalizationType;
import neural.som.TrainSelfOrganizingMap.LearningMethod;

/**
 * Chapter 12: OCR and the Self Organizing Map
 *
 * OCR: Main form that allows the user to interact with the OCR application.
 *
 * @author Jeff Heaton
 * @version 2.1
 */
public class OCR extends JFrame implements Runnable {

    class SymAction implements java.awt.event.ActionListener {
        public void actionPerformed(final java.awt.event.ActionEvent event) {
            final Object object = event.getSource();
            if (object == OCR.this.downSample) {
                downSample_actionPerformed(event);
            } else if (object == OCR.this.clear) {
                clear_actionPerformed(event);
            } else if (object == OCR.this.add) {
                add_actionPerformed(event);
            } else if (object == OCR.this.del) {
                del_actionPerformed(event);
            } else if (object == OCR.this.load) {
                load_actionPerformed(event);
            } else if (object == OCR.this.save) {
                save_actionPerformed(event);
            } else if (object == OCR.this.train) {
                train_actionPerformed(event);
            } else if (object == OCR.this.recognize) {
                recognize_actionPerformed(event);
            } else if (object == OCR.this.image) {
                image_actionPerformed(event);
            }else if (object == OCR.this.fChooser) {
                filechooser_actionPerformed(event);
            }
        }
    }

    class SymListSelection implements javax.swing.event.ListSelectionListener {
        public void valueChanged(
                final javax.swing.event.ListSelectionEvent event) {
            final Object object = event.getSource();
            if (object == OCR.this.letters) {
                letters_valueChanged(event);
            }
        }
    }

    public class UpdateStats implements Runnable {
        long _tries;
        double _lastError;
        double _bestError;

        public void run() {
            OCR.this.tries.setText("" + this._tries);
            OCR.this.lastError.setText("" + this._lastError);
            OCR.this.bestError.setText("" + this._bestError);
        }
    }

    /**
     * Serial id for this class.
     */
    private static final long serialVersionUID = -6779380961875907013L;

    /**
     * The downsample width for the application.
     */
    static final int DOWNSAMPLE_WIDTH = 50; // Original: 5. Mod: 50

    /**
     * The down sample height for the application.
     */
    static final int DOWNSAMPLE_HEIGHT = 50; // Original: 7. Mod: 50

    static final double MAX_ERROR = 0.01;

    /**
     * The main method.
     *
     * @param args
     *            Args not really used.
     */
    public static void main(final String[] args) { (new OCR()).setVisible(true); }

    /**
     * The entry component for the user to draw into.
     */
    boolean halt;

    /**
     * The entry component for the user to draw into.
     */
    Entry entry;

    /**
     * The down sample component to display the drawing downsampled.
     */
    Sample sample;

    /**
     * The letters that have been defined.
     */
    DefaultListModel letterListModel = new DefaultListModel();
    /**
     * The neural network.
     */
    SelfOrganizingMap net;
    /**
     * The background thread used for training.
     */
    Thread trainThread = null;

    // {{DECLARE_CONTROLS
    javax.swing.JLabel JLabel1 = new javax.swing.JLabel();

    javax.swing.JLabel JLabel2 = new javax.swing.JLabel();

    /**
     * THe downsample button.
     */
    javax.swing.JButton downSample = new javax.swing.JButton();

    /**
     * The add button.
     */
    javax.swing.JButton add = new javax.swing.JButton();
    /**
     * The clear button
     */
    javax.swing.JButton clear = new javax.swing.JButton();
    /**
     * The Image button
     */
    javax.swing.JButton image = new javax.swing.JButton();
    /**
     * The File Chooser button
     */
    javax.swing.JButton fChooser = new javax.swing.JButton();

    /**
     * The recognize button
     */
    javax.swing.JButton recognize = new javax.swing.JButton();

    javax.swing.JScrollPane JScrollPane1 = new javax.swing.JScrollPane();

    /**
     * The letters list box
     */
    javax.swing.JList letters = new javax.swing.JList();

    /**
     * The delete button
     */
    javax.swing.JButton del = new javax.swing.JButton();

    /**
     * The load button
     */
    javax.swing.JButton load = new javax.swing.JButton();
    /**
     * The save button
     */
    javax.swing.JButton save = new javax.swing.JButton();
    /**
     * The train button
     */
    javax.swing.JButton train = new javax.swing.JButton();

    javax.swing.JLabel JLabel3 = new javax.swing.JLabel();

    javax.swing.JLabel JLabel4 = new javax.swing.JLabel();

    /**
     * How many tries
     */
    javax.swing.JLabel tries = new javax.swing.JLabel();
    /**
     * The last error
     */
    javax.swing.JLabel lastError = new javax.swing.JLabel();
    /**
     * The best error
     */
    javax.swing.JLabel bestError = new javax.swing.JLabel();

    javax.swing.JLabel JLabel8 = new javax.swing.JLabel();

    javax.swing.JLabel JLabel5 = new javax.swing.JLabel();

    // }}
    // {{DECLARE_MENUS
    // }}

    /**
     * The constructor.
     */
    OCR() {
        getContentPane().setLayout(null);
        this.entry = new Entry();   //Panel donde se dibuja
        this.entry.setLocation(168, 25);
        this.entry.setSize(200, 128);
        getContentPane().add(this.entry);

        this.sample = new Sample(DOWNSAMPLE_WIDTH, DOWNSAMPLE_HEIGHT);  // Panel de recuadros
        this.sample.setLocation(400, 25); // Original: 307, 210. Mod: 400, 25
        this.sample.setSize(500, 500); // Original: 65, 70  Mod: 500, 500

        this.entry.setSample(this.sample);
        getContentPane().add(this.sample);

        // {{INIT_CONTROLS
        setTitle("Java Neural Network");
        getContentPane().setLayout(null);
        setSize(950, 580);
        setVisible(false);


        this.JLabel1.setText("Letters Known");
        getContentPane().add(this.JLabel1);
        this.JLabel1.setBounds(12, 12, 84, 12);

        this.JLabel2.setText("Tries:");
        getContentPane().add(this.JLabel2);
        this.JLabel2.setBounds(12, 264, 72, 24);

        this.downSample.setText("Down Sample");
        this.downSample.setActionCommand("Down Sample");
        getContentPane().add(this.downSample);
        this.downSample.setBounds(252, 180, 120, 24);

        this.add.setText("Add");
        this.add.setActionCommand("Add");
        getContentPane().add(this.add);
        this.add.setBounds(168, 156, 84, 24);

        this.clear.setText("Clear");
        this.clear.setActionCommand("Clear");
        getContentPane().add(this.clear);
        this.clear.setBounds(168, 180, 84, 24);

        this.image.setText("Image");
        this.image.setActionCommand("Image");
        getContentPane().add(this.image);
        this.image.setBounds(168, 204, 84, 24);

        this.fChooser.setText("Abrir");
        this.fChooser.setActionCommand("Abrir");
        getContentPane().add(this.fChooser);
        this.fChooser.setBounds(252, 204, 120, 24);

        this.recognize.setText("Recognize");
        this.recognize.setActionCommand("Recognize");
        getContentPane().add(this.recognize);
        this.recognize.setBounds(252, 156, 120, 24);

        this.JScrollPane1
                .setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.JScrollPane1.setOpaque(true);
        getContentPane().add(this.JScrollPane1);
        this.JScrollPane1.setBounds(12, 24, 144, 132);
        this.JScrollPane1.getViewport().add(this.letters);

        this.letters.setBounds(0, 0, 126, 129);

        this.del.setText("Delete");
        this.del.setActionCommand("Delete");
        getContentPane().add(this.del);
        this.del.setBounds(12, 156, 144, 24);

        this.load.setText("Load");
        this.load.setActionCommand("Load");
        getContentPane().add(this.load);
        this.load.setBounds(12, 180, 72, 24);

        this.save.setText("Save");
        this.save.setActionCommand("Save");
        getContentPane().add(this.save);
        this.save.setBounds(84, 180, 72, 24);

        this.train.setText("Begin Training");
        this.train.setActionCommand("Begin Training");
        getContentPane().add(this.train);
        this.train.setBounds(12, 204, 144, 24);

        this.JLabel3.setText("Last Error:");
        getContentPane().add(this.JLabel3);
        this.JLabel3.setBounds(12, 288, 72, 24);

        this.JLabel4.setText("Best Error:");
        getContentPane().add(this.JLabel4);
        this.JLabel4.setBounds(12, 312, 72, 24);

        this.tries.setText("0");
        getContentPane().add(this.tries);
        this.tries.setBounds(96, 264, 72, 24);

        this.lastError.setText("0");
        getContentPane().add(this.lastError);
        this.lastError.setBounds(96, 288, 72, 24);

        this.bestError.setText("0");
        getContentPane().add(this.bestError);
        this.bestError.setBounds(96, 312, 72, 24);

        this.JLabel8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        this.JLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        this.JLabel8.setText("Training Results");
        getContentPane().add(this.JLabel8);
        this.JLabel8.setFont(new Font("Dialog", Font.BOLD, 14));
        this.JLabel8.setBounds(12, 240, 120, 24);

        this.JLabel5.setText("Draw Letters Here");
        getContentPane().add(this.JLabel5);
        this.JLabel5.setBounds(204, 12, 144, 12);
        // }}

        // {{REGISTER_LISTENERS
        final SymAction lSymAction = new SymAction();
        this.downSample.addActionListener(lSymAction);
        this.clear.addActionListener(lSymAction);
        this.image.addActionListener(lSymAction);
        this.fChooser.addActionListener(lSymAction);
        this.add.addActionListener(lSymAction);
        this.del.addActionListener(lSymAction);
        final SymListSelection lSymListSelection = new SymListSelection();
        this.letters.addListSelectionListener(lSymListSelection);
        this.load.addActionListener(lSymAction);
        this.save.addActionListener(lSymAction);
        this.train.addActionListener(lSymAction);
        this.recognize.addActionListener(lSymAction);
        // }}
        this.letters.setModel(this.letterListModel);
        // {{INIT_MENUS
        // }}
    }

    /**
     * Counter that assigns the name to the pattern.
     */
    int  counter;

    /**
     * Called to add the current image to the training set
     *
     * @param event
     *            The event
     */
    @SuppressWarnings("unchecked")
    void add_actionPerformed(final java.awt.event.ActionEvent event) {

        int i;

        final String letter = JOptionPane
                .showInputDialog("Please enter a letter you would like to assign this sample to.");
        if (letter == null) {
            return;
        }

        if (letter.length() > 1) {
            JOptionPane.showMessageDialog(this,
                    "Please enter only a single letter.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.entry.downSample();

        final SampleData sampleData = (SampleData) this.sample.getData().clone();

        sampleData.setLetter(letter.charAt(0));

        for (i = 0; i < this.letterListModel.size(); i++) {

            final Comparable str = (Comparable) this.letterListModel.getElementAt(i);

            if (str.equals(letter)) {

                JOptionPane.showMessageDialog(this,
                        "That letter is already defined, delete it first!",
                        "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            /*if (str.compareTo(sampleData) > 0) {

                this.letterListModel.add(i, sampleData);
                return;
            }*/

        }

        this.letterListModel.add(this.letterListModel.size(), sampleData);
        this.letters.setSelectedIndex(i);
        this.entry.clear();
        this.sample.repaint();

    }

    private void filechooser_actionPerformed(ActionEvent event) {

        JFileChooser fileChooser = new JFileChooser("/home/kevin_cb/RedNeuronalArtificial/Animales");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter imgFilter = new FileNameExtensionFilter("JPG Images", "jpg");
        fileChooser.setFileFilter(imgFilter);

        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.CANCEL_OPTION){

            File imagen = fileChooser.getSelectedFile();

            try {

                BufferedImage myPicture = ImageIO.read(imagen);

                this.entry.entryImage = myPicture;

            }catch (IOException e){
                e.printStackTrace();

            }

        }

    }

    /**
     * Called to clear the image.
     *
     * @param event
     *            The event
     */
    void clear_actionPerformed(final java.awt.event.ActionEvent event) {
        this.entry.clear();
        this.sample.getData().clear();
        this.sample.repaint();

    }

    /**
     * Called when the del button is pressed.
     *
     * @param event
     *            The event.
     */
    void del_actionPerformed(final java.awt.event.ActionEvent event) {
        final int i = this.letters.getSelectedIndex();

        if (i == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a letter to delete.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.letterListModel.remove(i);
    }

    /**
     * Called to downsample the image.
     *
     * @param event
     *            The event
     */
    void downSample_actionPerformed(final java.awt.event.ActionEvent event) {
        this.entry.downSample();

    }

    /**
     * Called when a letter is selected from the list box.
     *
     * @param event
     *            The event
     */
    void letters_valueChanged(final javax.swing.event.ListSelectionEvent event) {

        if (this.letters.getSelectedIndex() == -1) {

            return;
        }

        final SampleData selected = (SampleData) this.letterListModel.getElementAt(this.letters.getSelectedIndex());

        this.sample.setData((SampleData) selected.clone());
        this.sample.repaint();
        this.entry.clear();
    }

    /**
     * Called when the load button is pressed.
     *
     * @param event
     *            The event
     */
    void load_actionPerformed(final java.awt.event.ActionEvent event) {

        try {

            FileReader f;// the actual file stream
            BufferedReader r;// used to read the file line by line

            f = new FileReader(new File("./sample.dat"));
            r = new BufferedReader(f);
            String line;
            int i = 0;

            this.letterListModel.clear();

            while ((line = r.readLine()) != null) {

                final SampleData ds = new SampleData(line.charAt(0),
                        OCR.DOWNSAMPLE_WIDTH, OCR.DOWNSAMPLE_HEIGHT);

                this.letterListModel.add(i++, ds);

                int idx = 2;
                for (int y = 0; y < ds.getHeight(); y++) {

                    for (int x = 0; x < ds.getWidth(); x++) {

                        ds.setData(x, y, line.charAt(idx++) == '1');

                    }

                }

            }

            r.close();
            f.close();

            clear_actionPerformed(null);

            JOptionPane.showMessageDialog(this, "Loaded from 'sample.dat'.",
                    "Training", JOptionPane.PLAIN_MESSAGE);

        } catch (final Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * Used to map neurons to actual letters.
     *
     * @return The current mapping between neurons and letters as an array.
     */
    char[] mapNeurons() {

        final char[] map = new char[this.letterListModel.size()];

        for (int i = 0; i < map.length; i++) {

            map[i] = '?';

        }

        for (int i = 0; i < this.letterListModel.size(); i++) {

            final double[] input = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT]; //DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT

            int idx = 0;

            final SampleData ds = (SampleData) this.letterListModel.getElementAt(i);

            for (int y = 0; y < ds.getHeight(); y++) {

                for (int x = 0; x < ds.getWidth(); x++) {

                    input[idx++] = ds.getData(x, y) ? .5 : -.5;

                }

            }

            final int best = this.net.winner(input);
            map[best] = ds.getLetter();
        }

        return map;
    }

    /**
     * Called when the recognize button is pressed.
     *
     * @param event
     *            The event.
     */
    void recognize_actionPerformed(final java.awt.event.ActionEvent event) {

        if (this.net == null) {

            JOptionPane.showMessageDialog(this, "I need to be trained first!",
                    "Error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        this.entry.downSample();

        final double[] input = new double[DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT]; // DOWNSAMPLE_WIDTH * DOWNSAMPLE_HEIGHT

        int idx = 0;

        final SampleData ds = this.sample.getData();

        for (int y = 0; y < ds.getHeight(); y++) {

            for (int x = 0; x < ds.getWidth(); x++) {

                input[idx++] = ds.getData(x, y) ? .5 : -.5;

            }

        }

        int best = this.net.winner(input);
        char[] map = mapNeurons();

        int index = 0;
        for (int i = 0; i < map.length; i++){

            SampleData prueba = (SampleData) this.letterListModel.get(i);

            if (prueba.getLetter() == map[best]){
                index = i;
            }

        }

        String animal = "";
        if (index >= 0 && index <= 9){

            animal = "Pájaro";

        }else if (index >= 10 && index <= 19){

            animal = "Gato";

        }else if (index >= 20 && index <= 29) {

            animal = "Perro";

        }

        JOptionPane.showMessageDialog(
                this,
                "El animal es un " + animal,
                "Reconocimiento",
                JOptionPane.PLAIN_MESSAGE
        );

        clear_actionPerformed(null);
    }

    /**
     * Called when the image button is pressed.
     *
     * @param event
     *            The event.
     */
    void image_actionPerformed(final java.awt.event.ActionEvent event) {

        try {

            for (int i = 0; i < 30; i++) {

                counter = i;

                String path = "/home/kevin_cb/RedNeuronalArtificial/Animales/image" + (i + 1) + ".jpg";

                System.out.println(path);

                BufferedImage myPicture = ImageIO.read(new File(path));

                this.entry.entryImage = myPicture;

                add_actionPerformed(null);

            }

            /*
            String ruta = "/home/kevin_cb/RedNeuronalArtificial/objetos/mesa/mesa " + (2) + ".jpg";

            System.out.println(ruta);

            BufferedImage myPicture = ImageIO.read(new File(ruta));

            this.entry.entryImage = myPicture;
            */

        } catch (IOException e) {
            e.printStackTrace();
        }

        clear_actionPerformed(null);

    }

    /**
     * Run method for the background training thread.
     */
    public void run() {

        try {

            final int inputNeuron = OCR.DOWNSAMPLE_HEIGHT * OCR.DOWNSAMPLE_WIDTH;

            final int outputNeuron = this.letterListModel.size();

            final double[][] set = new double[this.letterListModel.size()][inputNeuron];

            for (int t = 0; t < this.letterListModel.size(); t++) {

                int idx = 0;

                final SampleData ds = (SampleData) this.letterListModel
                        .getElementAt(t);

                for (int y = 0; y < ds.getHeight(); y++) {

                    for (int x = 0; x < ds.getWidth(); x++) {

                        set[t][idx++] = ds.getData(x, y) ? .5 : -.5;

                    }

                }

            }

            this.net = new SelfOrganizingMap(inputNeuron, outputNeuron,
                    NormalizationType.MULTIPLICATIVE);

            final TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(
                    this.net, set,LearningMethod.SUBTRACTIVE,0.5);

            int tries = 1;

            do {

                train.iteration();

                update(tries++, train.getTotalError(), train.getBestError());

            } while ((train.getTotalError() > MAX_ERROR) && !this.halt);

            this.halt = true;

            update(tries, train.getTotalError(), train.getBestError());

        } catch (final Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * Called when the save button is clicked.
     *
     * @param event
     *            The event
     */
    void save_actionPerformed(final java.awt.event.ActionEvent event) {

        try {

            OutputStream os;// the actual file stream
            PrintStream ps;// used to read the file line by line

            os = new FileOutputStream("./sample.dat", false);
            ps = new PrintStream(os);


            for (int i = 0; i < this.letterListModel.size(); i++) {

                final SampleData ds = (SampleData) this.letterListModel.elementAt(i);

                ps.print(ds.getLetter() + ":");

                for (int y = 0; y < ds.getHeight(); y++) {

                    for (int x = 0; x < ds.getWidth(); x++) {

                        ps.print(ds.getData(x, y) ? "1" : "0");

                    }

                }

                ps.println("");

            }

            ps.close();
            os.close();
            clear_actionPerformed(null);

            JOptionPane.showMessageDialog(this, "Saved to 'sample.dat'.",
                    "Training", JOptionPane.PLAIN_MESSAGE);

        } catch (final Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

    /**
     * Called when the train button is pressed.
     *
     * @param event
     *            The event.
     */
    void train_actionPerformed(final java.awt.event.ActionEvent event) {

        if (this.trainThread == null) {

            this.train.setText("Stop Training");
            this.train.repaint();
            this.trainThread = new Thread(this);
            this.trainThread.start();

        } else {

            this.halt = true;

        }

    }

    /**
     * Called to update the stats, from the neural network.
     *
     * @param retry
     *            How many tries.
     * @param totalError
     *            The current error.
     * @param bestError
     *            The best error.
     */
    public void update(final int retry, final double totalError,
                       final double bestError) {

        if (this.halt) {

            this.trainThread = null;
            this.train.setText("Begin Training");
            JOptionPane.showMessageDialog(this, "Training has completed.",
                    "Training", JOptionPane.PLAIN_MESSAGE);

        }

        final UpdateStats stats = new UpdateStats();
        stats._tries = retry;
        stats._lastError = totalError;
        stats._bestError = bestError;

        try {

            SwingUtilities.invokeAndWait(stats);

        } catch (final Exception e) {

            JOptionPane.showMessageDialog(this, "Error: " + e, "Training",
                    JOptionPane.ERROR_MESSAGE);

        }

    }

}
