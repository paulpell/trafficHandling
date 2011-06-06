package trafficHandling;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import static trafficHandling.LightState.*;


public class SensorHandler extends Thread {
	
	private ArrayList<TrafficLight> trafficLights;
	private ArrayList<Boolean> captors;
	private ArrayList<Long> lastTimes;
	private ArrayList<ReentrantLock> locks;
	private ArrayList<Condition> conditions;
	
	// this lock is used to synchronize the lights:
	// when one light wants to change to green, it takes the lock.. So
	// it must wait until the lock is free (i.e. until the previously
	// green light has turned to red)
	private final ReentrantLock megaLock = new ReentrantLock();
	//megaLock: just a fine name
	
	public SensorHandler() {
		trafficLights = new ArrayList<TrafficLight>();
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
		notify(); // tells the Thread there is a new signal
	}
	
	// FIXME do we change lastTimes into a list of times
	// given by Clock, or let it be with millis?????
	public long lastSignalTime(int id) {
		return lastTimes.get(id);
	}
	
	public boolean getSignal(int i) {
		return captors.get(i);
	}
	
	public synchronized void handleSignal(int numeros) {
		// if the numerosth light is green, it will look for itself
		if(trafficLights.get(numeros).getLightState() == RED) {
			// we need to tell the green light it has to turn red
			// so we check all the lights to find it
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
