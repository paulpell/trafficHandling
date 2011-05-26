package trafficHandling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class SensorHandler extends Thread {
	private Scanner in;
	private LinkedList<TrafficLight> trafficLights;
	
	private ArrayList<Boolean> captors;
	private ArrayList<Long> lastTimes;
	
	public SensorHandler() {
		in = new Scanner(System.in);
		trafficLights = new LinkedList<TrafficLight>();
		captors = new ArrayList<Boolean>();
		lastTimes = new ArrayList<Long>(); // to be discussed
	}
	
	public SensorHandler(TrafficLight light) {
		in = new Scanner(System.in);
		trafficLights = new LinkedList<TrafficLight>();
		trafficLights.add(light);
		captors = new ArrayList<Boolean>();
		lastTimes = new ArrayList<Long>(); // to be discussed
	}
	
	public void run() {
		while(true) {
			int value = in.nextInt();
			if (checkInput(value)){
				System.out.println("t=" + 5 + " : capteur " + value + " active");
				captors.set(value, true);//put the corresponding capteur in the good list
				lastTimes.set(value, System.currentTimeMillis());
			} else {
				System.out.println("Capteur invalide");
			}
		}
	}
	
	private boolean checkInput(int i) {
		return i < this.trafficLights.size();
	}
	
	public void addLight(TrafficLight feu) {
		trafficLights.add(feu);
		captors.add(false);
		lastTimes.add(0L);
	}
	
	public synchronized boolean hasSignal() {
		for (boolean i : captors){
			if (i) {
				return true;
			}
		}
		return false;
	}
	
	// WE HAVE TO INSERT THE LIGHTS IN PRIORITY ORDER!!! (ok in Croisement)
	public synchronized boolean getSignal(int numeros) {//HERE IS THE PRIORITY LIST
		assert numeros < captors.size();
		for (int i = 0; i < numeros;i++) {
			if (captors.get(i)) {
				return false;
			}
		}
		return captors.get(numeros);
	}

	public long lastSignalTime(int id) {
		return lastTimes.get(id);
	}
	
	public synchronized void setSignal(int numeros, boolean state) {
//		trafficLights.get(numeros).getState() == WAITING; //THIS IS IMPORTANT TODO
		captors.set(numeros,state);
		lastTimes.set(numeros, System.currentTimeMillis());
		TrafficLight concerned = captors.get(numeros);
		if(concerned == greenLight) {
			captors.get(numeros).notify();
		}
	}
}
