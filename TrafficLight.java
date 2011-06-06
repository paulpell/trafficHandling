package trafficHandling;

import static trafficHandling.LightState.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

class TrafficLight extends Thread {

	private int id;
	private LightState state;
	private Clock clk;
	private SensorHandler signal;
	private double firstGreenTime;
	private ReentrantLock lock;
	private Condition condition;


	public TrafficLight(int id, SensorHandler signal, Clock clk) {
		this(id, signal, clk, RED);
	}

	public TrafficLight(int id, SensorHandler signal, Clock clk, LightState initialState) {
		this.id = id;
		this.signal = signal;
		this.clk = clk;
		this.state = initialState;
		this.lock = new ReentrantLock();
		this.condition = lock.newCondition();
	}

	public void run() {
	//public void run() {
		if(state == GREEN){// green initial state: we need to take the lock
			signal.getMegaLock().lock();
			System.out.println("took the lock: "+this);
		}
		while(true) {
			switch (state ) {
				case RED:
					System.out.println("Light "+id+" changed to red at t="+clk.getTime());
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
					signal.getMegaLock().lock(); // before we change state (else there are two green lights)
					changeState(GREEN);
					break;
					
				case GREEN:;
					System.out.println("Light " + id+" changes to green at t="+clk.getTime());
					firstGreenTime = System.currentTimeMillis();
					//firstGreenTime = clk.getTime();
					//System.out.println("firstGreenTime = "+clk.getTime());//firstGreenTime);
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
					
					// reset the time to now
					time = System.currentTimeMillis();
					// while the last impulsion arrived less than 2 secs before,
					// and we did not wait 12 secs now
					while(time - signal.lastSignalTime(this.id) < 2 && time < 12000) {
						if(time <= 10000) {
							try {
								long t = 2000 - (long)time + signal.lastSignalTime(this.id);
								//sleep((long)2000); // we must wait 2 seconds, as the 12 will not be exceeded
								sleep(t); // wait until 2 secs elapsed since the last signal
							} catch( InterruptedException e){
								e.printStackTrace();
							}
						}
						else {// else, 
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
					break;
				//}
			//}
			//else{ // orange wait 2 second go to orange
				//case( ORANGE ): {
				case ORANGE:
					//System.out.printf("Light %d changed to orange, t=%d\n", id, System.currentTimeMillis());
					System.out.println("Light " + id + " changed to orange at t="+clk.getTime());
					try{
						sleep(2000);
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					//System.out.println("sleeped 2 secs, t="+System.currentTimeMillis());
					changeState(RED);
					signal.getMegaLock().unlock();// after we are red, we let the others do
					break;
				//}
			}
		}
	}

	private void changeState(LightState newState) {
		//System.out.println("Light "+id+" changes state at t= "+ System.currentTimeMillis());
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
