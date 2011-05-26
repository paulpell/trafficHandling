package trafficHandling;
import static trafficHandling.LightState.*;

class TrafficLight extends Thread {

	private int id;
	private LightState state;
	private static SensorHandler signal;
	private double firstGreenTime;


	public TrafficLight(int id) {
		this.id = id;
		this.state = RED;
	}

	public TrafficLight(int id, LightState initialState) {
		this.id = id;
		this.state = initialState;
	}

	public void run() {
		while(true) {
			if(state == RED) { //state is red so wait for a signal to change state
				while (!signal.getSignal(this.id)) {//priority issue ?
					wait();
				}
				changeState(GREEN);
			}
			else if(state == GREEN /*&& !signal.getSignal(this.id) */) {
				firstGreenTime = System.currentTimeMillis();
				wait();// wait until some other light gets a signal (given by notify)
				
				double time = System.currentMillis() - firstGreenTime;
				if(time < 6000)
					sleep(6000 - time);
					//waitUntil6();
				// here, time values (approximately) 6000
				while(signal.lastSignalTime() < 2 && time < 12000) {
					if(time <= 10000) sleep(2000); // we must 2 seconds, as the 12 will not be exceeded
					else sleep(12000 - time);
					time = System.currentMillis() - firstGreenTime;
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
				sleep(2);
				changeState(RED);
			}
		}
	}

	private void changeState(LightState newState) {
		state = newState;
	}
}
