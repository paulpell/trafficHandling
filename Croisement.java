package trafficHandling;

public class Croisement {
	private SensorHandler sensorHandler;


	public void main() {
		sensorHandler = new SensorHandler();
		Clock clk = new Clock();
		InputThread it = new InputThread(sensorHandler, clk);
		// ids are defined in priority order
		//TrafficLight NS = new TrafficLight(0, sensorHandler, trafficHandling.LightState.GREEN);
		TrafficLight NS = new TrafficLight(0, sensorHandler, clk, trafficHandling.LightState.GREEN);
		//TrafficLight EOD = new TrafficLight(1, sensorHandler);
		TrafficLight EOD = new TrafficLight(1, sensorHandler, clk);
		//TrafficLight EOG = new TrafficLight(2, sensorHandler);
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
