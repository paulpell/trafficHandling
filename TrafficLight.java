package trafficHandling;
import static trafficHandling.LightState.*;

class TrafficLight extends Thread {

	private int id;
	private LightState state;
	private static SensorHandler signal;
	private double firstGreenTime;


	public TrafficLight(int id, SensorHandler signal) {
		this.id = id;
		this.signal = signal;
		this.state = RED;
	}

	public TrafficLight(int id, SensorHandler signal, LightState initialState) {
		this.id = id;
		this.signal = signal;
		this.state = initialState;
	}

	public synchronized void run() {
		while(true) {
			if(state == RED) { //state is red so wait for a signal to change state
				while (!signal.getSignal(this.id)) {//priority issue ? SHOULD BE FINE
													// hasSignal would cause trouble
					try {
						wait();
					} catch( InterruptedException e) {
						e.printStackTrace();
					}
				}
				changeState(GREEN);
			}
			else if(state == GREEN /*&& !signal.getSignal(this.id) */) {
				firstGreenTime = System.currentTimeMillis();
				try{
					wait();// wait until some other light gets a signal (given by notify)
				} catch(InterruptedException e) {
					e.printStackTrace();
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
				System.out.printf("Light %i changed to orange after %1.1f", id, time);
				changeState(ORANGE);
				/*
				sleep(6); // the first minimum 6 seconds
				int i = 0; // number of times we waited for 
				do {
					wait(2000);
					i++;
				} while(signal.getSignal(this.id) && i<3);
				changeState(ORANGE);*/
			}
			else{ // orange wait 2 second go to orange
				try{
					sleep(2);
				} catch( InterruptedException e) {
					e.printStackTrace();
				}
				changeState(RED);
			}
		}
	}

	private void changeState(LightState newState) {
		state = newState;
	}
}
