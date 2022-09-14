/*
Matt Cerrato
Cell.java
02/24/20
*/

import java.awt.*;

/*
A class that will simulate a cell that can be either dead or alive
*/
public class Cell {
//    Instance field showing if the cell is alive or not
    private boolean notDeadYet;
//    Instance field showing if the cell is a preview or not
    private boolean kindOfAlive;
//    Instance field showing how many generations the cell has been alive
    private int age;

    /*
    Creates a Cell that starts of dead, that isn't a preview, and with a starting age of 0
    */
    public Cell(){
        this.notDeadYet = false;
        this.kindOfAlive = false;
        this.age = 0;
    }
    /*
    Creates a Cell with a boolean parameter dictating whether the cell is alive ord ead, that isn't a preview,
     and with a starting age of 0
    */
    public Cell(boolean alive){
        this.notDeadYet = alive;
        this.kindOfAlive = false;
        this.age = 0;
    }

    /*
    Getter for the notDeadYet instance field
    */
    public boolean getAlive(){
        return this.notDeadYet;
    }

    /*
    Setter for the notDeadYet instance field
     */
    public void setAlive(boolean alive){
        this.notDeadYet = alive;
    }

    /*
    Getter for the kindOfAlive instance field
     */
    public boolean getKindOfAlive(){
        return this.kindOfAlive;
    }

    /*
    Setter for the kindOfAlive instance field
     */
    public void setKindOfAlive(boolean kindOfAlive){
        this.kindOfAlive = kindOfAlive;
    }

    /*
    Function that will set the cell to be dead and reset's it's age to be 0
     */
    public void reset(){
        notDeadYet = false;
        this.age = 0;

    }

    /*
    Getter for the age instance field
     */
    public int getAge(){
        return this.age;
    }

    /*
    Setter for the age instance field
     */
    public void setAge(int age){
        this.age= age;
    }


    /*
    Function that will draw the cell onto the graphics parameter g, takes in x and y coordinates as well as a scale
     */
    public void draw(Graphics g, int x, int y, int scale ){
//        Initially draws the outline of each cell
        g.setColor(Color.BLACK);
        g.drawRect(x,y,scale,scale);

//        Alive cells have a color dependent on the age they have been alive, dead cells are white
        if (this.notDeadYet){
            g.setColor(new Color(Math.min(age,105),255 - Math.min(2*age,255),0));
        } else{

            g.setColor(new Color(255,255,255));
        }
//        Preview cells are set to a blue with an alpha value of 100
        if (this.kindOfAlive){
            g.setColor(new Color(0,0,255, 100));
        }
//        Will fill the rect with whichever color it should have depending on it's instance fields
        g.fillRect(x,y,scale,scale);

    }

    /*
    Function takes in an int of alive neighbors and uses it to decide if this cell should be alive or dead in the next
    generation
     */
    public void updateState(int aliveNeighbors){
        if (this.notDeadYet) {
            this.notDeadYet = (aliveNeighbors==2 || aliveNeighbors==3);
        }else{
            this.notDeadYet = (aliveNeighbors==3);
        }

    }

    /*
    Overrides the toString function of Object to print a 1 if alive and a 0 if dead
     */
    @Override
    public String toString(){
        if(notDeadYet){
            return "1";
        }
        return "0";
    }


}
