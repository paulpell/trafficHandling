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
				while (!signal.getSignal(this.id)) {//priority issue ?
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
				if(time < 6000) {
					try {
						sleep((long)(6000 - time));
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}

				// here, time values (approximately) 6000
				while(signal.lastSignalTime(this.id) < 2 && time < 12000) {
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
					time = System.currentTimeMillis() - firstGreenTime;
				}
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
