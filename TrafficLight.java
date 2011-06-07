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
		if(state == GREEN){// green initial state: we need to take the lock
			signal.getMegaLock().lock();
		}
		while(true) {
			switch (state) {
				case RED:
					System.out.println("t="+clk.getTime()+": Light "+id+" changed to red");

					// do nothing while noone wants to become green
					try {
						lock.lock();
						condition.await();// SensorHandler will look for our sleep
						lock.unlock();
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					catch (IllegalMonitorStateException ie) {
						ie.printStackTrace();
					}
					signal.getMegaLock().lock(); // make sure all the lights are red
					signal.setFalseSignal(this.id);// ok, we saw you wanted to change
					changeState(GREEN);
					break;
					
				case GREEN:
					System.out.println("t="+clk.getTime()+": Light "+id+" changed to green");
					firstGreenTime = System.currentTimeMillis();
					
					lock.lock();
					condition.signal(); // first, tell SensorHandler we are green
					lock.unlock();

					// wait until some other light gets a signal (called in handleSignal)
					try {
						lock.lock();
						condition.await(); // next, wait until another light wanna change
						lock.unlock();
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					catch (IllegalMonitorStateException ie) {
						ie.printStackTrace();
					}
					
					// do the time handling stuff

					double elapsedSinceGreen = System.currentTimeMillis() - firstGreenTime;
					if(elapsedSinceGreen < 6000) { // wait the initial 6 seconds
						try {
							sleep((long)(6000 - elapsedSinceGreen));
						} catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					// reset the time to now
					elapsedSinceGreen = System.currentTimeMillis() - firstGreenTime;
					double elapsedSinceSignal = System.currentTimeMillis() - signal.lastSignalTime(this.id);
					
					// while the last impulsion arrived less than 2 secs before,
					// and we did not wait 12 secs now
					while(elapsedSinceSignal < 2000 && elapsedSinceGreen < 12000) {
						if(elapsedSinceGreen <= 10000) {// then we can wait for two full secs
							try {
								long t = 2000 - (long)elapsedSinceSignal;
								sleep(t); // wait until 2 secs elapsed since the last signal
							} catch( InterruptedException e){
								e.printStackTrace();
							}
						}
						else {// else, we wait until 12 seconds elapsed
							try {
								sleep((long)(12000 - elapsedSinceGreen));
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						// we need the current time for the while test
						elapsedSinceGreen = System.currentTimeMillis() - firstGreenTime;
						elapsedSinceSignal = System.currentTimeMillis() - signal.lastSignalTime(this.id);
					}
					
					signal.setFalseSignal(this.id);// the cars had enough time until now to pass =)
					changeState(ORANGE);
					break;
					
					
				case ORANGE:
					System.out.println("t="+clk.getTime()+": Light "+id+" changed to orange");
					try{
						sleep(2000);
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
					changeState(RED);
					signal.getMegaLock().unlock();// after we are red, we let the others do
					break;
					
			}
		}
	}

	private void changeState(LightState newState) {
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
