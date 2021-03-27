package pack;
import java.io.File;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Class to run the elevator system
 * 
 * @author
 * @version 1.00
 */
public class ElevatorSystem {
	public static void main(String[] args) {
		
		//Create threads
		Thread floor_subsystem, scheduler;
		
		//Create Objects
		Scheduler schedulerObj = new Scheduler();
		
		//File FloorInputFile.txt should be stored in directly in the project folder
		File ioFile = new File("FloorInputFile.txt");
		FloorSubsystem floorSubsystem = new FloorSubsystem(schedulerObj, ioFile);
		schedulerObj.setup(floorSubsystem);
		
		//Later: Create more elevators with Threads here
		
		
		//Initialize threads
		scheduler = new Thread(schedulerObj, "scheduler");
		floor_subsystem = new Thread(floorSubsystem,"floor_subsystem");


		//Start threads
		floor_subsystem.start();
		scheduler.start();

	}
}
