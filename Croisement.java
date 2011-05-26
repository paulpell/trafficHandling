package trafficHandling;

public class Croisement {
	SensorHandler sensorHandler;


	public void main() {
		sensorHandler = new SensorHandler();
		// ids defined in priority order
		TrafficLight NS = new TrafficLight(0, sensorHandler, trafficHandling.LightState.GREEN);
		TrafficLight EOD = new TrafficLight(1, sensorHandler);
		TrafficLight EOG = new TrafficLight(2, sensorHandler);
		sensorHandler.addLight(NS);
		sensorHandler.addLight(EOD);
		sensorHandler.addLight(EOG);
		NS.start();
		EOD.start();
		EOG.start();
		sensorHandler.start();
	}
}
