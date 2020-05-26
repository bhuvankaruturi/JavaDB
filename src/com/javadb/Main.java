package com.javadb;

import com.javadb.catalog.Bootstrap;
import com.javadb.processors.CreateQueryProcessor;
import com.javadb.processors.InsertQueryProcessor;
import com.javadb.processors.SelectQueryProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static String prompt = "JavaDB> ";
    static String version = "v1.0b(example)";
    static String copyright = "2020 No rights reserved";
    static boolean isExit = false;

    /*
     *  The Scanner class is used to collect user commands from the prompt
     *  There are many ways to do this. This is just one.
     *
     *  Each time the semicolon (;) delimiter is entered, the userCommand
     *  String is re-populated.
     */
    static Scanner scanner = new Scanner(System.in).useDelimiter(";");

    /** ***********************************************************************
     *  Main method
     */
    public static void main(String[] args) {

        /* create data store */
        Bootstrap.init();

        /* Display the welcome screen */
        splashScreen();

        /* Variable to collect user input from the prompt */
        String userCommand = "";

        while(!isExit) {
            System.out.print(prompt);
            /* toLowerCase() renders command case insensitive */
            userCommand = scanner.next().replace("\n", " ").replace("\r", "").trim();
            // userCommand = userCommand.replace("\n", "").replace("\r", "");
            parseUserCommand(userCommand);
        }
        System.out.println("Exiting...");


    }

    /** ***********************************************************************
     *  Static method definitions
     */

    /**
     *  Display the splash screen
     */
    public static void splashScreen() {
        System.out.println(line("-",80));
        System.out.println("Welcome to JavaDB"); // Display the string.
        System.out.println("JavaDB Version " + getVersion());
        System.out.println(getCopyright());
        System.out.println("\nType \"help;\" to display supported commands.");
        System.out.println(line("-",80));
    }

    /**
     * @param s The String to be repeated
     * @param num The number of time to repeat String s.
     * @return String A String object, which is the String s appended to itself num times.
     */
    public static String line(String s,int num) {
        String a = "";
        for(int i=0;i<num;i++) {
            a += s;
        }
        return a;
    }

    /**
     *  Help: Display supported commands
     */
    public static void help() {
        System.out.println(line("*",80));
        System.out.println("SUPPORTED COMMANDS\n");
        System.out.println("All commands below are case insensitive\n");
        System.out.println("SHOW TABLES;");
        System.out.println("\tDisplay the names of all tables.\n");
        System.out.println("SELECT * FROM <table_name> [WHERE <condition>];");
        System.out.println("\tDisplay table records whose optional <condition>");
        System.out.println("\tis <column_name> = <value>.\n");
        System.out.println("CREATE TABLE <table_name> (<column_list>");
        System.out.println("\t<column_list> is the column definitions for the table\n");
        System.out.println("INSERT INTO <table_name> VALUE (<values_list>)");
        System.out.println("\t<value_list> is the list of comma separated values\n");
        System.out.println("DROP TABLE <table_name>;");
        System.out.println("\tRemove table data (i.e. all records) and its schema.\n");
        System.out.println("VERSION;");
        System.out.println("\tDisplay the program version.\n");
        System.out.println("HELP;");
        System.out.println("\tDisplay this help information.\n");
        System.out.println("EXIT;");
        System.out.println("\tExit the program.\n");
        System.out.println(line("*",80));
    }

    /** return the JavaDB version */
    public static String getVersion() {
        return version;
    }

    public static String getCopyright() {
        return copyright;
    }

    public static void displayVersion() {
        System.out.println("JavaDB Version " + getVersion());
        System.out.println(getCopyright());
    }

    public static void parseUserCommand (String userCommand) {

        /* commandTokens is an array of Strings that contains one token per array element
         * The first token can be used to determine the type of command
         * The other tokens can be used to pass relevant parameters to each command-specific
         * method inside each case statement */
        // String[] commandTokens = userCommand.split(" ");
        ArrayList<String> commandTokens = new ArrayList<String>(Arrays.asList(userCommand.split(" ")));

        /*
         *  This switch handles a very small list of hardcoded commands of known syntax.
         *  You will want to rewrite this method to interpret more complex commands.
         */
        switch (commandTokens.get(0).toLowerCase()) {
            case "select":
                parseQuery(userCommand);
                break;
            case "insert":
                insertRecord(userCommand);
                break;
            case "create":
                parseCreateTable(userCommand);
                break;
            case "show":
                parseShowTable(userCommand);
                break;
            case "help":
                help();
            case "version":
                displayVersion();
                break;
            case "exit":
            case "quit":
                isExit = true;
                break;
            default:
                System.out.println("I didn't understand the command: \"" + userCommand + "\"");
                break;
        }
    }

    /**
     *  method for handling insert statement
     * @param insertTableString is a String of the user input
     */
    public static void insertRecord(String insertTableString) {
        new InsertQueryProcessor(insertTableString);
    }


    /**
     *  method for executing select queries
     *  @param queryString is a String of the user input
     */
    public static void parseQuery(String queryString) {
        new SelectQueryProcessor(queryString);
    }

    /**
     *  method for executing show table query
     *  @param showQuery is a String of the user input
     */
    public static void parseShowTable(String showQuery) {
        String[] tokens = showQuery.toLowerCase().trim().split("\\s");
        if (tokens.length < 2 || !tokens[1].equals("tables") || tokens.length > 2) {
            System.out.println("Invalid show tables query");
            return;
        }
        String queryString = "SELECT * FROM database_tables";
        new SelectQueryProcessor(queryString);
    }

    /**
     *  Stub method for creating new tables
     *  @param createTableString is a String of the user input
     */
    public static void parseCreateTable(String createTableString) {
        new CreateQueryProcessor(createTableString);
    }
}
