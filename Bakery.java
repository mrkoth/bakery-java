import java.util.Random;

public class Bakery extends Thread {

	public int threadID;
	public static final int totalThreads = randInt(2,15);
	public static volatile int globalCounter = 0;
	
	// boolean table: indicates that a thread wants to enter
	private static volatile boolean[] choosing = new boolean[totalThreads];  
	// ticket: indicates priority of threads
	private static volatile int[] num = new int[totalThreads]; 
	
	
	public Bakery(int id) {threadID = id;}

	
	public void run() {
				try {
					//This loop indicates how many times the thread will try to enter
					for (int i = 0; i < totalThreads; i++) {
						entry(threadID);
						globalCounter++;
						System.out.println("ThreadID: " + threadID);
						System.out.println("\nCounter: " + globalCounter);
						exit(threadID);
					}
				}catch (Exception e) {}
	}

	public void entry(int id) {
		//Declares will to enter
		choosing[id] = true;
		num[id] = max() + 1;
		choosing[id] = false;
		
		for (int j = 0; j < totalThreads; j++) {			
			// Wait till j gets ticket
			while (choosing[j]) {}
			// Wait until all threads with smaller or same ticket number or higher priority finish
			while (num[j] != 0 && (num[id] > num[j] || (num[id] == num[j] && id > j))) {}
		}
	}

	
	private void exit(int id) {
		num[id] = 0;
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	private int max() {
		int maximum = num[0];
		for (int i = 1; i < num.length; i++) {
			if (num[i] > maximum)
				maximum = num[i];
		}
		return maximum;
	}

	public static void main(String[] args) {
		for (int i = 0; i < totalThreads; i++) {
			choosing[i] = false;
			num[i] = 0;
		}

		Bakery[] threads = new Bakery[totalThreads];
		
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Bakery(i);
			threads[i].start();
		}

		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Final Count: " + globalCounter);
	} 

}
