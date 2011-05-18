package concurrence;

class TrafficLight extends Thread {

	private int id;
	private enum EtatFeu {ROUGE, VERT, ORANGE}
	private EtatFeu etat;
	private static Detecteur signal;


	public TrafficLight(int id) {
		this.id = id;
	}

	public void run() {
		while( true ) {
			if(etat == ROUGE) {
				askChange(VERT);
				whil
			}
			else if(etat == VERT) {
				sleep(6); // the first minimum 6 seconds
				int i=0; // number of times we waited fo
				while(){
					wait(2);
				}
			}
			else{
				sleep(2);
				changeState(ROUGE);
			}
		}
	}


}
