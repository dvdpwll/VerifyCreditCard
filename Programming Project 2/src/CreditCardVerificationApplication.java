/*
Student: David Powell
Course: CIT-239
Date: 4/02/15
Program: Programming Project 2, Credit Cards
 */

/*
Instructions:
This program involves writing a Java applicagion which allow the users to Create
and Verify credit card accounts. The user types text commands with operate as shown
in the example.
-create v (create a new visa account)
-create AE (create a new American Express account)
-verify 4578913156489 250.00 (a request to purchase, check the creadit limit and and display approved)
-verify 1536486478747 -125.00 (a request to return an amount, check the limit and display accordingly)

use the charts to calc card numbers

The card data should be stored in a text file. Each line represents one account
format: account_Number | current_available_credit | maximum_credit_limit
The field delimiter character should be the vertical bar "|" char or pipe char.

Extra Credit:
The program saves the card data in a file named "dataFile.txt". If a preexisting
file already exists, the the program renames the preexisting file to a file name
which contains the current date and time:
dataFile_YYYMMDD_HHMMSS.txt

Design Suggestion: Two Classes
*/




import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Random;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CreditCardVerificationApplication {
    //class stuff
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        //main stuff
        String userInput = "q";
        System.out.println("This program accepts the follwing inputs and performs the corresponding actions: ");
        System.out.println("create 'card symbol'");
        System.out.println("verify 'accountNum' 'amount'");
        System.out.println("q");
        System.out.println("----------------------------");
        System.out.println("The follwing credit cards are supported: ");
        System.out.println("American Express - (AE)");
        System.out.println("Visa - (V)");
        System.out.println("MasterCard - (MC)");
        System.out.println("Discover - (DIS)");
        System.out.println("Diners Club - (DINE)");
       
        
        do//do-while loop to quit the program
            {
                //obtain the user input
                userInput = gatherInput();
                System.out.println("You entered: " + userInput);
                //check for "q" to see if we quit the program
                if (userInput.equals("q"))
                {
                    System.out.println("Program exit.");
                    break;
                }
                
                //split the user input
                String command = splitter(userInput, 0);
                
                //switch statement for commands
                switch (command){
                    case "create":
                        //stuff for create command
                        //System.out.println("You entered the create command.");
                        
                        //find card symbol
                        String symbol = splitter(userInput, 1);
                        
                        //find the first digit based on symbol
                        int firstDigit = findFirstDigit(symbol);
                        
                        //generate 15 random numbers for card number
                        int cardNumber[] = new int[16];
                        cardNumber[0] = firstDigit;
                        for (int i=1; i<16; i++){
                            cardNumber[i] = generateNumber(0, 9);
                        }
                        
                        //set credit limits
                        double current_available_credit = 0;
                        double maximum_credit_limit = 0;
                            
                        if (cardNumber[15] < 5){
                            current_available_credit = 1000.00;
                            maximum_credit_limit = 1000.00;
                        }
                        else{
                            current_available_credit = 500.00;
                            maximum_credit_limit = 500.00;
                        }
                            
                        //convert cardNumber array to a string
                        String account_Number = toString(cardNumber);
                        
                        //pull data from a file
                        Scanner scan = new Scanner(new File("dataFile.txt"));
                        List<String> liness = new ArrayList<String>();
                        while (scan.hasNextLine()){
                            liness.add(scan.nextLine());
                        }
                        String[] sizeArray = liness.toArray(new String[0]);
                                                
                        //find how many accounts there are
                        int size = sizeArray.length;
                        
                        //add new card data to text file
                        java.io.File file = new java.io.File("dataFile.txt");
                        try{
                            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("dataFile.txt", true)));
                            out.print(account_Number);
                            out.print("|");
                            out.print(current_available_credit);
                            out.print("|");
                            out.print(maximum_credit_limit);
                            out.println("");
                            out.close();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                        
                        //verify output
                        System.out.println("New account created for credit card symbol" + symbol + ":");
                        System.out.println("Account Number: " + account_Number);
                        System.out.println("credit limit: " + maximum_credit_limit);
                        System.out.println("There are currently " + size + " records on file.");
                        
                        //check
                        //System.out.println("account number: " + account_Number);
                        //System.out.println("avail credit: " + current_available_credit);
                        //System.out.println("credit limit: " + maximum_credit_limit);
                        
                        break;
                    case "verify":
                        //stuff for verify command
                        System.out.println("You entered the verify command.");
                        
                        //pull data from a file
                        Scanner sc = new Scanner(new File("dataFile.txt"));
                        List<String> lines = new ArrayList<String>();
                        while (sc.hasNextLine()){
                            lines.add(sc.nextLine());
                        }
                        String[] arr = lines.toArray(new String[0]);
                        
                        //find user request by splitting
                        String userAccNum = splitter(userInput, 1);
                        String userMoneyReq = splitter(userInput, 2);
                        
                        //find user account in the stored data
                        size = arr.length;
                        int dataPosition = searchData(userAccNum, arr, size);
                        
                        //convert credit limit to doubles
                        double currentCredit = toDouble(arr, dataPosition, 1);
                        double maxLimit = toDouble(arr, dataPosition, 2);
                        double moneyRequest = toDouble(userMoneyReq);
                        
                        //use the credit doubles to check the request
                        boolean trigger = false;
                        if(moneyRequest > 0){
                           trigger = debit(moneyRequest, currentCredit, maxLimit); 
                        }
                        else if (moneyRequest < 0){
                           trigger = credit(moneyRequest, currentCredit, maxLimit);
                        }
                        else{
                            System.out.println("No change to credit.");
                        }
                        
                        //if trigger is true then follow through witht he request
                        if(trigger == true){
                            currentCredit = currentCredit-moneyRequest;
                            arr[dataPosition] = toString(userAccNum, currentCredit, maxLimit);
                            System.out.println("AUTHORIZATION GRANDED");
                            System.out.println("Account Number: " + userAccNum);
                            System.out.println("Transaction Amount: " + moneyRequest);
                            System.out.println("Available Credit: " + currentCredit);
                            //moved data back into file
                            try{
                                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("dataFile.txt", false)));
                                for(int i = 0; i < arr.length; i++){
                                    String s;
                                    s = arr[i];
                                    out.println(s);
                                }
                                out.close();
                            }catch(IOException e){
                                e.printStackTrace();
                            }
                        }
                        else{
                            System.out.println("Transaction denied.");
                        }
                        
                        System.out.println("There are currently " + size + " records on file.");
                        
                        //check
                        //for(int i = 0; i < 3; i++){
                        //    System.out.println(arr[i]);
                        //}
                        //System.out.println("acount1: " + arr[0]);
                        //System.out.println("acount2: " + arr[1]);
                        //System.out.println("acount3: " + arr[2]);
                        
                        break;
                    default:
                        //stuff for error
                        System.out.println("You entered an invalid command.");
                        break;
                }
                
            } while (!userInput.equals("q"));
            //end of do-while loop to quit the program
    }
    
    //method stuff
    //method to gather user input
    public static String gatherInput(){
        Scanner input = new Scanner(System.in);
        
            //gather in put
            System.out.print("Enter command: ");
            String coordinate = input.nextLine();
            return coordinate;
    }
    
    //method to split the user input
    public static String splitter(String userInput, int position){
        //move text to an array
        String[] textArray = userInput.split(" ");
        
        //return the desired postion of the array
        return textArray[position];
    }
    
    //method to find the first digit of a specified type of card
    public static int findFirstDigit(String symbol){
        int firstDigit = 0;
        switch (symbol){
            case "AE": firstDigit = 3;
                break;
            case "V": firstDigit = 4;
                break;
            case "MC": firstDigit = 5;
                break;
            case "DIS": firstDigit = 6;
                break;
            case "DINE": firstDigit = 7;
                break;
            default:
                System.out.println("You entered an invalid card symbol.");
                break;
        }
        //return the first digit
        return firstDigit;
    }
    
    //method to generate a random number
    public static int generateNumber(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min)+1) + min;
        return randomNum;    
    }

    //methos to convert an int array to String
    public static String toString(int[] cardNumber){
        String stringNum = "";
        for (int i : cardNumber){
            stringNum+=Integer.toString(i);
        }
        return stringNum;
    }
    public static String toString(String cardNumber, double availableCredit, double creditLimit){
        String stringData = "";
        stringData += cardNumber + "|" + Double.toString(availableCredit) + "|" + Double.toString(creditLimit);
        System.out.println(stringData);
        return stringData;
    }
    
    //method to find the requested account number
    public static int searchData(String userAccNum, String arr[], int size){
        int position = 0;
        String[] tmpArray = {"123", "456", "789"};
        
        for(int i=0; i < size; i++){
            tmpArray = arr[i].split("\\|");
            
            //search for account number and return position
            if(tmpArray[0].equals(userAccNum)){
                position = i;
            }
        }
        return position;
    }
    
    //method to convert string to double
    public static double toDouble(String arr[], int dataPosition, int position){
        //split the desired account row
        String[] tmpArray = arr[dataPosition].split("\\|");
        //convert to double
        double convDouble = Double.parseDouble(tmpArray[position]);
        return convDouble;
    }
    public static double toDouble(String user){
        double convDouble = Double.parseDouble(user);
        return convDouble;
    }
    
    //check if the request is valid
    public static boolean debit(double request, double currentCredit, double maxCredit){
        boolean trigger = false;
        if ((currentCredit - request) > 0){
            trigger = true;
        }
        return trigger;
    }
    public static boolean credit(double request, double currentCredit, double maxCredit){
        boolean trigger = false;
        if((currentCredit - request) < maxCredit){
            trigger = true;
        }
        return trigger;
    }
}
