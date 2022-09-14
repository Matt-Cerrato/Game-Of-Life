/*
Matt Cerrato
LifeSimulation.java
02/24/20
*/

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Random;

/*
Class that simulates Conway's Game of Life
 */
public class LifeSimulation {
//    Instance field simulating the graphics display
    private LandscapeDisplay display;
//    Instance field simulating the grid
    private Landscape scape;


    /*
     Creates the simulation object
    */
    public LifeSimulation(){

//        Initializes the grid to have 100 rows and 100 cols
        scape = new Landscape(100,100);

//        Initializes the display to display the grid with a scale of 8
        display = new LandscapeDisplay(scape,8);
    }

    /*
    Function that calls the display's repaint function
     */
    public void repaint(){
        display.repaint();
    }
    public static void main(String[] args) throws InterruptedException {

//        Initalizes the simulation object
        LifeSimulation life = new LifeSimulation();

//        Runs forever
        while(true){
//            If the simulation isn't paused than advance the generation every 25 milliseconds
            if(!life.scape.getPaused()){
                Thread.sleep(25);
                life.scape.incrGeneration();
            }
//            Continuasly repaints the display
            life.repaint();

        }
    }
}
