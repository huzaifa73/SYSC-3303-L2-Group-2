package pack;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Class to run the elevator system
 * 
 * @author
 * @version 1.00
 */
public class Elevator_system {
	public static void main(String[] args) {
		
	//Create threads
	Thread floor_subsystem, elevator, scheduler;

	scheduler = new Thread(new Scheduler(), "scheduler");
	floor_subsystem = new Thread(new Floor_subsystem(scheduler),"floor_subsystem");
	elevator = new Thread(new Elevator(scheduler),"elevator");

        //Start threads
	floor_subsystem.start();
	elevator.start();
	scheduler.start();

	}
}

class Floor_subsystem implements Runnable{
	private Thread scheduler;
	public Floor_subsystem(Thread scheduler)
	{
        	this.scheduler = scheduler;
	}

	public void run()
	{
		while(true) {

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
        }
    }
}

class Elevator implements Runnable{
	private Thread scheduler;

	public Elevator(Thread scheduler)
	{
		this.scheduler = scheduler;
	}

	public void run()
	{
		while(true) {
	            
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {}
        	
		}
	}
}

class Scheduler implements Runnable{

	public Scheduler()
	{
		
	}

	public void run()
	{
        	while(true) {
            		try {
                		Thread.sleep(1000);
            		} catch (InterruptedException e) {}
        	}
	}
}
