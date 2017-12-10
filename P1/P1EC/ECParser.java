/**
 * ECParser
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.10.23
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

public class ECParser {
    
    private enum Command {
        SETSTATE, RANDOMIZESTATE, PRINTSTATE, SOLVE, MAXNODES
    }
    
    private ECPuzzle puzzle;
    private int maxNodes = -1;
    
    public static void main (String[] args) throws IOException, FileNotFoundException {
        ECParser p = new ECParser();
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
            while (0 < command.length && !command[0].equalsIgnoreCase("exit")) {
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
                this.puzzle = new ECPuzzle(command[1]);
                break;
            case RANDOMIZESTATE:
                this.puzzle = new ECPuzzle();
                this.puzzle.scrambleGoal(Integer.parseInt(command[1]));
                break;
            case PRINTSTATE:
                printState();
                break;
            case SOLVE:
                try {
                    solve(command[1].toLowerCase(), command[2].toLowerCase());
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    solve(command[1].toLowerCase());
                }
                break;
            case MAXNODES:
                maxNodes(command[1]);
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
    
    private void solve(String algorithm) {
        try {
            if (0 < maxNodes) {
                this.puzzle.setMaxNodes(maxNodes);
            }
            this.puzzle.aStarSearch();
        }
        catch (NullPointerException e) {
            System.err.println("No state has been set for this puzzle");
        }
    }
    
    private void solve(String algorithm, String option) {
        try {
            if (0 < maxNodes) {
                this.puzzle.setMaxNodes(maxNodes);
            }
            this.puzzle.beamSearch(Integer.parseInt(option));
        }
        catch (NullPointerException e) {
            System.err.println("No state has been set for this puzzle");
        }
    }
    
    private void maxNodes(String maxNodes) {
        this.maxNodes = Integer.parseInt(maxNodes);
    }
}
