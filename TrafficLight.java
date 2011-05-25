package trafficHandling;

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
			if(state == RED) {
				while (!signal.getSignal(this.id)) {
					wait();
				}
				changeState(GREEN);
			}
			else if(state == GREEN) {
				sleep(6); // the first minimum 6 seconds
				int i=0; // number of times we waited for 
				while(signal.getSignal(this.id) && i<3){
					wait(2000);
					i++;
				}
				changeState(ORANGE);
			}
			else{ // orange
				sleep(2);
				changeState(RED);
			}
		}
	}

	private void changeState(LightState newState) {
		state = newState;
	}
}
