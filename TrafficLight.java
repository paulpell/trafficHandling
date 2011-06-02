package trafficHandling;

import static trafficHandling.LightState.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class TrafficLight extends Thread {

	private int id;
	private LightState state;
	private SensorHandler signal;
	private double firstGreenTime;
	private ReentrantLock lock;
	private Condition condition;


	public TrafficLight(int id, SensorHandler signal) {
		this(id, signal, RED);
	}

	public TrafficLight(int id, SensorHandler signal, LightState initialState) {
		this.id = id;
		this.signal = signal;
		this.state = initialState;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}

	public void run() {
	//public void run() {
		while(true) {
			switch (state ) {
				case (RED) {
			//if(state == RED) { 
					System.out.println("Signal "+id+" changed to red ");
					try {
						lock.lock();
						condition.await();
						lock.unlock();
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					catch (IllegalMonitorStateException ie) {
						ie.printStackTrace();
					}
					changeState(GREEN);
				}
			//}
			//else if(state == GREEN /*&& !signal.getSignal(this.id) */) {
				case (GREEN) {
					signal.getMegaLock().lock();
					System.out.println("Signal " + id+" changes to green");
					firstGreenTime = System.currentTimeMillis();
					// wait until some other light gets a signal (given by notify)
					try {
						lock.lock();
						condition.await();
						lock.unlock();
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					catch (IllegalMonitorStateException ie) {
						ie.printStackTrace();
					}
					
					double time = System.currentTimeMillis() - firstGreenTime;
					if(time < 6000) { // wait the initial 6 seconds
						try {
							sleep((long)(6000 - time));
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}

					// here, time values (approximately) 6000
					time = System.currentTimeMillis();
					// while the last impulsion arrived less than 2 secs before,
					// and we did not wait 12 secs now
					while(time - signal.lastSignalTime(this.id) < 2 && time < 12000) {
						if(time <= 10000) {
							try {
								sleep((long)2000); // we must wait 2 seconds, as the 12 will not be exceeded
							} catch( InterruptedException e){
								e.printStackTrace();
							}
						}
						else {
							try {
								sleep((long)(12000 - time));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// we need the current time for the while test
						time = System.currentTimeMillis() - firstGreenTime;
					}
					changeState(ORANGE);
				}
			//}
			//else{ // orange wait 2 second go to orange
				case( ORANGE ) {
					System.out.printf("Light %d changed to orange\n", id);
					try{
						sleep(2);
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					changeState(RED);
					signal.getMegaLock().unlock();
				}
			}
		}
	}

	private void changeState(LightState newState) {
		System.out.println("Light "+id+" changes state at t= "+ System.currentTimeMillis());
		state = newState;
	}
	
	public LightState getLightState() {
		return this.state;
	}
	
	public ReentrantLock getLock() {
		return lock;
	}
	
	public Condition getCondition() {
		return condition;
	}
}
