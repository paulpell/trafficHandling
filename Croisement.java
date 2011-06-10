package trafficHandling;

/**
 * Class that contains all the necessary stuff to simulate one junction.
 * That is: a clock, a SensorHandler, an InputThread (listening on the
 * keyboard and transmitting signals to sensorHandler), and some
 * TrafficLights. <br/>
 * Then it starts all these threads, that is it starts the simulation.
 */
public class Croisement {

	/**
	 * The method that does all the things
	 */
	public void main() {
		SensorHandler sensorHandler = new SensorHandler();
		Clock clk = new Clock();
		InputThread it = new InputThread(sensorHandler, clk);
		// ids are defined in priority order
		TrafficLight NS = new TrafficLight(0, sensorHandler, clk, trafficHandling.LightState.GREEN);
		TrafficLight EOD = new TrafficLight(1, sensorHandler, clk);
		TrafficLight EOG = new TrafficLight(2, sensorHandler, clk);
		sensorHandler.addLight(NS);
		sensorHandler.addLight(EOD);
		sensorHandler.addLight(EOG);
		clk.start();
		NS.start();
		EOD.start();
		EOG.start();
		it.start();
		sensorHandler.start();
	}
}
