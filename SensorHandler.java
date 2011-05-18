package trafficHandling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class SensorHandler {
	private Scanner in;
	private LinkedList<TrafficLight> trafficLights;
	
	private ArrayList<Boolean> captors;
	
	public SensorHandler(TrafficLight light) {
		in = new Scanner(System.in);
		trafficLights = new LinkedList<TrafficLight>();
		trafficLights.add(light);
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
	
	private boolean checkInput(int i) {
		return i < this.trafficLights.size();
	}
	
	public void addFeu(TrafficLight feu) {
		trafficLights.add(feu);
		captors.add(false);
	}
	
	public synchronized boolean hasSignal() {
		for (boolean i : captors){
			if (i) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized boolean getSignal(int numeros) {//HERE IS THE PRIORITY LIST
		assert numeros < captors.size();
		for (int i = 0; i < numeros;i++) {
			if (captors.get(i)) {
				return false;
			}
		}
		return captors.get(numeros);
	}
	
	public synchronized void setSignal(int numeros, boolean state) {
		captors.set(numeros,state);
	}
}
