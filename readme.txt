our classes: 
	Clock
		used to count the time since the beginning of the program
		
	Croisement
		this class creates all the things we need for the simulation:
			a clock, an input thread (listens the keyboard), a sensorHandler,
			and the needed traffic lights.
	
	InputThread
		creates a java.util.Scanner, listens for input on the keyboard, and transmits
		to the SensorHandler

	LightState
		an enumeration for the set {RED, GREEN, ORANGE}

	Main
		just the start class, that launches the simulation

	SensorHandler
		Listens for input (through inputTread), tells the lights how they have to change

	TrafficLight
		This class (extending Thread), waits until it has to change, and does the timing 
		handling when it is green or orange
