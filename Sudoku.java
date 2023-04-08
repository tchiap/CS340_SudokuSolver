/**
 * Tom Chiapete
 * Feb 9, 2006
 * Computer Science 340 
 * Assignment:  Sudoku Solver
 * 
 * This program will solve incomplete Sudoku puzzles.  
 * Blanks or unsolved cells should be noted as 0.
 * This program will go through the two dimensional 
 * array with a recursive solver method.
 * Assumptions:  Inputted incomplete Sudoku puzzles are valid.
 * Requires Libraries: 
 * java/io -- for terminal i/o
 * java/util -- requires the use of utility Stack.
 * 
 * Known bugs:  None.
 * 
 * I've tried many cases and returned the correct results. 
 * 
 */

import java.io.*;
import java.util.*;

public class Sudoku
{
    // Instance variables
    private static int [] [] matrix = new int [10] [10]; // Holds our puzzle 9x9.
    
    /**
     * main() method
     * Start by loading a valid file that can be read in using another method.
     * Second, print out the unsolved puzzle.
     * Third, call the resursive method solveSudoku().  This will recursively solve
     * the puzzle.
     * Finally, print out our completely solve Sudoku puzzle.
     */
    public static void main(String [] args)
    {
        
        // If there is an argument, use that as the filename, and pass it to the 
        // loadMatrix() method.  If not, pass an empty string.  loadMatrix() knows 
        // what to do, and will ask for a filename.
        if (args.length > 0)
            loadMatrix(args[0]);
        else
            loadMatrix("");

        // Print out the unfilled Sudoku.
        System.out.println("Unfilled Sudoku:");
        printMatrix();
        
        // Make our first call to solveSudoku().  Pass in starting values of row 0,
        // column 0, and our two dimensional array.
        solveSudoku(0,0,matrix);
        
        // Print out the solved Sudoku.
        System.out.println("\nSolved Sudoku:");
        printMatrix();
    }
    
    /**
     * printMatrix() method.
     * This is a simple method that just prints our two dimensional array to 
     * to the screen in a neat format.
     */
    public static void printMatrix()
    {
        for (int x = 0; x < 9; x++)
        {
            for (int y = 0; y < 9; y++)
            {
                System.out.print(matrix[x][y]+" ");
            }
            // End of row, line break.
            System.out.println();
        }
    }
    
    /** 
     * solveSudoku() method.
     * This is a recursive method that takes in a row parameter,
     * column, and the two dimensional array itself.
     * Returns boolean based on conditions stated in comments below.
     */
    public static boolean solveSudoku(int row, int col, int [][] matrix) 
    {
        // If the row is greater than 8 or column is greater than 8,
        // return true right away.
        if (row > 8 || col > 8) 
            return true;

        // To know what cell we have to be working with now and in the future, 
        // some variables need to be set.  Initially, set the next row to the 
        // current row, and increment the next column value.
        int nextRowNum = row;
        int nextColNum = col + 1;
        
        // If the next column is greater than 8, then move down to the next 
        // row by incrementing the nextRowNum variable.  Then reset the 
        // next column value to 0.
        if (nextColNum > 8) 
        {
            nextRowNum = nextRowNum + 1;
            nextColNum = 0;
        }

        // When the position is not zero, skip this position and move on.
        // Return the boolean value of the recursive call.
        if ( matrix[row][col] != 0 )
            return solveSudoku(nextRowNum, nextColNum, matrix);

        // Return a stack of integers in which we can use to 
        // attempt available numbers by calling the getStack() helper method.
        Stack<Integer> intStack = getStack(row, col, matrix);
        
        // When the stack that was just return isn't empty, pop off the first 
        // entry in the stack and try it at the current position.  
        // Recursively call the solveSudoku() method and if that method returns true, 
        // we must return true from this call.
        while(intStack.empty() == false) 
        {
            matrix[row][col] = intStack.pop();
            if (solveSudoku(nextRowNum, nextColNum, matrix) == true)
                return true;
        }
        
        // Since nothing has been returned thus far, we need to set the current 
        // positions value to 0 again for now, then return false.
        matrix[row][col] = 0;
        return false;
    }
    
    /**
     * loadMatrix() method
     * This method takes in one parameter... A filename.
     * Use the Scanner class to scan in a filename, if necessary, 
     * but also to scan in the file contents.
     * 
     * Place into the two dimensional array to resemble its 9 by 9 shape.
     * 
     * Then this provides invalid filename checking.
     */
    public static void loadMatrix(String filename)
    {
        // If the filename provided as the argument to this method is blank, 
        // we know that we need to now ask for a filename.
        // Read in filename.
        if (filename.equals(""))
        {
            System.out.print("Please enter the filename:  ");
            Scanner input = new Scanner(System.in);
            filename = input.next();
        }
        
        // Try bringing in the file.  If the file is found, there shouldn't be
        // a problem.
        try
        {
            Scanner loadFile = new Scanner(new File(filename));
            
            // Read in file into the 2d array.
            while (loadFile.hasNext())
            {
                for (int x = 0; x < 9; x++)
                {
                    for (int y = 0; y < 9; y++)
                    {
                        matrix[x][y] = loadFile.nextInt();
                    }
                }
            }
        }
        
        // Oh no... The program received a bad file name.  
        // Catch the FileNotFoundException.
        // For this, this program will let the person know that what they entered 
        // was not found.
        catch (FileNotFoundException e)
        {
            System.out.println(filename);
            System.out.println(" was not found.");
            System.out.println("-----------------------------");
            System.exit(1);
        }
    }

    /** 
     * getStack() helper method.
     * This method returns a stack of integers.  The stack of integers is a list 
     * of possible values.  Theoretically, I could have done this just fine 
     * without a stack, but I used the Stack methods to clean up the code for me.
     * To find what possibilites that can be pushed onto the stack, I need to 
     * declare a 9 index boolean array -- those indexes representing actual numbers. 
     * Through the few calculations below, I can find and return a possibilities 
     * stack.  For explaination of calculations, see comments below.
     */
    private static Stack<Integer> getStack(int row, int col, int[][] matrix) 
    {
        // Create a stack in the form of Stack<E>, E being the type Integer.
        Stack<Integer> intStack = new Stack<Integer>();
        
        // Boolean array to know which ints we've used so far.
        boolean usedInts[] = new boolean[10];

        // I have to check two positions off at this point.  
        // From an index position 0 to 8, I have to mark TRUE in 
        // the usedInts array for the current x row at the current column 
        // AND for the current row at the current x column.
        for (int x = 0; x < 9; x++  )
        {
            int chkPos1 = matrix[x][col];
            int chkPos2 = matrix[row][x];
            usedInts[chkPos1] = true;
            usedInts[chkPos2] = true;
        }

        // Find the "square" numbers that we're currently located in.  I found 
        // the easiest way to do this is that for row, take the row minus the 
        // value of the the remainder of the row variable divided by three.  
        // Obviously, do the same thing to find the square column number.
        int squareRowNum = row - (row % 3);
        int squareColumnNum = col - (col % 3);

        
        // In order to figure out the used ints, I'll need a nested for loop.
        // The outter loop -- start initially at the square row number value, 
        // and go up until we reach that value, plus two.  The inner is similar, 
        // working for the column index in the 2d array.  So for each pass 
        // inside both the loops, take the pass's row and column value as both 
        // of the index, and store it in a temporary value.
        // This temp value has to be set to true in the usedInts array as 
        // declared above.
        int currValue = 0;
        for (int x = squareRowNum; x < squareRowNum +3; x++)
        {
            for (int y = squareColumnNum; y < squareColumnNum+3; y++)
            {
                currValue = matrix[x][y];
                usedInts[currValue] = true;
            }
        }

        // When the number in the row is not used, push it onto the stack.
        for (int y = 1; y < 10; y++)
        {
            if (usedInts[y] == false)
            {
                intStack.push(y);
            }
        }
        
        // Return the integer stack.
        return intStack;
    }

}
