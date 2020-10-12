// Authors: Maik De Leon Lopez, Jason Mendez, Dylan Dickerson
// CSC 471-01 
// Project #1

// Note: Sometimes it encounters an error with parsing integers, this is due to the nature of dealing with string manipulation. When it does work it works as it should.

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

public class NFAtoDFA {

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		System.out.println("Please enter the number of states: ");
		int numStates = reader.nextInt(); // Receive User Input for number of states
		if (numStates < 1) {
			System.out.println("Invalid number of states.\nExiting...");
			System.exit(1);
		}
		System.out.print("State set of the NFA = {");
		int States[] = new int[numStates]; // generate these states
		for (int i = 0; i < numStates - 1; i++) { // Since index starts from 0
			States[i] = i + 1; // Our state values will be from 1-numStates
			System.out.print((i + 1) + ",");
		}
		States[numStates - 1] = numStates;
		System.out.print(numStates + "}\n"); // Done this way for formatting purposes

		System.out.println("Please enter the number of symbols in the alphabet: ");
		int numAlpha = reader.nextInt(); // Receive User Input for the size of alphabet
		if (numAlpha < 1) {
			System.out.println("Invalid number of symbols.\nExiting...");
			System.exit(1);
		}
		System.out.println("Enter the Symbols in the alphabet: ");
		String alphabet[] = new String[numAlpha + 1];
		for (int i = 0; i < numAlpha; i++) {
			alphabet[i] = reader.next(); // User enters the alphabet
		}
		alphabet[numAlpha] = "epsilon"; // We automatically add epsilon as part of our NFA alphabet
		System.out.print("Alphabet of the NFA = {");
		for (int i = 0; i < numAlpha - 1; i++) {
			System.out.print(alphabet[i] + ",");
		}
		System.out.print(alphabet[numAlpha - 1] + "}\n"); // Done this way for formatting purposes

		System.out.println("Enter the transition function result in set format \"{1,2,...}\": ");
		ArrayList<Integer>[][] Delta = new ArrayList[numStates][numAlpha + 1];
		for (int i = 0; i < numStates; i++) {
			for (int j = 0; j < numAlpha + 1; j++) {
				System.out.print("Delta(" + (i + 1) + "," + alphabet[j] + ") = ");
				Delta[i][j] = (extractIntegers(reader.next())); // Extract the integer values entered by user and place
																// them in an ArrayList
			}
		}

		System.out.println("Please enter the start state: ");
		int startState = reader.nextInt(); // User input for the start state

		System.out.print("Please enter all final states on one line in format \"{1,2,...,n)\": ");
		ArrayList<Integer> finalStates = extractIntegers(reader.next()); // Place our final states into an arrayList
		reader.close();
		System.out.println("\n================Equivalent DFA==================");
		NtoDFA(States, alphabet, Delta, startState, finalStates); // Convert our NFA to an equivalent DFA
	}

	// This method extracts the integer values in a string and places them into an
	// arrayList
	public static ArrayList extractIntegers(String s) {
		if(s.contains("{") && s.contains("}")) {
			s = s.replace("{", ""); // Get rid of any braces
			s = s.replace("}", "");
		}
		ArrayList<Integer> intList = new ArrayList<Integer>();
		String[] inputNumber = s.split(","); // inputNumber will now contain the numbers which are separated by commas
		for (int i = 0; i < inputNumber.length; i++) {
			intList.add(Integer.parseInt(inputNumber[i]));// we add these numbers to our list
		}
		return intList;
	}

	// This method converts our NFA to an equivalent DFA
	public static void NtoDFA(int[] States, String[] alphabet, ArrayList[][] Delta, int startState,
			ArrayList finalStates) {
		String powerSet[] = new String[(int) Math.pow(2, States.length)]; // Our power set is equal to 2^|Set|
		powerSet[0] = ""; // We set the first element of our powerSet to be empty
		for (int i = 1; i < powerSet.length; i++) { // Start from index 1 to the length of powerSet
			powerSet[i] = ","; // Used for formatting
			for (int j = 0; j < States.length; j++) { // for every state we want to select using binary operations
				if ((i & (1 << j)) > 0) { // if i binary and with j binary shifted to the left by 1 is != 0
					powerSet[i] += States[j] + ","; // we add that state to our powerSet
				}
			}
		}
		System.out.print("State set of the DFA = { "); // We reformat the powerSet and print it out nicely
		System.out.print("{" + powerSet[0] + "},");
		for (int i = 1; i < powerSet.length - 1; i++) {
			powerSet[i] = powerSet[i].replaceFirst(",", "");
			powerSet[i] = powerSet[i].substring(0, powerSet[i].length() - 1);
			System.out.print("{" + powerSet[i] + "},");
		}
		powerSet[powerSet.length - 1] = powerSet[powerSet.length - 1].replaceFirst(",", "");
		powerSet[powerSet.length - 1] = powerSet[powerSet.length - 1].substring(0,
				powerSet[powerSet.length - 1].length() - 1);
		System.out.print("{" + powerSet[powerSet.length - 1] + "} }\n");

		System.out.print("Alphabet of the DFA = {"); // Our alphabet remains the same but we don't include epsilon
		for (int i = 0; i < alphabet.length - 2; i++) {
			System.out.print(alphabet[i] + ",");
		}
		System.out.print(alphabet[alphabet.length - 2] + "}\n");

		System.out.println("Transition function of the DFA:");	// Our new Delta Prime will be of size powerSet by alphabet
		ArrayList<Integer>[][] DeltaP = new ArrayList[powerSet.length][alphabet.length - 1];
		System.out.print("Delta'({" + powerSet[0] + "}," + alphabet[0] + ") = empty" + "\n");
		System.out.print("Delta'({" + powerSet[0] + "}," + alphabet[1] + ") = empty" + "\n");
		for (int i = 1; i < powerSet.length; i++) {
			for (int j = 0; j < alphabet.length - 1; j++) {	// Placing values in the correct position in our matrix
				String e = "";
				ArrayList<Integer> intList = new ArrayList<Integer>();
				String[] inputNumber = powerSet[i].split(","); // inputNumber will now contain the numbers which are
																// separated by commas
				for (int p = 0; p < inputNumber.length; p++) {
						intList.add(Integer.parseInt(inputNumber[p]));// we add these numbers to our list
				}
				// intList now has all the states we will have to transform from our old Delta function
				for(int p = 0; p < intList.size(); p++) { // for every value in the list append the calcE(old delta function values)
					if(Delta[intList.get(p)-1][j].size() == 1 && (int)Delta[intList.get(p)-1][j].get(0)==0) {
						e += "empty";
					}
					else {
						for(int k = 0; k < Delta[intList.get(p)-1][j].size(); k++) {
							e += calcE(Delta, alphabet, (int)Delta[intList.get(p)-1][j].get(k))+",";	
						}
					}
				}
				if(e.contentEquals("empty")) {		// if the string says empty
					e = " "+e;		// just add a space before for formatting
				}
				else {
					e = e.replace("empty", "");	// replace any strings that say empty from our string
					e = formatString(e);	// format our string to remove any duplicates
					e = sortString(e);		// sort our string values so it looks right
					e = "{"+e+"}";	// add brackets before and after the string
				}
				System.out.print("Delta'({" + powerSet[i] + "}," + alphabet[j] + ") =" + e + "\n");	// print out every delta' for given set and input
			}
		}

		System.out.print("Start state of the DFA = {"); // Calculate E(startState) to get our New Start State
		String tempString = calcE(Delta, alphabet, startState);	
		tempString = tempString.replace("[", "");	// get rid of any [ since that's how arrayLists' toString method prints them
		tempString = tempString.replace("]", "");
		System.out.println(tempString + "}");

		System.out.print("Final state set of the DFA = {");	// To get our final states, it is any set in our power set that contains our original final set
		for (int i = 0; i < powerSet.length; i++) {
			for (int j = 0; j < finalStates.size(); j++) {
				String temp = finalStates.toString().replace("[", "");
				temp = temp.replace("]", "");
				if (powerSet[i].contains(temp)) {	// if the powerSet contains any of our final state
					System.out.print("{" + powerSet[i] + "}");	// print it out
				}
			}
		}
		System.out.println("}");
	}

	public static String calcE(ArrayList[][] Delta, String[] alphabet, int state) {
		String result = "";
		if (state == 0) {	// if our state is the empty state then just return 0
			return "0";
		} 
		else if (Delta[state - 1][alphabet.length - 1].size() == 1	
				&& (int) Delta[state - 1][alphabet.length - 1].get(0) == 0) {	// if our delta function points us to an empty set just return the state itself
			result += (state) + "";
		} else {
			result += state + ",";
			for (int i = 0; i < Delta[state - 1][alphabet.length - 1].size(); i++) {	//else for every state in our delta function
				int j = (int) Delta[state - 1][alphabet.length - 1].get(i);	// calculate the e of that state)
				result += calcE(Delta, alphabet, j);
			}
		}
		return result;
	}
	
	// this method formats the string making it look nice
	public static String formatString(String string) {
		String myS = "";
		int index;
		for(int i = 0; i < string.length(); i++) {	// gets rid of the duplicates
			char c = string.charAt(i);
			index = string.indexOf(c, i+1);
			if(index == -1) {	// if the next character is not the same
				myS += ""+c;	// add that character to our string
			}
			else if(c == ',') {	// if our character is a ,
				myS += ""+c;	// add that , to our string
			}
		}
		if(myS.contains(",,")) {	// if we have double ,, just leave one of them
			myS = myS.replace(",,", ",");
		}
		while(myS.endsWith(",")) {	// if we have , at the end shorten the string by 1
				myS = myS.substring(0, myS.length()-1);
		}
		return myS;
	}
	
	//this method sorts the string integer values
	public static String sortString(String str) {
		ArrayList list = extractIntegers(str);	// we use our preexisting method to get an arrayList of integers
		int[] arr = new int[list.size()];
		String mystr = "";
		for(int i = 0; i < list.size(); i++) {	// we copy the values of the arrayList into an array for ease of use
			arr[i] = (int)list.get(i);
		}
		for(int i = 0; i < arr.length-1; i++) {	// we preform bubble sort to sort the array
			for(int j = 0; j < arr.length-1-i; j++) {
				if(arr[j] > arr[j+1] && arr[j] !=0) {
					int temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
			}
		}
		for(int i = 0; i < arr.length; i ++) {
			mystr += arr[i]+",";	// we add the commas to the end of each value in our array
		}
		if(mystr.endsWith(",")) {
			mystr = mystr.substring(0, mystr.length()-1);	// makes sure that our last character isn't a ,
		}
		return mystr;
	}
}