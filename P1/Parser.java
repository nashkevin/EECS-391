/**
 * Puzzle
 *
 * @author   Kevin Nash (kjn33)
 * @version  2017.9.27
 */
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
    
    public enum Command {
        SETSTATE, RANDOMIZESTATE, PRINTSTATE, MOVE, SOLVE, MAXNODES
    };
    
    public static void main (String[] args) throws IOException, FileNotFoundException {
        if (1 == args.length) {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            for (String command; (command = br.readLine()) != null; ) {
                try {
                    System.out.println("$ " + command);
                    performCommand(tokenizeCommand(command));
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
            String[] command = tokenizeCommand(scan.nextLine());
            while (!command[0].equalsIgnoreCase("exit")) {
                try {
                    performCommand(command);
                }
                catch (IllegalArgumentException e) {
                    System.err.println("Invalid input");
                }
                System.out.print("$ ");
                command = tokenizeCommand(scan.nextLine());
            } 
            scan.close();
        }
    }
    
    private static String[] tokenizeCommand(String command) {
        String regex = "\"([^\"]*)\"|(\\S+)";
        Matcher m = Pattern.compile(regex).matcher(command);
        LinkedList<String> tokens = new LinkedList<String>();
        while (m.find()) {
            String token = (m.group(1) != null) ? m.group(1) : m.group(2);
            tokens.add(token);
        }
        return tokens.toArray(new String[tokens.size()]);
    }
    
    private static void performCommand(String[] command) {
        switch (Command.valueOf(command[0].toUpperCase())) {
            case SETSTATE:
                break;
            case RANDOMIZESTATE:
                break;
            case PRINTSTATE:
                break;
            case MOVE:
                break;
            case SOLVE:
                break;
            case MAXNODES:
                break;
        }
    }
}
