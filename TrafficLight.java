package concurrence;

class TrafficLight extends Thread {

	private int id;
	private enum LightState {RED, GREEN, ORANGE}
	private LightState state;
	private static Detector signal;


	public TrafficLight(int id) {
		this.id = id;
	}

	public void run() {
		while(true) {
			if(etat == RED) {
				while (!signal.getSignal()) {
					wait();
				}
				changeState(GREEN);
			}
			else if(etat == GREEN) {
				sleep(6); // the first minimum 6 seconds
				int i=0; // number of times we waited for 
				while(){
					wait(2);
				}
				changeState(ORANGE);
			}
			else{ // orange
				sleep(2);
				changeState(RED);
			}
		}
	}

	private void changeState(EtatFeu newState) {
		state = newState;
	}
}
