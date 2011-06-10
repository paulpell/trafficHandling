package trafficHandling;


import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * This class is just a Thread listening for the keyboard input, and 
 * sending the signals to its SensorHandler instance.
 */
class InputThread extends Thread {

	/** the SensorHandler instance, to give the new inputs */
	private SensorHandler sensorHandler;
	/** we need a java.util.Scanner, to read from the keyboard */
	private Scanner in;
	/** a Clock, to write a message with the current time */
	private Clock clk;

	private InputThread(){}

	/**
	 * @param sh the {@link SensorHandler} that controls the whole junction
	 * @param clk the {@link Clock} instance shared by the whole project
	 */
	public InputThread(SensorHandler sh, Clock clk) {
		in = new Scanner(System.in);
		this.clk = clk;
		sensorHandler = sh;
	}

	/**
	 * The main thread method, that listens for the keyboard in a loop,
	 * and reports all the inputs to the sensorHandler.
	 */
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
	
	// internally used, to know whether an input is valid.
	private boolean checkInput(int i) {
		return i < sensorHandler.getLightsCount();
	}
}
