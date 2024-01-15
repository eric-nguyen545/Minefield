import java.util.Random;

public class Minefield {
    /**
    Global Section
    */
    public static final String ANSI_YELLOW_BRIGHT = "\u001B[33;1m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE_BRIGHT = "\u001b[34;1m";
    public static final String ANSI_BLUE = "\u001b[34m";
    public static final String ANSI_RED_BRIGHT = "\u001b[31;1m";
    public static final String ANSI_RED = "\u001b[31m";
    public static final String ANSI_GREEN = "\u001b[32m";
    public static final String ANSI_PURPLE = "\u001b[35m";
    public static final String ANSI_CYAN = "\u001b[36m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001b[47m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001b[45m";
    public static final String ANSI_GREY_BACKGROUND = "\u001b[0m";

    /* 
     * Class Variable Section
     * 
    */

    /*Things to Note:
     * Please review ALL files given before attempting to write these functions.
     * Understand the Cell.java class to know what object our array contains and what methods you can utilize
     * Understand the StackGen.java class to know what type of stack you will be working with and methods you can utilize
     * Understand the QGen.java class to know what type of queue you will be working with and methods you can utilize
     */
    
    /**
     * Minefield
     * 
     * Build a 2-d Cell array representing your minefield.
     * Constructor
     * @param rows       Number of rows.
     * @param columns    Number of columns.
     * @param flags      Number of flags, should be equal to mines
     */
    private int numRows;
    private int numCols;
    private int numFlags;
    private Cell[][] game;
    public Minefield(int rows, int columns, int flags) {
        numRows = rows;
        numCols= columns;
        numFlags = flags;
        game = new Cell[numRows][numCols];
        for (int x = 0; x < numRows; x++){
            for (int y = 0; y < numCols; y ++){
                game[x][y] = new Cell(false, "0");
            }
        }
    }

    /**
     * evaluateField
     * 
     *
     * @function:
     * Evaluate entire array.
     * When a mine is found check the surrounding adjacent tiles. If another mine is found during this check, increment adjacent cells status by 1.
     * 
     */
    public void evaluateField() {
        for (int x = 0; x < numRows; x++) {
            for (int y = 0; y < numCols; y++) {
                evaluateArea(x, y);
            }
        }
    }
    public void evaluateArea(int x, int y) { //helper method for evaluateField
        if (!game[x][y].getStatus().equals("M")) {
            int mineCount = 0;
            //Go through the surrounding cells, and increment the number of mines accordingly
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i >= 0 && i < numRows && j >= 0 && j < numCols && game[i][j].getStatus().equals("M")) {
                        mineCount++;
                    }
                }
            }
            //change the cell number if mineCount is more than 0
            if (mineCount > 0) {
                game[x][y].setStatus(Integer.toString(mineCount));
            }
        }
    }
    /**
     * createMines
     * 
     * Randomly generate coordinates for possible mine locations.
     * If the coordinate has not already been generated and is not equal to the starting cell set the cell to be a mine.
     * utilize rand.nextInt()
     * 
     * @param x       Start x, avoid placing on this square.
     * @param y        Start y, avoid placing on this square.
     * @param mines      Number of mines to place.
     */
    public void createMines(int x, int y, int mines) {
        //keep track of how many mines have been created
        int createdMines = 0;
        Random rand = new Random();
        //loop until enough mines have been created
        while (createdMines != mines) {
            //create random cords
            int randIntX = rand.nextInt(numRows);
            int randIntY = rand.nextInt(numCols);
            //check if the cords are already a mine or starting location
            //if not then create a mine
            if ((!game[randIntX][randIntY].getStatus().equals("M")) && (randIntX != x && randIntY != y)){
                game[randIntX][randIntY].setStatus("M");
                createdMines++;
            }
        }
    }

    /**
     * guess
     * 
     * Check if the guessed cell is inbounds (if not done in the Main class). 
     * Either place a flag on the designated cell if the flag boolean is true or clear it.
     * If the cell has a 0 call the revealZeroes() method or if the cell has a mine end the game.
     * At the end reveal the cell to the user.
     * 
     * 
     * @param x       The x value the user entered.
     * @param y       The y value the user entered.
     * @param flag    A boolean value that allows the user to place a flag on the corresponding square.
     * @return boolean Return false if guess did not hit mine or if flag was placed, true if mine found.
     */
    public boolean guess(int x, int y, boolean flag) {
        //check if guess is in bounds and if they want to place a flag
        if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols) && flag){
            game[x][y].setStatus("F");
            game[x][y].setRevealed(true);
            return false;
        }
        //check if guess is in bounds and if they do not want to place a flag
        if ((x >= 0 && x < numRows) && (y >= 0 && y < numCols) && !flag){
            //If they don't want to place a flag and hit a mine, returns true thus ending game
            if (game[x][y].getStatus().equals("M")) {
                game[x][y].setRevealed(true);
                return true;
            }
            //Check if the guess is a spot is an integer
            if (game[x][y].getStatus().equals("0") || game[x][y].getStatus().equals("1") || game[x][y].getStatus().equals("2")
            || game[x][y].getStatus().equals("3") || game[x][y].getStatus().equals("4") || game[x][y].getStatus().equals("5")
            || game[x][y].getStatus().equals("6") || game[x][y].getStatus().equals("7") || game[x][y].getStatus().equals("8")
            || game[x][y].getStatus().equals("9")){
                revealZeroes(x,y);
                game[x][y].setRevealed(true);
                return false;
            }
        }
        return true;
    }

    /**
     * gameOver
     * 
     * Ways a game of Minesweeper ends:
     * 1. player guesses a cell with a mine: game over -> player loses
     * 2. player has revealed the last cell without revealing any mines -> player wins
     * 
     * @return boolean Return false if game is not over and squares have yet to be revealed, otheriwse return true.
     */
    public boolean gameOver() {
        //traverse the whole board to check if all non mine cells have been revealed yet
        for (int x = 0; x < numRows; x++){
            for (int y = 0; y <numCols; y++){
                if (!game[x][y].getStatus().equals("M") && !game[x][y].getRevealed()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reveal the cells that contain zeroes that surround the inputted cell.
     * Continue revealing 0-cells in every direction until no more 0-cells are found in any direction.
     * Utilize a STACK to accomplish this.
     *
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a stack be useful here rather than a queue?
     *
     * @param x      The x value the user entered.
     * @param y      The y value the user entered.
     */
    public void revealZeroes(int x, int y) {
        Stack1Gen<int[]> stackZeroes = new Stack1Gen<>();
        stackZeroes.push(new int[]{x, y});
        while (!stackZeroes.isEmpty()){
            int[] currCell = stackZeroes.pop();
            int currCellX = currCell[0];
            int currCellY = currCell[1];
            //continue to the next item in the stack if current one is out of bounds
            if (currCellX < 0 || currCellX >= numRows || currCellY < 0 || currCellY >= numCols) {
                continue;
            }
            //continue to the next item in the stack if the current one is already revealed or not a 0
            if (game[currCellX][currCellY].getRevealed() || !game[currCellX][currCellY].getStatus().equals("0")) {
                continue;
            }
            game[currCellX][currCellY].setRevealed(true);
            stackZeroes.push(new int[]{currCellX + 1, currCellY});
            stackZeroes.push(new int[]{currCellX - 1, currCellY});
            stackZeroes.push(new int[]{currCellX, currCellY + 1});
            stackZeroes.push(new int[]{currCellX, currCellY - 1});
        }
    }

    /**
     * revealStartingArea
     *
     * On the starting move only reveal the neighboring cells of the initial cell and continue revealing the surrounding concealed cells until a mine is found.
     * Utilize a QUEUE to accomplish this.
     * 
     * This method should follow the psuedocode given in the lab writeup.
     * Why might a queue be useful for this function?
     *
     * @param x     The x value the user entered.
     * @param y     The y value the user entered.
     */
    public void revealStartingArea(int x, int y) {
        Q1Gen<int[]> QReveal = new Q1Gen<>();
        QReveal.add(new int[]{x, y});
        while (QReveal.length() != 0) {
            int[] currCell = QReveal.remove();
            int currCellX = currCell[0];
            int currCellY = currCell[1];
            game[currCellX][currCellY].setRevealed(true);
            //if the cell is a mine, stop revealing cells
            if (game[currCellX][currCellY].getStatus().equals("M")) {
                break;
            }
            //check if in cell is in bounds, then add the up, down, left, or right to the queue and reveal the current cell
            if (currCellX + 1 < numRows && (currCellY < numCols) && !game[currCellX + 1][currCellY].getRevealed()) {
                QReveal.add(new int[]{currCellX + 1, currCellY});
                game[currCellX][currCellY].setRevealed(true);
            }
            if (currCellX - 1 > 0 && (currCellY < numCols) && !game[currCellX - 1][currCellY].getRevealed()) {
                QReveal.add(new int[]{currCellX - 1, currCellY});
                game[currCellX][currCellY].setRevealed(true);
            }
            if ((currCellX < numRows && (currCellY + 1 < numCols)) && !game[currCellX][currCellY + 1].getRevealed()) {
                QReveal.add(new int[]{currCellX, currCellY + 1});
                game[currCellX][currCellY].setRevealed(true);
            }
            if ((currCellX < numRows && (currCellY - 1 >= 0)) && !game[currCellX][currCellY - 1].getRevealed()) {
                QReveal.add(new int[]{currCellX, currCellY - 1});
                game[currCellX][currCellY].setRevealed(true);
            }
        }
    }

    /**
     * For both printing methods utilize the ANSI colour codes provided! 
     * 
     * 
     * 
     * 
     * 
     * debug
     *
     * @function This method should print the entire minefield, regardless if the user has guessed a square.
     * *This method should print out when debug mode has been selected. 
     */
    public void debug() {
        StringBuilder out = new StringBuilder();
        out.append(" ");
        //print the top indexes
        for (int i = 0; i < game.length; i++) {
            out.append(" ");
            out.append(i);
            out.append(" ");
        }
        out.append('\n');
        for (int i = 0; i < game.length; i++) {
            //print side indexes
            out.append(i);
            for (int j = 0; j < game[0].length; j++) {
                out.append(" ");
                if (game[i][j].getStatus().equals("F")) { //print out the right color depending on the number, flag, or mine
                    out.append(ANSI_PURPLE + "F" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("M")) {
                    out.append(ANSI_RED + "M" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("0")) {
                    out.append(ANSI_YELLOW + "0" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("1")) {
                    out.append(ANSI_BLUE + "1" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("2")) {
                    out.append(ANSI_GREEN + "2" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("3")) {
                    out.append(ANSI_RED + "3" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("4")) {
                    out.append(ANSI_RED + "4" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("5")) {
                    out.append(ANSI_RED + "5" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("6")) {
                    out.append(ANSI_RED + "6" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("7")) {
                    out.append(ANSI_RED + "7" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("8")) {
                    out.append(ANSI_RED + "8" + ANSI_GREY_BACKGROUND);
                }
                if (game[i][j].getStatus().equals("9")) {
                    out.append(ANSI_RED + "9" + ANSI_GREY_BACKGROUND);
                }
                out.append(" ");
            }
            out.append("\n");
        }
        System.out.println(out);
    }

    /**
     * toString
     *
     * @return String The string that is returned only has the squares that has been revealed to the user or that the user has guessed.
     */
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(" ");
        //print the top indexes
        for (int i = 0; i < game.length; i++) {
            out.append(" ");
            out.append(i);
            out.append(" ");
        }
        out.append('\n');
        for (int i = 0; i < game.length; i++) {
            //print side indexes
            out.append(i);
            for (int j = 0; j < game[0].length; j++) {  //print out the right color depending on the number, flag, or mine
                if (game[i][j].getRevealed()){ //check if the cell has been revealed yet
                    out.append(" ");
                    if (game[i][j].getStatus().equals("F")) {
                        out.append(ANSI_PURPLE + "F" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("M")) {
                        out.append(ANSI_RED + "M" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("0")) {
                        out.append(ANSI_YELLOW + "0" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("1")) {
                        out.append(ANSI_BLUE + "1" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("2")) {
                        out.append(ANSI_GREEN + "2" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("3")) {
                        out.append(ANSI_RED + "3" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("4")) {
                        out.append(ANSI_RED + "4" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("5")) {
                        out.append(ANSI_RED + "5" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("6")) {
                        out.append(ANSI_RED + "6" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("7")) {
                        out.append(ANSI_RED + "7" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("8")) {
                        out.append(ANSI_RED + "8" + ANSI_GREY_BACKGROUND);
                    }
                    if (game[i][j].getStatus().equals("9")) {
                        out.append(ANSI_RED + "9" + ANSI_GREY_BACKGROUND);
                    }
                    out.append(" ");
                } else{
                    out.append(" - ");
                }
            }
            out.append("\n");
        }
        return out.toString();
    }
}
