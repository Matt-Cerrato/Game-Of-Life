/*
  Originally written by Bruce A. Maxwell a long time ago.
  Updated by Brian Eastwood and Stephanie Taylor more recently
  Updated by Bruce again in Fall 2018
  Updated by Matt Cerrato 02/24/20

  Creates a window using the JFrame class.

  Creates a drawable area in the window using the JPanel class.

  The JPanel calls the Landscape's draw method to fill in content, so the
  Landscape class needs a draw method.
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Displays a Landscape graphically using Swing.  The Landscape
 * contains a grid which can be displayed at any scale factor.
 * @author bseastwo
 */
public class LandscapeDisplay
{
    JFrame win;

    protected Landscape scape;
    private LandscapePanel canvas;
    private int gridScale; // width (and height) of each square in the grid
    private boolean paused;

    /**
     * Initializes a display window for a Landscape.
     * @param scape the Landscape to display
     * @param scale controls the relative size of the display
     */
    public LandscapeDisplay(Landscape scape, int scale)
    {
        // setup the window

        this.paused = true;
        this.win = new JFrame("Game of Life");
        this.win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.scape = scape;
        this.gridScale = scale;

        // create a panel in which to display the Landscape
        // put a buffer of two rows around the display grid
        this.canvas = new LandscapePanel( (int) (this.scape.getCols()+4) * this.gridScale,
                (int) (this.scape.getRows()+4) * this.gridScale);

        // add the panel to the window, layout, and display

//        Creates the buttons and their actionListeners and actions then adds the buttons to the window
        JButton startButton = new JButton("Start");
        startButton.setFocusable(false);
        startButton.setBounds(400, 805, 80, 20);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
                scape.setPaused(paused);
                if (paused){
                    startButton.setText("Start");
                }else{
                    startButton.setText("Pause");
                }


            }
        });
        this.win.add(startButton);
        JButton resetButton = new JButton("Reset");
        resetButton.setFocusable(false);
        resetButton.setBounds(500, 805, 80,20);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scape.reset();
                repaint();
            }
        });
        this.win.add(resetButton);
        JButton backButton = new JButton("Back");
        backButton.setFocusable(false);
        backButton.setBounds(600, 805, 80, 20);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scape.decrGeneration();
                repaint();
            }
        });
        this.win.add(backButton);
        JButton forwardButton = new JButton("Forward");
        forwardButton.setFocusable(false);
        forwardButton.setBounds(700, 805, 80, 20);
        forwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             scape.incrGeneration();
             repaint();
            }
        });
        this.win.add(forwardButton);
        JComboBox<ComboItem> comboBox = new JComboBox<>();
        comboBox.addItem(new ComboItem("None", null));
        File oscillator62 = new File("62oscillator.txt");
        comboBox.addItem(new ComboItem("Period 62 Oscillator", oscillator62.getAbsolutePath()));
        File GosperGlider = new File("gosperGlider.txt");
        comboBox.addItem(new ComboItem("Gosper Glider",GosperGlider.getAbsolutePath()));
        File oscillator135 = new File("Period-135-Oscillator.txt");
        comboBox.addItem(new ComboItem("Period 135 Oscillator",oscillator135.getAbsolutePath()));
        File thunk = new File("thunk.txt");
        comboBox.addItem(new ComboItem("Thunk", thunk.getAbsolutePath()));
        File copperHead = new File("Copperhead.txt");
        comboBox.addItem(new ComboItem("Copperhead", copperHead.getAbsolutePath()));
        File diamond = new File("diamond.txt");
        comboBox.addItem(new ComboItem("Diamond", diamond.getAbsolutePath()));
        File human = new File("Human.txt");
        comboBox.addItem(new ComboItem("Spaceship", human.getAbsolutePath()));
        File blinkership = new File("blinkership.txt");
        comboBox.addItem(new ComboItem("Blinker Ship", blinkership.getAbsolutePath()));
        comboBox.setFocusable(false);
        comboBox.setBounds(159, 803, 189, 30);
        comboBox.setMaximumRowCount(5);

        this.win.add(comboBox, BorderLayout.CENTER);
        this.win.add(this.canvas, BorderLayout.CENTER);

        this.win.pack();
        this.win.setVisible(true);
//        Mouse motion listener for previewing patterns
        this.win.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                try {
//                    Using the combobox will try to make the preview using the mouse's position as the x and y parameters
                    ComboItem string = (ComboItem)comboBox.getSelectedItem();
                    String pattern = string.getValue();
                    if (pattern != null){
                        scape.parse_rle_file(pattern, (e.getY()-3*gridScale)/gridScale, e.getX()/gridScale, true);
                        repaint();
                    }

                } catch (IOException ex) {

                }

            }
        });
//        Mouse listener for placing patterns
        this.win.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                try {
//                    Will try and find the value of the combobox and if it isn't null will draw the pattern
//                    Using the mouse position as the x and y parameters
                    ComboItem string = (ComboItem)comboBox.getSelectedItem();
                    String pattern = string.getValue();
                    if (pattern != null){
                        scape.parse_rle_file(pattern, (e.getY()-3*gridScale)/gridScale, e.getX()/gridScale, false);
                        repaint();
                    } else{
//                        If the combobox's value is null than will make the cell where user clicks alive

                        if(e.getY()-3*gridScale <= scape.getRows() * gridScale && e.getX() <= scape.getCols()*gridScale) {

                            Cell cell = scape.getCell((e.getY() - 3 * gridScale) / gridScale, e.getX() / gridScale);
                            cell.setAlive(!cell.getAlive());
                            repaint();
                        }
                    }
//                    Resets the combobox to none and resets all preview cells when there is click
                    comboBox.setSelectedIndex(0);
                    scape.resetShadow();

                } catch (IOException ex) {

                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
//        Key listener for different functions
        this.win.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

//                Can advance simulation with right arrow key
                if (e.getKeyCode() == KeyEvent.VK_RIGHT){


                    scape.incrGeneration();
                    repaint();

//                    Can rewind simulation with left arrow key
                } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                    scape.decrGeneration();
                    repaint();

//                    Will pause simulation when 'p' is pressed and changes text on start/pause button
                }else if (e.getKeyCode() == KeyEvent.VK_P){
                    paused = !paused;
                    scape.setPaused(paused);
                    if (paused){
                        startButton.setText("Start");
                    }else{
                        startButton.setText("Pause");
                    }

//                    Will reset whole grid if 'r' is pressed
                }else if (e.getKeyCode() == KeyEvent.VK_R){
                    scape.reset();
                    repaint();
                }

            }
            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    /*
    Class that simulates a comboItem inside a comboBox
    */
    private class ComboItem{
//        Instance fields for what combBox displays and the value of item
        private String key, value;

//        Makes the comboItem with a given key and value
        public ComboItem(String key, String value){
            this.key = key;
            this.value = value;
        }
//        toString called by comboBox returns the key
        public String toString(){
            return this.key;
        }

//        Getter for key instance field
        public String getKey(){
            return this.key;
        }

//        Getter for value instance field used by comboBox
        public String getValue(){
            return this.value;
        }
    }
    /**
     * Saves an image of the display contents to a file.  The supplied
     * filename should have an extension supported by javax.imageio, e.g.
     * "png" or "jpg".
     *
     * @param filename  the name of the file to save
     */
    public void saveImage(String filename)
    {
        // get the file extension from the filename
        String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());

        // create an image buffer to save this component
        Component tosave = this.win.getRootPane();
        BufferedImage image = new BufferedImage(tosave.getWidth(), tosave.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // paint the component to the image buffer
        Graphics g = image.createGraphics();
        tosave.paint(g);
        g.dispose();

        // save the image
        try
        {
            ImageIO.write(image, ext, new File(filename));
        }
        catch (IOException ioe)
        {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * This inner class provides the panel on which Landscape elements
     * are drawn.
     */
    private class LandscapePanel extends JPanel
    {
        /**
         * Creates the panel.
         * @param width     the width of the panel in pixels
         * @param height        the height of the panel in pixels
         */
        public LandscapePanel(int width, int height)
        {
            super();
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(Color.lightGray);
        }

        /**
         * Method overridden from JComponent that is responsible for
         * drawing components on the screen.  The supplied Graphics
         * object is used to draw.
         *
         * @param g     the Graphics object used for drawing
         */
        public void paintComponent(Graphics g)
        {
            // take care of housekeeping by calling parent paintComponent
            super.paintComponent(g);

            // call the Landscape draw method here
            scape.draw( g, gridScale );

        } // end paintComponent

    } // end LandscapePanel

    public void repaint() {
        this.win.repaint();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Landscape scape = new Landscape(100,100);
        Random gen = new Random();
        double density = .3;

//         initialize the grid to be 30% full
//        for (int i = 0; i < scape.getRows(); i++) {
//            for (int j = 0; j < scape.getCols(); j++ ) {
//                scape.getCell( i, j ).setAlive( gen.nextDouble() <= density );
//            }
//        }
        scape.parse_rle_file("/Users/mattcerrato/IdeaProjects/GameOfLife/src/Period-135-Oscillator.txt", 25,25, false);

        LandscapeDisplay display = new LandscapeDisplay(scape, 8);
        display.repaint();


    }
}