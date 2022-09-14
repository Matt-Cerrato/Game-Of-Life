/*
Matt Cerrato
Landscape.java
02/24/20
*/

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/*
Class that simulates a grid of cells
 */
public class Landscape {
//    Instance field simulating the grid of cells with a 2D array
    private Cell[][] landscape;
//    Instance field keeping track of the past grids of cells
    private ArrayList<Cell[][]> past;
//    Instance field showing what generation the simulation is on
    private int generation;
//    Instance field showing if the simulation is paused or not
    private boolean paused;

    /*
    Makes the Landscape object with a specified number of rows and cols
     */
    public Landscape(int rows, int cols){
//        Initially the simulation is paused
        this.paused = true;
//        The 2D array is initialized with the correct number of rows and cols
        this.landscape = new Cell[rows][cols];
//        The arrayList of past landscapes is initally empty
        this.past = new ArrayList<>();
//        Generation starts at 0
        this.generation = 0;
//        Initializes each cell in landscape to be dead
        for (int i = 0; i < rows ; i++) {
            for (int j = 0; j <cols ; j++) {
                this.landscape[i][j] = new Cell(false);
//                this.past.get(generation)[i][j] = new Cell(deadOrNot);
            }
        }
//        Adds this first landscape to the past ArrayList
        this.past.add(landscape);
    }

    /*
    Resets each cell in the current generation's landscape
     */
    public void reset(){
        for(Cell[] col : this.past.get(generation)){
            for(Cell cell: col){
                cell.reset();
            }
        }
    }

    /*
    Getter for the number of rows in the landscape
     */
    public int getRows(){
        return this.landscape.length;
    }

    /*
    Getter for the number of cols in the landscape
     */
    public int getCols(){
        return this.landscape[0].length;
    }

    /*
    Getter for the generation of the simulation
     */
    public int getGeneration(){
        return this.generation;
    }

    /*
    Function that will advance the current generation by one and if the generation is the latest generation also advance
    the cells
     */
    public void incrGeneration(){
        if (this.generation >= this.past.size()-1){
            advance();
        }
        this.generation ++;
    }

    /*
    Function that will remove the current generation from the past ArrayLists and set the generation to be one less
    Only if the generation is not the first generation
     */
    public void decrGeneration(){
        if (this.generation >0){
            this.past.remove(this.generation);
            this.generation -=1;

        }

    }

    /*
    Getter for a cell in the grid that takes the parameters row and col
     */
    public Cell getCell(int row, int col){
        return this.past.get(generation)[row][col];
    }

    /*
    Function that will return the number of alive neigbors of a cell given by the parameters row and col
     */
    public int getAliveNeighbors(int row, int col){
//        Local variable simulating number of alive neighbors
        int aliveNeighbors = 0;

//        Loops through all of the neighbors of the cell, accounting for cells on the border of the simulation
//        By checking if the indexs around the cell are viable and if not then setting them to be correct start and end
//        indexes
        for (int i = Math.max(0, row-1); i <= Math.min(row+1, this.landscape.length-1); i++) {
            for (int j =Math.max(0,col-1); j <= Math.min(col+1, this.landscape[0].length-1) ; j++) {
//                Will not count the cell that is being checked
                if(!(i == row && j== col)) {

//                    Only adds to alive neighbors if the neighbors are alive
                    if (this.past.get(generation)[i][j].getAlive()) {
                        aliveNeighbors++;
                    }
                }

            }
        }

        return aliveNeighbors;
    }

    /*
    Setter function that takes in a boolean and sets the paused instance field to that parameter
     */
    public void setPaused(boolean pause){
        this.paused = pause;
    }

    /*
    Getter function for the paused instance field
     */
    public boolean getPaused(){
        return this.paused;
    }

    /*
    Draw function that takes in a graphics object and a gridscale parameter, and will draw each cell on to the graphics
    object with the correct scale
     */
    public void draw(Graphics g, int gridScale){

        for (int i = 0; i < this.landscape.length; i++) {
            for (int j = 0; j < this.landscape[0].length; j++) {
                this.past.get(generation)[i][j].draw(g, j * gridScale, i * gridScale, gridScale);
            }
        }
    }

    /*
    Function that will simulate the cells advancing by one generation
     */
    public void advance(){
//        A temporary grid that will contain the correct cells for the next generation using information from the
//        Current genration
        Cell[][] newgrid = new Cell[landscape.length][landscape[0].length];
        for (int i = 0; i < landscape.length ; i++) {
            for (int j = 0; j <landscape[0].length ; j++) {
//                Initiazes the cells in the new grid with cells that are the same as the cells in the current grid
                Cell cell = this.past.get(generation)[i][j];
                newgrid[i][j] = new Cell(cell.getAlive());
                if (cell.getAlive()){
//                    Increases the age of alive cells by 1
                    newgrid[i][j].setAge(cell.getAge()+1);
                }
                newgrid[i][j].setKindOfAlive(cell.getKindOfAlive());
            }
        }
        for (int i = 0; i<landscape.length; i++) {
            for(int j = 0; j<landscape[0].length; j++){
//                Updates the state of the new grid's cells using the neighbors from the current grid
                newgrid[i][j].updateState(getAliveNeighbors(i,j));

            }
        }
//        Sets the current grid to be the new grid and adds the current grid to the past ArrayList
        landscape = newgrid;
        this.past.add(landscape);
    }

    /*
    Function that will set all the cells in the current generation to not be previewing
     */
    public void resetShadow(){
        for (Cell[] cellrow:this.past.get(generation)){
            for (Cell cell:cellrow){
                cell.setKindOfAlive(false);
            }

        }
    }

    /*
    Function that will take a file parameter, x and y parameters, and a boolean showing if the
    pattern will be a preview or be created
     */
    public void parse_rle_file(String fileName, int x, int y, boolean shadow) throws IOException {
//        Initally resets the preview cells and starts to read the file
        resetShadow();
        FileInputStream fstream = new FileInputStream(fileName);
        // Get the object of DataInputStream
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine = br.readLine();
        int rows = 0;
        int cols = 0;

//        Read File Line By Line reading through all of the comment lines
        while (strLine.contains("#")) {
            strLine = br.readLine();
        }

//        Array containing the strings with information on size of the pattern
        String[] box = strLine.split(",");
//        Loop through the strings and sets the rows and cols fields to the correct number for this pattern
        for (String bound : box) {
            if (bound.contains("x = ")) {
                cols = Integer.parseInt(bound.substring(bound.indexOf("x = ")+4));
            } else if (bound.contains("y = ")) {
                rows = Integer.parseInt(bound.substring(bound.indexOf("y = ")+4));
            }
        }
//        Will attempt to index into values from starting position to end of pattern and set the cells in that area
//        to false if not previewing pattern
        try {
            for (int i = x; i < rows + x; i++) {
                for (int j = y; j < cols + y; j++) {
                    if (!shadow) {
                        this.past.get(generation)[i][j].setAlive(false);
                    }

                }
            }
        }catch(Exception e){

        }


//
        int rowindex = 0;
        String readThis = strLine;
        strLine = "";

//        Loops through the lines with the information on pattern concatenating the lines into on string
        while ((readThis = br.readLine()) != null) {
            strLine += readThis;
        }
//        Array of strings that show what each row in the pattern will have
        String[] row = strLine.split("\\$");

//        Loops through the strings in the row array
        for(String cells: row){
//            Accounts for splitting
            cells += "$";
//            List of cells in the row
            ArrayList<Boolean> cellList = new ArrayList<>();

//            Variable to show whether the next cells will be alive or dead
            boolean alive = false;

//            Number of cells to  be alive or dead starts at 0;
            String numCells = "0";
            int rowsToSkip = 1;

//            Loops through the row string
            for (int i = 0; i < cells.length() ; i++) {

//                Parses depending on what character is found and what int's are found

//                If the character is a b then will add the correct number of dead cells to the cellList
                if (cells.substring(i,i+1).equals("b")){
                    alive = false;
                    for (int j = 0; j < Integer.parseInt(numCells); j++) {
                        cellList.add(alive);
                    }
//                    if the numCells wasn't changed than add just one dead cell
                    if (numCells.equals("0")){
                        cellList.add(false);
                    }
//                    resets then number of cells to be added to initial value
                    numCells = "0";

//                    If the character is an o then will add the correct number of alive cells to the cellList
                } else if(cells.substring(i,i+1).equals("o")){
                    alive = true;
                    for (int j = 0; j < Integer.parseInt(numCells); j++) {
                        cellList.add(alive);
                    }

//                    if the numCells wasn't changed than add just one alive cell
                    if (numCells.equals("0")){
                        cellList.add(true);
                    }
//                    resets then number of cells to be added to initial value
                    numCells = "0";

//                    If the character was a $ than simulates the end of the row by adding to the variable showing
//                    how many empty rows pattern has the correct number of empty rows to skip
                } else if(cells.substring(i,i+1).equals("$")){
                    try{
//                        If the numbers to skip is greater than the initial value to the variable than set
//                        the rowsToSkip variable to the new value.
                        if(Integer.parseInt(numCells)>0){
                            rowsToSkip = Integer.parseInt(numCells);
                        }
                    }catch (Exception e){
                    }
//                    Resets the numCells variable to it's initial value
                    numCells = "0";

//                    If the character isn't any of the other characters than assume it's an int
                } else if(!(cells.substring(i,i+1).equals("o") || cells.substring(i,i+1).equals("b") || cells.substring(i,i+1).equals("$"))){
//                    Concatenates the int character to the numCells value, concatenation accounts for values with
//                    Multiple integers in them such as values in ten's or hundreds.
                    numCells += cells.substring(i,i+1);
                }
            }

//            Loops through the size of one row in the pattern
            for (int i = 0; i < cols ; i++) {
//                Will set the cell in the current generation to be a preview cell or an alive cell depending on parameter
                try{
                    if (shadow){
                        this.past.get(generation)[rowindex+x][i+y].setKindOfAlive(cellList.get(i));
                    }else{
                        this.past.get(generation)[rowindex+x][i+y].setAlive(cellList.get(i));
                    }
                }
//                If the cellList doesn't contain the index it will then try to set the remaining cells to be
//                not preview cells or not alive cells
                catch(Exception e){
                    try{
                        if (shadow){
                            this.past.get(generation)[rowindex+x][i+y].setKindOfAlive(false);
                        }else{
                            this.past.get(generation)[rowindex+x][i+y].setAlive(false);
                        }
                    }catch(Exception ef){
                    }
                }
            }
//            At the end of the row will change the next row depending on the rows to be skipped that was parsed
            rowindex+=rowsToSkip;
        }
    }


    /*
    Function that overrides the inhereted toString function to print the cells with a line between rows
     */
    @Override
    public String toString(){
        String grid = "";
        for (Cell[] row : this.past.get(generation)) {
            for(Cell cell:row){
                grid += cell;
            }
            grid += "\n";
        }
        return grid;
    }

    /*
    Main function checking if the class runs
     */
    public static void main(String[] args) throws IOException {
        Landscape test = new Landscape(100,100);
        test.parse_rle_file("/Users/mattcerrato/IdeaProjects/GameOfLife/src/Period-135-Oscillator.txt",0,0, false);
        System.out.println(test);


    }
}
