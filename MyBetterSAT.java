import java.util.ArrayList;
import java.util.Scanner;

public class MyBetterSAT {

	public static void main(String[] args) {
		ReadInput io = new ReadInput();
		
		io.popData();
		
		SatCase myCase = io.getData();
		
		System.out.println("");
		System.out.println(myCase.solveSat(myCase.clauseList));
		
		//System.out.println("Answer on above line.");

	}

}

class ReadInput {
	private Scanner in;
	
	//private String filename = "input.txt";
	private String inputLine;
	private int varNum;
	private int claNum;
	private int combNum;
	private byte[] lits;
	
	private ArrayList<Clause> clauseList;
	private SatCase satCase;
	
	public void popData() {
		// Might need a try/catch block here
		in = new Scanner(System.in);
		
		
		
		//this while is sloppy
		while (true) {
			inputLine = in.nextLine().trim();
			String[] attribs = inputLine.split("\\s+");
			
			if (attribs[0].matches("(?i)(c)")) {
				continue;
			} else if (attribs[0].matches("(?i)(p)")) {
				varNum = Integer.parseInt(attribs[2]);
				
				//System.out.println(varNum);
				
				//combNum = (int) Math.pow(2, varNum) - 0;
				lits = new byte[varNum];
				for (int x = 0; x < lits.length; x++){
					lits[x] = 0;
				}
				
				claNum = Integer.parseInt(attribs[3]);
				clauseList = new ArrayList<Clause>(claNum);
			} else {
				int[] ors = new int[attribs.length - 1];
				for (int x = 0; x < attribs.length - 1; x++) {
					ors[x] = Integer.parseInt(attribs[x]);
				}
				Clause clause = new Clause(ors);
				clauseList.add(clause);
				
				for (int x = 0; x < claNum - 1; x++) {
					inputLine = in.nextLine().trim();
					attribs = inputLine.split("\\s+");
					ors = new int[attribs.length - 1];
					for (int y = 0; y < attribs.length - 1; y++) {
						ors[y] = Integer.parseInt(attribs[y]);
					}
					clause = new Clause(ors);
					clauseList.add(clause);
				}
				break;
			}
			
		}
		
		satCase = new SatCase(varNum, lits, claNum, clauseList);
		
		
	}
	
	public SatCase getData() {
		return satCase;
	}
	
}

class Clause {
	public int[] ors;
	
	public int[] getOrs() {
		return ors;
	}

	public void setOrs(int[] ors) {
		this.ors = ors;
	}

	Clause (int[] ors) {
		this.ors = ors;
	}
	
	public boolean testClause(int x) {
		//boolean flag = false;
		
		//for (int i : ors) {
			/*if (i < 0) {
				if (MyUtils.getBit(x, (Math.abs(i) -1)) == 0) {
					return true;
				} 
			} else {
				if (MyUtils.getBit(x, (i - 1)) == 1) {
					return true;
				}
			}*/
		//}
		
		
		return false;
	}
	
	public boolean checkEmpty() {
		for (int x : ors) {
			if (x != 0) {
				return false;
			}
		}
		
		return true;
	}
	
	public int checkUnit() {
		int counter = 0;
		int unitLit = 0;
		for (int x : ors) {
			if (x != 0) {
				counter++;
				unitLit = x;
			}
		}
		
		if (counter == 1) {
			return unitLit;
		}
		
		return 0;
	}
	
	public Clause makeCopy() {
		int[] tempOrs = ors.clone();
		Clause temp = new Clause(tempOrs);
		
		return temp;
		
	}
	
	
	
	// Maybe Byte buffer
	
}

class SatCase {
	private int varNum;
	//private int combNum;
	private byte[] lits;
	private int claNum;
	public ArrayList<Clause> clauseList;
	//private ArrayList<Integer> unitLits;
	
	SatCase (int varNum, byte[] lits, int claNum, ArrayList<Clause> clauseList) {
		this.varNum = varNum;
		this.lits = lits;
		this.claNum = claNum;
		this.clauseList = clauseList;
	}
	
	public boolean solveSat(ArrayList<Clause> list) {
		ArrayList<Clause> tempList = new ArrayList<Clause>(0);
		for (int x = 0; x < list.size(); x++) {
			tempList.add(list.get(x).makeCopy());
		}
		
		//byte[] tempLits = lits.clone();
		
		
		// If no clauses, return true
		if (tempList.isEmpty()) {
			return true;
		}
		
		// If any empty clauses, return false
		for (Clause i : tempList) {
			if (i.checkEmpty()) {
				return false;
			}
		}
		
		// Propagate any unit clauses
		ArrayList<Integer> unitLits = new ArrayList<Integer>(0);
		
		for (Clause i : tempList) {
			int checkUnit = i.checkUnit();
			if (checkUnit != 0) {
				unitLits.add(checkUnit);
			}
		}
		
		if (!unitLits.isEmpty()) {
			unitProp(unitLits, tempList);
		}
		
		// Pure Literal Assignments
		byte[] pureLits = new byte[varNum + 1];
		for (int x = 0; x < pureLits.length; x++) {
			pureLits[x] = 0;
		}
		
		// Finding the Pures. 1s and 2s are pure odds and evens.
		for (Clause i : tempList) {
			for (int x : i.getOrs()) {
				if (x != 0) {
					if (pureLits[Math.abs(x)] == 0) {
						if (x > 0) {
							pureLits[Math.abs(x)] = 1;
						} else {
							pureLits[Math.abs(x)] = 2;
						}
					} else if (pureLits[Math.abs(x)] == 1) {
						if (x < 0) {
							pureLits[Math.abs(x)] = 3;
						}
					} else if (pureLits[Math.abs(x)] == 2) {
						if (x > 0) {
							pureLits[Math.abs(x)] = 3;
						}
					}
				}
			}
		}
		
		// Removing clauses with pures
		for (int i = tempList.size() - 1; i > -1; i--) {
			for (int j = 0; j < tempList.get(i).ors.length; j++) {
				if ((pureLits[Math.abs(tempList.get(i).ors[j])] == 1) || 
						(pureLits[Math.abs(tempList.get(i).ors[j])] == 2)) {
					tempList.remove(i);
					break;
				}
			}
		}
		
		// Need to find the minimum variable still not assigned.
		int minVar = Integer.MAX_VALUE;
		for (Clause i : tempList) {
			for (int x : i.getOrs()) {
				if ((minVar > Math.abs(x)) && x != 0) {
					minVar = Math.abs(x);
				}
			}
		}
		
		
		//System.out.println(minVar);
		
		// Assigning the min variable to true and false to copies of the clause list
		// Start by making two copies of the original.
		ArrayList<Clause> tList = new ArrayList<Clause>(0);
		for (int x = 0; x < tempList.size(); x++) {
			tList.add(tempList.get(x).makeCopy());
		}
		
		ArrayList<Clause> fList = new ArrayList<Clause>(0);
		for (int x = 0; x < tempList.size(); x++) {
			fList.add(tempList.get(x).makeCopy());
		}
		
		// Now set the var to true and remove clauses and variables as needed in tList
		for (int i = tList.size() - 1; i > -1; i--) {
			for (int j = 0; j < tList.get(i).ors.length; j++) {
				if (tList.get(i).ors[j] == (minVar * -1)) {
					tList.get(i).ors[j] = 0;
				}
				
				if (tList.get(i).ors[j] == minVar) {
					tList.remove(i);
					break;
				}
			}
			
		}
		
		// Now set the var to false and remove clauses and variables as needed in fList
		for (int i = fList.size() - 1; i > -1; i--) {
			for (int j = 0; j < fList.get(i).ors.length; j++) {
				if (fList.get(i).ors[j] == minVar) {
					fList.get(i).ors[j] = 0;
				}
				
				if (fList.get(i).ors[j] == (minVar * -1)) {
					fList.remove(i);
					break;
				}
			}
			
		}
		
		
		if (solveSat(tList)) {
			return true;
		} else {
			return (solveSat(fList));
		}
		
	}
	
	private void unitProp(ArrayList<Integer> unitLits, ArrayList<Clause> tempList) {
		for (Integer x : unitLits) {
			for (int i = tempList.size() - 1; i > -1; i--) {
				for (int j = 0; j < tempList.get(i).ors.length; j++) {
					if (tempList.get(i).ors[j] == (x * -1)) {
						tempList.get(i).ors[j] = 0;
					}
					
					if (tempList.get(i).ors[j] == x) {
						tempList.remove(i);
						break;
					}
				}
			}
		}
	}
	
}
