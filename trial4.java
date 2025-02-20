package main;

import java.util.*;


public class trial4{
// same thing as trial 3 but with array lists instead of lists
	
	
	static ArrayList<LinkedList<Integer>> puzzle = new ArrayList<>();
	//(y,x) get over it number x column just looks better
	static boolean[][] columns;

	//for timer
	static double longest = 0.00;
	static int Lindex = 0;
	static double shortest = 0.00;
	static int sindex = 0;
	static ArrayList<Double> times = new ArrayList<>();
	static ArrayList<Double> stimes = new ArrayList<>();
	static ArrayList<Double> ftimes = new ArrayList<>();
	static boolean solved = false;
	
	//so you know just looking at data which gen you ran and what solver was used
	static boolean spuz;
	static boolean cpuz;
	static boolean puz;
	static boolean Sai;
	static boolean Spi;
	static boolean Sppi;
	
	//fun colors first time using these so wanted to give it a shot
	//highlights I do not like these at all
	static String greenHL = "\u001B[42m";
	static String redHL =  "\u001B[41m";
	static String yellowHL =  "\u001B[43m";
	//text colors
	static String green = "\u001B[32m";
	static String red =  "\u001B[31m";
	static String yellow =  "\u001B[33m";
	static String cyan = "\u001B[36m";
	//reset is the same for both HL and text
	static String reset = "\u001B[0m";
	
	public static void main(String[] args) {

		pn("-------------------");
		pn("Running simulation");
		long beg = System.nanoTime();
		for(int i = 0; i <1000; i++){

		pn("Running simulation: " + i);

		pn("-------------------");
		solved = false;
		cpuzzlegen(40,3);
		//input();
		//scramble();
			
		// it is crucial to note that the puzzle generators location means that we do
		// not count the generation as part of the solve time 
		long ST = System.nanoTime();
		
		solverai();


		long ET = System.nanoTime();
		double total= (ET-ST)/1000000000.0;
		if(total>longest) {
			longest = total;
			Lindex = i;
		}
		if(total<shortest||shortest==0) {
			shortest = total;
			sindex = i;
		}
		times.add(total);
		if(solved==true) {
			stimes.add(total);
		}else {
			ftimes.add(total);
		}
		
		pn("-------------------");

		// these are for if we are doing a loop you just swap them for the ones we 
		//using including the L indexes

		pn("Simutlation " + i +" took " + total + " seconds");
		pn("END OF SIMULATION " + i);
		
		//System.out.println("Simulation took " + total + " seconds");
		
		pn("END OF ALL SIMULATIONS");

		pn("-------------------");

		}
		long end = System.nanoTime();
		//goes outside of the loop
		
		//total time elapsed
		double tte= (end-beg)/1000000000.0;
		stats();
		pn("Best case took " + green +  shortest + reset + " seconds during sim: " + green + sindex + reset);
		pn("Worst case took " + red + longest + reset+ " seconds during sim: " + red + Lindex + reset);
		pn("Total time elapsed: " + yellow + tte + reset);
		pn(" ");
		pn("Number of successes: " + green + stimes.size() + reset);
		pn("Number of Failures: " + red + ftimes.size() + reset);
		pn("Number of total simulations: " + yellow + times.size() + reset);
	}

	//calculates statistically significant data
	public static void stats(){
		//this function does slow the program down a bit so if you want you can remove
		ArrayList<Double> stat= new ArrayList<>();
		ArrayList<Double> sstat= new ArrayList<>();
		ArrayList<Double> fstat= new ArrayList<>();
		
		ArrayList<Double> ci = new ArrayList<>();
		ArrayList<Double> sci = new ArrayList<>();
		ArrayList<Double> fci = new ArrayList<>();
		
		Double mean = 0.00;
		Double smean = 0.00;
		Double fmean = 0.00;
		
		Double std = 0.00;
		Double sstd = 0.00;
		Double fstd = 0.00;
		
		
		//this was my mothers suggestion, without her I would have totally neglected
		//looking at successful time data and fail time data
		for(int i = 0; i < times.size();i++) {
			mean = mean + times.get(i);
		}
		mean = mean/times.size();
		stat.add(mean);
		
		for(int i = 0; i < stimes.size();i++) {
			smean = smean + stimes.get(i);
		}
		smean = smean/stimes.size();
		sstat.add(smean);
		
		for(int i = 0; i < ftimes.size();i++) {
			fmean = fmean + ftimes.get(i);
		}
		fmean = fmean/ftimes.size();
		fstat.add(fmean);
		
		//stdev time
		for(int i = 0; i < times.size();i++) {
			std = times.get(i) - mean;
		}
		std =Math.abs(std/times.size());
		stat.add(std);
		
		for(int i = 0; i < stimes.size();i++) {
			sstd = stimes.get(i) - smean;
		}
		sstd =Math.abs(sstd/stimes.size());
		sstat.add(sstd);
		
		for(int i = 0; i < ftimes.size();i++) {
			fstd = ftimes.get(i) - fmean;
		}
		
		fstd =Math.abs(fstd/ftimes.size());
		fstat.add(fstd);
		
		
		//hypothesis testing
		if(times.size()>=30) {
			//z score test
			ci.add(mean-(1.645*(std/(Math.pow(times.size(),0.5)))));
			ci.add(mean+(1.645*(std/(Math.pow(times.size(),0.5)))));
		}else {
			//t test
			Double tvalue = tval(times.size());
			ci.add(mean-(tvalue*(std/(Math.pow(times.size(),0.5)))));
			ci.add(mean+(tvalue*(std/(Math.pow(times.size(),0.5)))));
		}
		
		if(stimes.size()>=30) {
			//z score test
			sci.add(smean-(1.645*(sstd/Math.pow(stimes.size(),0.5))));
			sci.add(smean+(1.645*(sstd/Math.pow(stimes.size(),0.5))));
		}else {
			//t test
			Double tvalue = tval(stimes.size());
			sci.add(smean-(tvalue*(sstd/Math.pow(stimes.size(),0.5))));
			sci.add(smean+(tvalue*(sstd/Math.pow(stimes.size(),0.5))));
		}
		
		if(ftimes.size()>=30) {
			//z score test
			fci.add(fmean-(1.645*(fstd/Math.pow(stimes.size(),0.5))));
			fci.add(fmean+(1.645*(fstd/Math.pow(stimes.size(),0.5))));
		}else if (ftimes.size()>0){
			//t test
			Double tvalue = tval(ftimes.size());
			fci.add(fmean-(tvalue*(fstd/Math.pow(stimes.size(),0.5))));
			fci.add(fmean+(tvalue*(fstd/Math.pow(stimes.size(),0.5))));
		}
		pl("Stats for");
		if(spuz) {
			pl(cyan + "Solveable puzzle generator" + reset);
		}else if(cpuz){	
			pl(cyan +"Cody's puzzle generator" + reset);
		}else if(puz) {
			pl(cyan +"puzzle generator" + reset);
		}
		pl("using");
		if(Sai) {
			pl(cyan + "Arbitrary Insert Solver" + reset);
		}else if(Spi) {
			pl(cyan + "Precision Insert" + reset);
		}else if(Sppi){
			pl(cyan + "Pre-parsed Precision Insert" + reset);
		}
		pn("");
		pn("");
		pn("mean and standard dev for all: " + stat);
		pn("mean and standard dev for all successes: " + sstat);
		pn("mean and standard dev for all failures: " + fstat);
		pn(" ");
		pn("CI 95% lower and upper bounds for all: " + ci);
		pn("CI 95% lower and upper bounds for all successes: " + sci);
		pn("CI 95% lower and upper bounds for all failures: " + fci);
		pn("");
		if(sci.isEmpty()){
			//using highlight because something must be wrong
			pn(redHL + "No DATA" + reset);
		}else if(stat.get(0)>ci.get(0)&&stat.get(0)<ci.get(1) && ci.get(0)!=null) {
			pn("The mean for combined tests "+ green + "IS " + reset + "statistically significant");
		}else {
			pn("The mean for combined tests " + red + "IS NOT " + reset + "statistically significant");
		}
		if(sci.isEmpty()){
			//using highlight because something must be wrong
			pn(redHL + "No Successes" + reset);
		}else if(sstat.get(0)>sci.get(0)&&sstat.get(0)<sci.get(1)) {
			pn("The mean for successful tests " + green + "IS " + reset + "statistically significant");
		}else {
			pn("The mean for successful tests " + red + "IS NOT " + reset + "statistically significant");
		}
		if(fci.isEmpty()){
			pn(yellow + "No Failures" + reset);
		}else if(fstat.get(0)>fci.get(0)&&fstat.get(0)<fci.get(1)) {
			pn("The mean for failed tests " + green + "IS " + reset + "statistically significant");
		}else {
			pn("The mean for failed tests  "+ red + "IS NOT " + reset + "statistically significant");
		}
		pn("-------------------");
	}
	
	public static double tval(int n) {
		double tvalue;
		switch (n-1) {
		  case 1:
		    tvalue = 6.314;
		    break;
		  case 2:
		    tvalue = 2.920;
		    break;
		  case 3:
		    tvalue = 2.353;
		    break;
		  case 4:
		    tvalue = 2.132;
		    break;
		  case 5:
		    tvalue = 2.015;
		    break;
		  case 6:
		    tvalue = 1.943;
		    break;
		  case 7:
		    tvalue = 1.881;
		    break;
		  case 8:
		    tvalue = 1.812;
		    break;
		  case 9:
		    tvalue = 1.746;
		    break;
		  case 10:
		    tvalue = 1.684;
		    break;
		  case 11:
		    tvalue = 1.645;
		    break;
		  case 12:
		    tvalue = 1.610;
		    break;
		  case 13:
		    tvalue = 1.579;
		    break;
		  case 14:
		    tvalue = 1.555;
		    break;
		  case 15:
		    tvalue = 1.534;
		    break;
		  case 16:
		    tvalue = 1.514;
		    break;
		  case 17:
		    tvalue = 1.496;
		    break;
		  case 18:
		    tvalue = 1.482;
		    break;
		  case 19:
		    tvalue = 1.470;
		    break;
		  case 20:
		    tvalue = 1.458;
		    break;
		  case 21:
		    tvalue = 1.446;
		    break;
		  case 22:
		    tvalue = 1.435;
		    break;
		  case 23:
		    tvalue = 1.425;
		    break;
		  case 24:
		    tvalue = 1.415;
		    break;
		  case 25:
		    tvalue = 1.405;
		    break;
		  case 26:
		    tvalue = 1.397;
		    break;
		  case 27:
		    tvalue = 1.389;
		    break;
		  case 28:
		    tvalue = 1.381;
		    break;
		  case 29:
		    tvalue = 1.373;
		    break;
		  default:
		    // For large degrees of freedom, the t-value approaches 1.96
		    tvalue = 1.96;
		}
		return tvalue;
	}
	//does a rotation right of the piece much better than last iteration
	public static LinkedList<Integer> r(int index, int num){
		LinkedList<Integer> piece = puzzle.get(index);
		for(int i = 0; i < num;i++) {
			piece.addFirst(piece.getLast());
			piece.remove(piece.size()-1);
		}
		return piece;
	}
	
	// same thing with specified piece
	public static LinkedList<Integer> r(LinkedList<Integer> piece, int num){
		for(int i = 0; i < num;i++) {
			piece.addFirst(piece.getLast());
			piece.remove(piece.size()-1);
		}
		return piece;
	}
	
	//does a rotation left
	public static LinkedList<Integer> l(int index, int num){
		LinkedList<Integer> piece = puzzle.get(index);
		for(int i = 0; i < num;i++) {
			piece.add(piece.get(0));
			piece.remove(0);
		}	
		return piece;
	}

	// same thing with specified piece
		public static LinkedList<Integer> l(LinkedList<Integer> piece, int num){
			for(int i = 0; i < num;i++) {
				piece.add(piece.get(0));
				piece.remove(0);
			}
			return piece;
		}
	//creates a solvable puzzle does such by clearing the current puzzle
	//then we use a randomizer to generate proper puzzles can do this of all sizes
	//Is a brilliant change to the old version and we got to reuse the code (modified)!
	public static void spuzzlegen(int pieces,int col){
		spuz = true;
		puzzle.clear();
		columns = new boolean[pieces][col];
		int counter = 0;
		int test;
		Random random = new Random();
		int rand = random.nextInt(10)+20;

		for(int i = 0; i < pieces; i++) {
			for(int c  = 0 ; c < col; c++) {
				counter++;
				test = (int) (1 + Math.floor(counter*rand*17*Math.E)%pieces);
				while(columns[test-1][c] == true) {
					counter++;
					test = (int) (1 + Math.floor(counter*17*Math.E*rand)%pieces);	
				}
				try {
					puzzle.get(i).add(test);
				}catch(Exception e) {
					LinkedList<Integer> dummy = new LinkedList<Integer>();
					dummy.add(test);
					puzzle.add(dummy);
				}
				columns[test-1][c] = true; 
			}
		}
		columns = new boolean[pieces][col];
		ppuzzle();
	}

	//creates a puzzle we do not know if it is solvable figuring out how to detect
	//if it is solvable by pigeon holing will be fascinating!
	public static void puzzlegen(int pieces,int col){
		puz = true;
		puzzle.clear();
		columns = new boolean[pieces][col];
		Random random = new Random();
		int rand = random.nextInt(10)+20;
		int counter = 0;
		int test;
		for(int i = 0; i < pieces; i++) {
			for(int c  = 0 ; c < col; c++) {
				counter++;
				test = (int) (1 + Math.floor(counter*17*rand*Math.E)%pieces);
				try {
					puzzle.get(i).add(test);
				}catch(Exception e) {
					LinkedList<Integer> dummy = new LinkedList<Integer>();
					dummy.add(test);
					puzzle.add(dummy);
				} 
			}
		}
		ppuzzle();
	}

	
	public static void cpuzzlegen(int pieces,int col){
		cpuz = true;
		puzzle.clear();
		columns = new boolean[pieces][col];
		Random random = new Random();
		int rand = random.nextInt(10)+20;
		int[] tracker = new int[pieces];
		for (int i = 0; i<pieces;i++) {
			tracker[i] = col;
		}
		int counter = 0;
		int test;
		for(int i = 0; i < pieces; i++) {
			for(int c  = 0 ; c < col; c++) {
				counter++;
				test = (int) (1 + Math.floor(counter*17*rand*Math.E)%pieces);
				while(tracker[test-1] == 0) {
					counter++;
					test = (int) (1 + Math.floor(counter*17*rand*Math.E)%pieces);	
				}
				try {
					puzzle.get(i).add(test);
				}catch(Exception e) {
					LinkedList<Integer> dummy = new LinkedList<Integer>();
					dummy.add(test);
					puzzle.add(dummy);
				}
				tracker[test-1]--; 
			}
		}
		ppuzzle();
	}
	
	public static void scramble() {
		pn("-------------------");
		pn("Scrambling");
		Random random = new Random();
		int rand = random.nextInt(10)+20;
		int counter = -1;
		for (int i = 0 ; i <puzzle.size();i++) {
			counter++;
			r(puzzle.get(i), (int) (Math.floor(counter*rand*17*Math.E)%puzzle.size()));
			if(i%2==0) {
				r(puzzle.get(i), (int) (Math.floor(counter*rand*17*Math.E)%puzzle.size()));
			}
		}
		ppuzzle();
	}
	

	//ez print method for the puzzle
	public static void ppuzzle() {
		for(int i = 0; i < puzzle.size(); i++) {
			pn("Line " +(i+1) + ": " + puzzle.get(i));
		}
	}
	
	//prints puzzle in a certain state
	public static void ppuzzle(ArrayList<LinkedList<Integer>> state) {
		for(int i = 0; i < state.size(); i++) {
			pn("Line " +(i+1) + ": " + puzzle.get(i));
		}
	}
	//prints with obstacle
	public static void ppuzzle(ArrayList<LinkedList<Integer>> state,LinkedList<Integer> obstacle) {
		for(int i = 0; i < state.size(); i++) {
			pn("Line " + (i+1) + ": " + puzzle.get(i));
		}
		pn("Obstacle: " + red + obstacle + reset);
	}
	
	//function that finds the rotation that works
	public static LinkedList<Integer> ftr(LinkedList<Integer> piece) {
		boolean works = false;
		while(works==false) {
			works = true;
			int holder = -1;
			for(int c = 0; c < piece.size();c++) {
				holder = piece.get(c);
				if(columns[holder-1][c]) {
					works=false;
					break;
				}
			}
			if(works==true) {
				atc(piece);
				return piece;
			}
			// we generally want to consider our first position so this goes last
			r(piece,1);
		}
		//this should never happen, so that I know there is an issue this system exit is here
		System.exit(0);
		return null;
	}

	//function that just checks the columns
	public static boolean ctc(LinkedList<Integer> piece) {
		for(int i = 0; i < piece.size();i++) {
			int holder = piece.get(i);
			if(columns[holder-1][i]) {
				return false;
			}else {
				continue;
			}		
		}
		return true;
	}
	
    //function that documents the existence of the piece in columns
	public static void atc(LinkedList<Integer> piece){
		for(int i = 0; i < piece.size();i++) {
			int holder = piece.get(i);
			columns[holder-1][i] = true;
		}
	}
	
	//function that removes the documentation of the piece in columns
	public static void rfc(LinkedList<Integer> piece){
		for(int i = 0; i < piece.size();i++) {
			int holder = piece.get(i);
			columns[holder-1][i] = false;
		}
	}
	
	//solver for all puzzles! arbitrary insert
	public static void solverai(){
		//setup
		Sai = true;
		ArrayList<LinkedList<Integer>> solved = new ArrayList<>();
		Stack<Integer>[] recurse = new Stack[2]; 
		recurse[0] = new Stack<Integer>();
		recurse[1] = new Stack<Integer>();
		solved.add(puzzle.get(0));
		atc(puzzle.get(0));
		int farthest = 0;
		
		for(int i = 1; i<puzzle.size(); i++) {
			//for testing and so i know progress is being made and we are not just stuck
			if(farthest < i) {
				farthest = i;
				//pn(farthest);
			}
			
			int ways = -1;
			LinkedList<Integer> test = puzzle.get(i);
			//counts number of ways  it works
			for(int c = 0; c < test.size(); c++) {
				if(ctc(test)) {
					ways++;
				}
				r(test,1);
			}
			// if we have 0 ways then the piece is bad and we need to refer to the stack
			if(ways==-1) {
				//if the stack is empty our puzzle can't be solved we have exhausted
				//all options
				if(recurse[0].isEmpty()) {
					pn("-------------------");
					pn(red + "PUZZLE CANNOT BE SOLVED" + reset);
					ppuzzle(solved,test);
					//for binomial dist
					//System.exit(0);
					return;
				}
				int index = recurse[0].pop();
				int lives = recurse[1].pop();
				lives--;
				i--;
				while(i>=index){
					rfc(solved.get(i));
					solved.remove(i);
					i--;
				}
				i++;
				test = puzzle.get(index);
				//we don't want to consider its current position
				r(test, 1);
				ftr(test);
				solved.add(test);
				if(lives>0) {
					recurse[0].push(index);
					recurse[1].push(lives);
				}
			}else if(ways>0){
				//multiple ways to solve
				ftr(test);
				solved.add(test);
				recurse[0].push(i);
				recurse[1].push(ways);
			}else if(ways==0) {
				//only one way to solve
				ftr(test);
				solved.add(test);
			}
		}
		pn("-------------------");
		pn(green + "PUZZLE HAS BEEN SOLVED!" + reset);
		ppuzzle(solved);
		trial4.solved = true;
		//for negative binomial
		//System.exit(0);
		
		return;
	}
	
	//precision insert 
	public static void solverpi(){
		//setup
		Spi = true;
		ArrayList<LinkedList<Integer>> solved = new ArrayList<>();
		boolean added = true;
		Stack<Integer>[] recurse = new Stack[2]; 
		solved.add(puzzle.get(0));
		
	}
	
	//pre parsed precision insert
	public static void solverppi(){
		//setup
		Sppi = true;
		ArrayList<LinkedList<Integer>> solved = new ArrayList<>();
		boolean added = true;
		Stack<Integer>[] recurse = new Stack[2]; 
		solved.add(puzzle.get(0));
		
	}
	
	public static void input() {
		int rows;
		int col;
		int input;
		puzzle.clear();
		
		Scanner in = new Scanner (System.in);
		pl("How many columns");
		col = in.nextInt();
		pl("How many rows");
		rows = in.nextInt();
		columns = new boolean[rows][col];
		for(int i = 0; i < rows; i++) {
			pl("please write line " + (i+1) + " one by one");
			for(int c = 0; c < col; c++) {
				input = in.nextInt();
				try {
					puzzle.get(i).add(input);
				}catch(Exception e) {
					LinkedList<Integer> dummy = new LinkedList<Integer>();
					dummy.add(input);
					puzzle.add(dummy);
				} 
			}
		}
	}
	
	//commands for printing my first CS professor showed us print line and print new line
	public static <E> void pl(E item){
		System.out.print(item + " ");
	}	

	public static <E> void pn(E item){
		System.out.println(item);
	}
}