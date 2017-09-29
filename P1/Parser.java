/**
 * Parser
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.27
 */
package P1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Parser {
    
    private enum Command {
        SETSTATE, RANDOMIZESTATE, PRINTSTATE, MOVE, SOLVE, MAXNODES
    }
    
    private Puzzle puzzle;
    
    public static void main (String[] args) throws IOException, FileNotFoundException {
        Parser p = new Parser();
        if (1 == args.length) {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            for (String command; (command = br.readLine()) != null; ) {
                try {
                    System.out.println("$ " + command);
                    p.performCommand(command);
                }
                catch (IllegalArgumentException e) {
                    System.err.println("Invalid input");
                }
            }
            br.close();
        }
        else {
            Scanner scan = new Scanner(System.in);
            System.out.print("$ ");
            String[] command = p.tokenizeCommand(scan.nextLine());
            while (!command[0].equalsIgnoreCase("exit")) {
                try {
                    p.performCommand(command);
                }
                catch (IllegalArgumentException e) {
                    System.err.println("Invalid input");
                }
                System.out.print("$ ");
                command = p.tokenizeCommand(scan.nextLine());
            } 
            scan.close();
        }
    }
    
    private String[] tokenizeCommand(String command) {
        String regex = "\"([^\"]*)\"|(\\S+)";
        Matcher m = Pattern.compile(regex).matcher(command);
        LinkedList<String> tokens = new LinkedList<String>();
        while (m.find()) {
            String token = (m.group(1) != null) ? m.group(1) : m.group(2);
            tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private void performCommand(String command) {
        performCommand(tokenizeCommand(command));
    }
    
    private void performCommand(String[] command) {
        switch (Command.valueOf(command[0].toUpperCase())) {
            case SETSTATE:
                this.puzzle = new Puzzle(command[1]);
                break;
            case RANDOMIZESTATE:
                this.puzzle = new Puzzle();
                this.puzzle.scrambleGoal(Integer.parseInt(command[1]));
                break;
            case PRINTSTATE:
                printState();
                break;
            case MOVE:
                break;
            case SOLVE:
                break;
            case MAXNODES:
                break;
        }
    }
    
    private void printState() {
        try {
            System.out.println(this.puzzle.getState().toString());
        }
        catch (NullPointerException e) {
            System.err.println("No state has been set for this puzzle");
        }
    }
}
