package trafficHandling;
import static trafficHandling.LightState.*;

class TrafficLight extends Thread {

	private int id;
	private LightState state;
	private static SensorHandler signal;


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
				saveTime();
				wait();// wait until some other light gets a signal (given by notify)
				
				if(time < 6)
					waitUntil6();
				
				while(signal.lastSignalTime() < 2)
					wait2_orUntil12();
				
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
