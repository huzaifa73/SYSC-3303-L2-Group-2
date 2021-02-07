package pack;
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
		Thread floorSubsystem, elevator, scheduler;
		Scheduler schedulerObj = new Scheduler();
		FloorSubsystem floorSubsystemObj = new FloorSubsystem(schedulerObj);
		Elevator elevatorObj = new Elevator(schedulerObj);
		schedulerObj.setup(floor_subsystemObj, elevatorObj);
		
		scheduler = new Thread(schedulerObj, "scheduler");
		floorSubsystem = new Thread(new FloorSubsystem(schedulerObj),"floorSubsystem");
		elevator = new Thread(new Elevator(schedulerObj),"elevator");

		
		
		//Start threads
		floorSubsystem.start();
		elevator.start();
		scheduler.start();

	}
}

