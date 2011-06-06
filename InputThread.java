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
					//System.out.printf("t= %1.1f : capteur %d active\n", (double)System.currentTimeMillis()/1000, value);
					System.out.println("t="+clk.getTime()+" : capteur "+value+" actif");
					sensorHandler.setSignal(value, true);
				} else {
					System.out.println("Capteur invalide");
				}
			} catch(InputMismatchException e) {
				System.out.println("invalid integer input");
				in.next();
				continue;
			}
			
		}
	
	}
	
	private boolean checkInput(int i) {
		return i < sensorHandler.getLightsCount();
	}
}
