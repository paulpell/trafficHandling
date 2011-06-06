package trafficHandling;



public class Clock extends Thread {
	
	private long beginTime;
	private long time;
	
	public Clock () {
		beginTime = System.currentTimeMillis();
		time = 0;
	}


	public void run() {
		while(true) {
			try {
				sleep(100);//100 millis
				time = System.currentTimeMillis() - beginTime;
			} catch(InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	// tricky function to give the time in a format with one decimal
	public double getTime() {
		int foo = (int)time / 100; // tenths of seconds
		return foo/10.;
	}

}
