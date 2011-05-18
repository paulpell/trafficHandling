import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class Detecteur {
	private Scanner in;
	private LinkedList<Feu> feux;
	
	private LinkedList<Boolean> captors;
	
	Detecteur(Feu feu) {
		in = new Scanner(System.in);
		feux = new LinkedList<Feu>();
		feux.add(feu);
		captors = new ArrayList<Boolean>();
	}
	
	public void run() {
		while(true) {
			int value = in.nextInt();
			if (checkInput(value)){
				System.out.println("t=" + 5 + " : capteur " + value + " active");
				captors.set(value, true);//put the corresponding capteur in the good list
			} else {
				System.out.println("Capteur invalide");
			}
		}
	}
	
	private boolean checkInput(int in) {
		return (in < feux.size());
	}
	
	public synchronized void addFeu(Feu feu) {
		feux.add(feu);
		captors.add(false);
	}
	
	public synchronized boolean hasSignal(){
		boolean ret = false;
		for (boolean i : captors){
			ret |= i;
		}
		return false;
	}
	
	public synchronized boolean getSignal(int numeros) {//HERE IS THE PRIORITY LIST
		//IF THE FIRST IS TRUE
		assert numeros < captors.size();
		for (int i = 0; i < numeros;i++) {
			if (captors.get(i)) {//something before the number want the signal 
				return false;
			}
		}
	//HELL NO NO NO THIS IS RIGHT THIS IS BIG we had nothing till the number
		return captors.get(numeros);
	}
	
	public synchronized void resetSignal(int numeros) {
		captors.set(numeros,false);
	}
}
