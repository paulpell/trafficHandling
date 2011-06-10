package trafficHandling;


import java.util.Scanner;
import java.util.InputMismatchException;

class InputThread extends Thread {

	private SensorHandler sensorHandler;
	private Scanner in;
	private Clock clk;


	public InputThread(SensorHandler sh, Clock clk) {
		in = new Scanner(System.in);
		this.clk = clk;
		sensorHandler = sh;
	}

	public void run() {
		int value = 0;
	
		while(true) {
			try {
				value = in.nextInt();
				if (checkInput(value)){
					System.out.println("t=" + clk.getTime() + " : capteur " + value + " actif");
					sensorHandler.setSignal(value, true);
				} else {
					System.err.println("Invalid Input");
				}
			} catch(InputMismatchException e) {
				System.err.println("Invalid integer input");
				in.next();
				continue;
			}
			
		}
	
	}
	
	private boolean checkInput(int i) {
		return i < sensorHandler.getLightsCount();
	}
}
