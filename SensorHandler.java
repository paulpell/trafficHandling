package trafficHandling;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import static trafficHandling.LightState.*;


/**
 * This class is the main class of the project. It holds all the lights,
 * handles the signals and tells to the lights when they have to change state.
 */
public class SensorHandler extends Thread {
	
	private ArrayList<TrafficLight> trafficLights;
	private ArrayList<Boolean> captors;
	private ArrayList<Long> lastTimes;
	private ArrayList<ReentrantLock> locks;
	private ArrayList<Condition> conditions;
	
	/** this lock is used to synchronize the lights:
	 *  when one light wants to change to green, it takes the lock.. So
	 *  it must wait until the lock is free (i.e. until the previously
	 *  green light has turned to red)
	 */
	private final ReentrantLock orangeLock = new ReentrantLock();

	private int nextGreen = 0;// needed to know what thread to await
	
	/**
	 * That constructor initializes the different lists that class maintains.
	 */
	// just initializes all the lists
	public SensorHandler() {
		trafficLights = new ArrayList<TrafficLight>();
		captors = new ArrayList<Boolean>();
		lastTimes = new ArrayList<Long>();
		locks = new ArrayList<ReentrantLock>();
		conditions = new ArrayList<Condition>();
	}
	
	/**
	 * The job of this function is really easy:
	 * it waits until an impulsion arrives (given by InputThread),
	 * then tells the currently green light it has to change to red and
	 * then, when all the lights are red, checks which one is the next to 
	 * become green.
	 */
	public void run() {
		while(true) {
			// wait a new signal
			while (!hasSignal()) {
				wait_signal(); // just don't want a synchronized run();
							   // it would cause trouble when waiting on a Condition,
							   // and calling an other synchronized function here
			} 

			int greenIndex = getGreenLightIndex();

			if (greenIndex == -1) { // no green yet, we wait it: it is needed
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
			orangeLock.lock();
			orangeLock.unlock();

			// so, now we can just look what light wants to be green,
			// with circular light handling
			int next = (greenIndex + 1) % getLightsCount();
			int nextnext = (next + 1) % getLightsCount();
			if (captors.get(next)) {
				handleSignal(next); // notifies the next green it wants to change
			}
			else {// else, we are sure the other is set (a green light sets nothing)
				handleSignal(nextnext);
			}
		}
	}
	
	/**
	 * Simple function that uses this class' monitor to wait a signal (when
	 * there is an impulsion).
	 */
	private synchronized void wait_signal() {
		try{
			wait();// wait a new signal
		} catch( InterruptedException e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * This function must be called when a new light is added to this handler.
	 * @param light the instance of TrafficLight to be added
	 */
	public void addLight(TrafficLight light) {
		trafficLights.add(light);
		captors.add(false);
		lastTimes.add(0L);
		locks.add(light.getLock());
		conditions.add(light.getCondition());
	}
	
	/**
	 * Returns the number of lights that are stored.
	 * @return the count of the lights
	 */
	public int getLightsCount() {
		return trafficLights.size();
	}
	
	// returns the index of the currently green light, or -1 if there's none
	private int getGreenLightIndex() {
		for(int i=0; i<getLightsCount(); i++)
			if (trafficLights.get(i).getLightState() == GREEN)
				return i;
		return -1;
	}
	
	/**
	 * This function is to be used when a light receives an impulsion.
	 * @param numeros the id of the light that received the impulsion
	 * @param state whether we set it to true or false (in this project, always true)
	 */
	public synchronized void setSignal(final int numeros, final boolean state) {
		captors.set(numeros, state);
		lastTimes.set(numeros, System.currentTimeMillis());
		if (numeros != getGreenLightIndex())
			notify(); // tells the Thread there is a new signal
	}
	
	/**
	 * This function is to be used when one wants to clear the signal of some light.
	 * @param numeros the id of the light to be resetted
	 */
	// useful, because setSignal sends a notification; this here not
	public synchronized void setFalseSignal(int numeros) {
		captors.set(numeros, false);
	}
	
	/**
	 * Returns the time at which the idth light received an impulsion
	 * @param id the id number of the interesting light
	 * @return the time given by System.currentTimeMillis when the impulsion arrived
	 */
	// FIXME do we change lastTimes into a list of times
	// given by Clock, or let it be with millis?????
	public long lastSignalTime(int id) {
		return lastTimes.get(id);
	}
	
	/**
	 * return whether any signal is set (whether any light got an impulsion)
	 * @return true iff at least one signal is set
	 */
	public boolean hasSignal() {
		for(boolean b: captors)
			if (b) return true;
		return false;
	}
	
	/**
	 * Returns whether the ith signal had an impulsion.
	 * @param i the index of the light we question
	 * @return true iff there was a not cleared impulsion on ith light
	 */
	public boolean getSignal(int i) {
		return captors.get(i);
	}
	
	/**
	 * Function that tells a light it must turn to green. (but only once
	 * all the lights are green)
	 * @param numeros the id of the light that should change to green
	 */
	// when arriving in this method, all the lights must be red
	public synchronized void handleSignal(int numeros) {
		assert getGreenLightIndex() == -1;
		setFalseSignal(numeros);
		nextGreen = numeros; // this is needed in run()
		// wake the red one
		locks.get(numeros).lock();
		conditions.get(numeros).signal();
		locks.get(numeros).unlock();
	
	}
	
	/**
	 * function to get the lock that ensures that all the lights are red.
	 * (every time a light turns to green, it locks it and it is released
	 * when it turns to red from orange)
	 */
	public ReentrantLock getOrangeLock(){
		return orangeLock;
	}
}
