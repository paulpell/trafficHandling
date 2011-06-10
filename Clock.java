package trafficHandling;

/**
 * This class conveniently counts the time. That is, it povides a function
 * that returns the current time since the beginning of the program, in double
 * with one decimal.
 */

public class Clock extends Thread {
	
	/** Stores the begin time */
	private long beginTime;
	/** stores the current time */
	private long time;
	
	/**
	 * Constructor, to set up the clock: set the begin time
	 */
	public Clock () {
		beginTime = System.currentTimeMillis();
		time = 0;
	}

	/**
	 * main method, that loops, doing:
	 * <ul> <li>sleep 100 ms</li>
	 * 		<li> set the new time </li> </ul>
	 */
	public void run() {
		while(true) {
			try {
				sleep(100);//100 millis
				time = System.currentTimeMillis() - beginTime;
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This is that nice function, providing time
	 */
	// tricky function to give the time in a format with one decimal
	public double getTime() {
		int foo = (int)Math.ceil(time / 100.); // tenths of seconds
		return foo/10.;
	}

}
