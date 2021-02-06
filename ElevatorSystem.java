package pack;
import java.util.LinkedList;
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
		Scheduler schedulerObj = new Scheduler();
		Floor_subsystem floor_subsystemObj = new Floor_subsystem(schedulerObj);
		Elevator elevatorObj = new Elevator(schedulerObj);
		schedulerObj.setup(floor_subsystemObj, elevatorObj);
		
		scheduler = new Thread(schedulerObj, "scheduler");
		floor_subsystem = new Thread(new Floor_subsystem(schedulerObj),"floor_subsystem");
		elevator = new Thread(new Elevator(schedulerObj),"elevator");

		
		
		//Start threads
		floor_subsystem.start();
		elevator.start();
		scheduler.start();

	}
}

class Event{
	boolean fromFloor;
	int floorNumber;
	int elevatorNumber;
	String time;
	
	public Event(boolean fromFloor, int floorNumber) {
		this.fromFloor = fromFloor;
		this.floorNumber = floorNumber;
		elevatorNumber = -1;
		time = "-1";
	}
	
	public Event(boolean fromFloor, int floorNumber, int elevatorNumber, String time) {
		this.fromFloor = fromFloor;
		this.floorNumber = floorNumber;
		this.elevatorNumber = elevatorNumber;
		this.time = time;
	}
	
	public void print() {
		System.out.println("fromFloor: " + fromFloor + " floorNumber: " + floorNumber + " elevatorNumber: " + elevatorNumber + " time: " + time);
	}
}

class Floor_subsystem implements Runnable{
	private Scheduler scheduler;
    public Floor_subsystem(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    //sends event request to the scheduler
    private void send_Request(Event event) {
    	scheduler.receive_request(event);
    }
    
    //get data back from scheduler
    public void receive_Data() {
    	
    }

    public void run()
    {
        for(int i = 0; i < 20; i++) {

        	//test
        	Event event = new Event(true, i);
        	send_Request(event);
        	
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}

class Elevator implements Runnable{
    private Scheduler scheduler;
    private Event nextFloor;

    public Elevator(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }
    
    //sends event request to the scheduler
    private void send_Request(Event event) {
    	//scheduler.receive_request(event);
    }
    
    //sends data to the scheduler
    //Probably just stating the event has been completed
    private void send_data(Event event) {
    	
    }
    
    //get event request from scheduler
    public void receive_Request(Event event) {
    	
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
	private Floor_subsystem floor_subsystem;
	private Elevator elevator;
	
	private EventList eventList = new EventList();
	
	//List of completed events (mostly for debug purposes)
	private LinkedList<Event> completedEventList = new LinkedList<>();
	
    public Scheduler()
    {

    }
    
    //get the floor system and elevators
    public void setup(Floor_subsystem floor_subsystem, Elevator elevator) {
    	this.floor_subsystem = floor_subsystem;
    	this.elevator = elevator;
    }
    
    //receive_request from 
    public void receive_request(Event event) {
    	eventList.put(event);
    	send_request(event);
    }
    
    private void send_request(Event event) {
    	elevator.receive_Request(event);
    }
    
    
    //Sends data back to the floor subsystem
    private void send_data() {
    	
    }
    
    //Gets data back from the elevator
    private void receive_data(Event event) {
    	completedEventList.add(event);
    	eventList.remove(event);

    }

    public void run()
    {
        while(true) {
        	eventList.print();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}
