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

	private int nextGreen = 0;// needed to know what thread to await
	
	public SensorHandler() {
		trafficLights = new ArrayList<TrafficLight>();
		captors = new ArrayList<Boolean>();
		lastTimes = new ArrayList<Long>();
		locks = new ArrayList<ReentrantLock>();
		conditions = new ArrayList<Condition>();
	}
	
	public void run() {
		while(true) {
			// wait a new signal
			while (!hasSignal()) {
				wait_signal(); // just don't want a synchronized run();
							   // it would cause trouble when waiting on a Condition
			} 

			int greenIndex = getGreenLightIndex();

			if (greenIndex == -1) { // no green yet, we wait it
				// nextGreen is set in handleSignal()
				locks.get(nextGreen).lock();
				try {
					conditions.get(nextGreen).await();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				}
				locks.get(nextGreen).unlock();
				greenIndex = getGreenLightIndex();
			}
			// tell the green light it must go red
			locks.get(greenIndex).lock();
			conditions.get(greenIndex).signal();
			locks.get(greenIndex).unlock();


			// after the locking, we are sure all the lights are red
			megaLock.lock();
			megaLock.unlock();

			// so, now we can just look what light wants to be green,
			// with circular light handling
			int next = (greenIndex + 1) % getLightsCount();
			int nextnext = (next + 1) % getLightsCount();
			if (captors.get(next)) {
				handleSignal(next); // notifies both the green and the next green they want to change
			}
			else {
				handleSignal(nextnext);
			}
		}
	}
	
	public synchronized void wait_signal() {
		try{
			wait();// wait a new signal
		} catch( InterruptedException e) {
			e.printStackTrace();
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
	
	private int getGreenLightIndex() {
		for(int i=0; i<getLightsCount(); i++)
			if (trafficLights.get(i).getLightState() == GREEN)
				return i;
		return -1;
	}
	
	public synchronized void setSignal(final int numeros, final boolean state) {
		captors.set(numeros, state);
		lastTimes.set(numeros, System.currentTimeMillis());
		if (numeros != getGreenLightIndex())
			notify(); // tells the Thread there is a new signal
	}
	
	// useful, because setSignal sends a notification; this here not
	public synchronized void setFalseSignal(int numeros) {
		captors.set(numeros, false);
	}
	
	// FIXME do we change lastTimes into a list of times
	// given by Clock, or let it be with millis?????
	public long lastSignalTime(int id) {
		return lastTimes.get(id);
	}
	
	public boolean hasSignal() {
		for(boolean b: captors)
			if (b) return true;
		return false;
	}
	
	public boolean getSignal(int i) {
		return captors.get(i);
	}
	
	// when arriving in this method, all the lights must be red
	public synchronized void handleSignal(int numeros) {
		assert getGreenLightIndex() == -1;

		// if the numerosth light is green, it will look for itself
		if(trafficLights.get(numeros).getLightState() == RED) {
			setFalseSignal(numeros);
			nextGreen = numeros;
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
