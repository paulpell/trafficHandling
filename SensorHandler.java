package trafficHandling;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import static trafficHandling.LightState.*;


public class SensorHandler extends Thread {
	private LinkedList<TrafficLight> trafficLights;
	
	private ArrayList<Boolean> captors;
	private ArrayList<Long> lastTimes;
	private ArrayList<ReentrantLock> locks;
	private ArrayList<Condition> conditions;
	
	private ReentrantLock megaLock = new ReentrantLock();
	
	public SensorHandler() {
		trafficLights = new LinkedList<TrafficLight>();
		captors = new ArrayList<Boolean>();
		lastTimes = new ArrayList<Long>();
		locks = new ArrayList<ReentrantLock>();
		conditions = new ArrayList<Condition>();
	}
	
	public synchronized void run() {
		while(true) {
			try{
				wait();// wait a new signal
			} catch( InterruptedException e) {
				e.printStackTrace();
			} 
			for( int i=0; i<trafficLights.size(); i++) {
				if(getSignal(i)) {
					handleSignal(i);
				}
			}
		}
	}
	
	
	
	public void addLight(TrafficLight feu) {
		trafficLights.add(feu);
		captors.add(false);
		lastTimes.add(0L);
		locks.add(feu.getLock());
		conditions.add(feu.getCondition());
	}
	
	public int getLightsCount() {
		return trafficLights.size();
	}
	
	public synchronized void setSignal(int numeros, boolean state) {
		captors.set(numeros, state);
		lastTimes.set(numeros, System.currentTimeMillis());
		notify();
	}
	
	public long lastSignalTime(int id) {
		return lastTimes.get(id);
	}
	
	public boolean getSignal(int i) {
		return captors.get(i);
	}
	
	public synchronized void handleSignal(int numeros) {
		if(trafficLights.get(numeros).getLightState() == RED) {
			System.out.println(trafficLights.get(numeros).getState());
			for ( int i=0; i< trafficLights.size(); i++ ) {
				if (trafficLights.get(i).getLightState() == GREEN){
					locks.get(i).lock();
					conditions.get(i).signal();
					locks.get(i).unlock();
				}
			}
			// wake the red one
			locks.get(numeros).lock();
			conditions.get(numeros).signal();
			locks.get(numeros).unlock();
		}
	}
	
	public ReentrantLock getMegaLock(){
		return megaLock;
	}
}
