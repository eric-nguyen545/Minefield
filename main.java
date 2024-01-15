//Import Section
import java.util.Random;
import java.util.Scanner;

/*
 * Provided in this class is the necessary code to get started with your game's implementation
 * You will find a while loop that should take your minefield's gameOver() method as its conditional
 * Then you will prompt the user with input and manipulate the data as before in project 2
 * 
 * Things to Note:
 * 1. Think back to project 1 when we asked our user to give a shape. In this project we will be asking the user to provide a mode. Then create a minefield accordingly
 * 2. You must implement a way to check if we are playing in debug mode or not.
 * 3. When working inside your while loop think about what happens each turn. We get input, user our methods, check their return values. repeat.
 * 4. Once while loop is complete figure out how to determine if the user won or lost. Print appropriate statement.
 */

public class main {
    public static void main(String[] args) {
        //initialize to create a minefield, and keep track of size and number of flags
        Minefield minefield;
        int numFlags;
        int size;
        //take in the user difficulty
        Scanner userInput = new Scanner(System.in);
        System.out.println("Please select a difficulty [Easy] [Medium] [Hard]");
        String difficulty = userInput.nextLine();
        //Depending on the input, create game based on it, along with assigning values to numFlags and size
        if (difficulty.equals("Easy")) {
            minefield = new Minefield(5, 5, 5);
            size = 5;
            numFlags = 5;
        } else if (difficulty.equalsIgnoreCase("Medium")) {
            minefield = new Minefield(9, 9, 12);
            size = 9;
            numFlags = 12;
        } else if (difficulty.equalsIgnoreCase("Hard")) {
            minefield = new Minefield(20, 20, 40);
            size = 20;
            numFlags = 40;
        } else {
            System.out.println("Invalid input, difficulty has been set to Easy");
            minefield = new Minefield(5, 5, 5);
            size = 5;
            numFlags = 5;
        }
        //debug scanner and prompt
        Scanner debug = new Scanner(System.in);
        System.out.println("Do you want to enter debug mode? [Yes] [No]");
        String debugAns = debug.nextLine();
        //starting cords scanner and prompt
        Scanner startCords = new Scanner(System.in);
        System.out.println("Enter starting coordinates for a " + size + " x " + size + " grid: [x] [y]");
        String initialCords = startCords.nextLine();
        String[] start = initialCords.split(" ");
        int startX = Integer.parseInt(start[0]);
        int startY = Integer.parseInt(start[1]);
        //check if starting cords are in bounds, if not give user another chance to input correct cords
        while (startX >= size || startX < 0 || startY >= size || startY < 0){
            Scanner anotherStart = new Scanner(System.in);
            System.out.println("Please input an inbounds starting coordinate.");
            String anotherStartInput = anotherStart.nextLine();
            String[] anotherStartCord = anotherStartInput.split(" ");
            int anotherStartX = Integer.parseInt(anotherStartCord[0]);
            int anotherStartY = Integer.parseInt(anotherStartCord[1]);
            startX = anotherStartX;
            startY = anotherStartY;
        }
        //create initial mines, update array, and reveal starting 0's
        minefield.createMines(startX, startY, numFlags);
        minefield.evaluateField();
        minefield.revealStartingArea(startX, startY);
        while (!minefield.gameOver()) {
            //Check for debugging
            if (debugAns.equals("Yes")) {
                minefield.debug();
            }
            System.out.println(minefield);
            //Check if there are still flags remaining to use
            if (numFlags == 0){
                System.out.println("You are out of flags, please do not try to place another flag");
            }
            //Take in the input of the user
            Scanner guess = new Scanner(System.in);
            System.out.println("Enter a coordinate and if you wish to place a flag (Remaining: " + numFlags + ") [x] [y] [flag (true, false)]");
            String guessInput = guess.nextLine();
            //Split up the user input, assign it to an array then assign each part of it to an X value, Y value, and flag.
            String[] userGuess = guessInput.split(" ");
            int xGuess = Integer.parseInt(userGuess[0]);
            int yGuess = Integer.parseInt(userGuess[1]);
            boolean placeFlag = Boolean.parseBoolean(userGuess[2]);
            //Give the user another chance to guess in bounds or without a flag
            while (xGuess >= size || xGuess < 0 || yGuess >= size || yGuess < 0 || (numFlags == 0 && placeFlag)){
                Scanner anotherGuess = new Scanner(System.in);
                System.out.println("Please input an inbounds coordinate, or you are out of flags.");
                String anotherGuessInput = anotherGuess.nextLine();
                String[] anotherUserGuess = anotherGuessInput.split(" ");
                int anotherXGuess = Integer.parseInt(anotherUserGuess[0]);
                int anotherYGuess = Integer.parseInt(anotherUserGuess[1]);
                boolean anotherPlaceFlag = Boolean.parseBoolean(anotherUserGuess[2]);
                xGuess = anotherXGuess;
                yGuess = anotherYGuess;
                placeFlag = anotherPlaceFlag;
            }
            //Increment the number of flags if necessary
            if (placeFlag){
                numFlags--;
            }
            //Check if the user has hit a mine
            if (minefield.guess(xGuess, yGuess, placeFlag)){
                System.out.println(minefield);
                System.out.println("You have hit a mine! Game Over :(");
                break;
            }
        }
        if (minefield.gameOver()) {
            //If no mines were guessed and all other cells are revealed, win condition is met
            System.out.println(minefield);
            System.out.println("You have cleared the field! You won :)");
        }
    }
}
