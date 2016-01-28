import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class TSPSolver {
	static int[] currentPermute;
	static int[] roses;
	static int bouquet;
	static int[][] matrix;
	static int lowEstimate;
	static int rose;

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("Start time: "+System.currentTimeMillis());
		TSPSolver tsp = new TSPSolver();
		NearestNeighbor2 nnSolver = new NearestNeighbor2();
		String matrixString = tsp.fileReadIn();
		matrix = tsp.StringToIntMatrix(matrixString);
		lowEstimate = nnSolver.oldNearestNeighborRun(matrix);
		long possiblePermutes = (factorial(matrix.length - 1)/2);
		currentPermute = new int[matrix[0].length];
		
		roses = tsp.roseColoredPath(matrix);
		bouquet = 0;
		
		for (int petal : roses){
			bouquet += petal;
		}
		
		rose = bouquet;
		
		System.out.println("Rose colored glasses path = " + bouquet);
		
		for(int i = 0; i < currentPermute.length; i++){
			currentPermute[i] = i;
		}
		
		int[] bestArray = currentPermute.clone();
		int bestValue = Integer.MAX_VALUE;
		int currentValue = 0;
		
		if ((rose + lowEstimate)/2 < lowEstimate - rose){
			for (long i = 0; i < possiblePermutes; i++) {
				
				currentValue = tsp.getPermuteValueRose();
				if(currentValue <= bestValue){
					bestArray = currentPermute.clone();
					bestValue = currentValue;
					lowEstimate = bestValue;
				}
				rose = bouquet;
		//		System.out.print(printArray(currentPermute));		
		//		System.out.println("\tDistance:\t" + currentValue);
				currentPermute = tsp.getLexes(currentPermute);
			}
		} else{
		
			for (long i = 0; i < possiblePermutes; i++) {
				
				currentValue = tsp.getPermuteValue();
				if(currentValue <= bestValue){
					bestArray = currentPermute.clone();
					bestValue = currentValue;
					lowEstimate = bestValue;
				}
		//		System.out.print(printArray(currentPermute));		
		//		System.out.println("\tDistance:\t" + currentValue);
				currentPermute = tsp.getLexes(currentPermute);
			}
		}
		
		System.out.println("Best path:\t" + tsp.MatrixLineToString(bestArray) + "\tBest Distance:\t" + bestValue);
		System.out.println("Last path:\t" + tsp.MatrixLineToString(currentPermute) + "\tLast Distance:\t" + currentValue);
		System.out.println("Stop time: "+System.currentTimeMillis());


	}
	

	/*
	public void TSPRun() throws FileNotFoundException{
		TSPSolver2 tsp = new TSPSolver2();
		String matrixString = tsp.fileReadIn();
		int[][] matrix = tsp.StringToIntMatrix(matrixString);
		long possiblePermutes = (factorial(matrix.length) - 1 ) /2;
		currentPermute = new int[matrix[0].length];
		for(int i = 0; i < currentPermute.length; i++){
			currentPermute[i] = i;
		}
		int[] bestArray = currentPermute.clone();
		int bestValue = Integer.MAX_VALUE;
		int currentValue = 0;
		
		
		for (long i = 0; i < possiblePermutes; i++) {
			currentValue = tsp.getPermuteValue(currentPermute, matrix);
			if(currentValue <= bestValue){
				bestArray = currentPermute.clone();
				bestValue = currentValue;
			}
			System.out.print(printArray(currentPermute));
			
			System.out.println("\tDistance:\t" + tsp.getPermuteValue(currentPermute, matrix));
			currentPermute = tsp.getLexes(currentPermute);
		}
		
		System.out.println("Best path: " + printArray(bestArray) + "\tBest Distance:\t" + bestValue);		
		
	}
	*/
	
	
	/*
	 * The getPermuteValue will look at a permutation and return it's value.
	 */	
	public int getPermuteValue(){
		int num = 0;
		
		for(int i = 0; i < currentPermute.length - 1; i++){
			num += matrix[currentPermute[i]][currentPermute[i+1]];
			if(num > lowEstimate){
				insertionSort(currentPermute, i);
				return num;
			}
		}
		
		num += matrix[currentPermute[currentPermute.length-1]][currentPermute[0]];
		
		return num;
	}
	
	/*
	 * The getPermuteValue will look at a permutation and return it's value, and increases branch and bound performance.
	 * Naturally, the rose references are intended to reflect the mind-bogglingly optimistic path length estimates that
	 * produced those variables.
	 * 
	 * After testing, this method does not increase running speed.  It actually causes it to take significantly longer.
	 * Further improvements will only be in code optimization, rather than algorithm optimization assuming Java.
	 */	
	public int getPermuteValueRose(){
		int num = 0;
		
		for(int i = 0; i < currentPermute.length - 1; i++){
			num += matrix[currentPermute[i]][currentPermute[i+1]];
			rose -= roses[currentPermute[i]];
			if((num + rose) > lowEstimate){
				insertionSort(currentPermute, i);
				return num + rose;
			}
		}
		
		num += matrix[currentPermute[currentPermute.length-1]][currentPermute[0]];
		
		return num;
	}

	/*
	 * nextPermutaion will return the next permutation of the input.
	 */
	public int[] getLexes(int[] currentPermute) {
		currentPermute = next_permutation(currentPermute);
		return currentPermute;
	}

	/*
	 *
	 * 
	 * When given an array of ints this will return the corresponding symbols
	 * from a long list of symbols to indicate which order jumps are made in.
	 */
	public String MatrixLineToString(int[] matrixJumps) {
		String alteredMatrixLine = "";
		String lineValues = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789)!@#$%^&*(";

		for (int i = 0; i < matrixJumps.length; i++) {
			alteredMatrixLine += lineValues.substring(matrixJumps[i],
					matrixJumps[i] + 1) + "\t";
		}
		alteredMatrixLine += alteredMatrixLine.substring(0, 1);

		return alteredMatrixLine;
	}

	/*
	 * The StringToIntMatrix takes the string that has been entered into the
	 * method requirements and then converts it into a new int[][] matrix. It
	 * then returns the matrix.
	 */
	public int[][] StringToIntMatrix(String matrixString) {
		int[][] matrix;
		String[] singleMatrixLine;
		String[] matrixLinesArray;

		matrixLinesArray = matrixString.split("\n");
		matrix = new int[matrixLinesArray.length][matrixLinesArray.length];
		for (int i = 0; i < matrixLinesArray.length; i++) {
			singleMatrixLine = matrixLinesArray[i].split("\\D+?");
			for (int j = 0; j < singleMatrixLine.length; j++) {
				matrix[i][j] = Integer.parseInt(singleMatrixLine[j]);
			}
		}

		return matrix;
	}
	
	public int[] roseColoredPath(int[][] findCheap){
		int[] rosey = new int[findCheap.length];
		int index = 0;
		int cheapest;
		
		for (int[] origin : findCheap){
			cheapest = Integer.MAX_VALUE;
			for (int cig : origin){
				if (cig < cheapest){
					cheapest = cig;
				}
			}
			rosey[index] = cheapest;
			index++;
		}
		
		
		return rosey;
	}

	/*
	 * The fileReadIn method asks for the file that the user wants to use as
	 * their dataset. It then reads in each line of the file and puts this into
	 * a variable after attaching a newline character to the end of the line.
	 * This variable is then returned for the use of the rest of the program.
	 */
	public String fileReadIn() throws FileNotFoundException {
		Scanner inputGetter = new Scanner(System.in);
		Scanner sc;
		String fileName = "", inputArrayString = "";
		File file;

		System.out
				.print("Please enter the name of the file in which the array is stored: ");
		fileName = inputGetter.nextLine();
		file = new File(fileName);
		sc = new Scanner(file);

		while (sc.hasNext()) {
			inputArrayString += sc.nextLine() + "\n";
		}

		sc.close();
		inputGetter.close();

		return inputArrayString;
	}

	/*
	 * The factorial method will return the factorial of any int in the method's
	 * input.
	 */
	public static long factorial(long n) {
		long fact = 1; // this will be the result
		for (long i = 1; i <= n; i++) {
			fact *= i;
		}
		return fact;
	}

	/*
	 * The follwoing methods are copied from the LexPermsOriginal and modified
	 * for use in this class. print, swap and next_permute also appear in
	 * LexPermsOriginal in different forms. Check the class LexPermsOriginal if
	 * you need to see their original code.
	 */
	public static String print(int[] array) {
		String str = "";
		for (int tmp : array) {
			str += " " + tmp;
		}
		str += "\n";
		return str;
	}

	public static int[] next_permutation(int[] array) {
		int i, j;
		for (i = array.length - 2; i >= 0; i--) {
			if (array[i] < array[i + 1])
				break;
		}
		if (i < 0) {
			//System.out.println("End");
			// System.exit(0);
			return array;
		}

		for (j = array.length - 1; j > i; j--) {
			if (array[j] > array[i])
				break;
		}

		swap(array, i++, j);

		for (j = array.length - 1; j > i; i++, j--) {
			swap(array, i, j);
		}
		return array;
	}

	public static String printArray(int[] array) {
		String str = "";
		for (int tmp : array) {
			str += " " + tmp;
		}
		return str;
	}
	
	public static void insertionSort(int[] permute, int position){
		/*
		 * This code is based on the implementation shown in Java Foundations Second Edition
		 */
		position++;

			for(int l=position;l<permute.length;l++){
				int key = permute[l];
				int p = l;
				
				while(p > position &&permute[p-1] <= key){
					permute[p] = permute[p-1];
					p--;
				}
				
				permute[p] = key;
			}
	}
	
	public static void bubbleSort(int[] a, int position /*String direction*/)
	{
		int out, in;
		position++;

		for(out=a.length-1; out>position; out--)
			for(in=position; in<out; in++)
				if(/*a[in] > a[in+1] && direction.equalsIgnoreCase("A") || */a[in] < a[in+1]/* && direction.equalsIgnoreCase("D")*/)
				{
					int temp = a[in];
					a[in] = a[in+1];
					a[in+1] = temp;
				}
	}

	public static void swap(int[] array, int x, int y) {
		array[x] ^= array[y];
		array[y] ^= array[x];
		array[x] ^= array[y];
	}

}
