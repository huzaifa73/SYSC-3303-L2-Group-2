package pack;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Class to run the elevator system
 * 
 * @author
 * @version 1.00
 */

class Scheduler implements Runnable{
    private Floor_subsystem floor_subsystem;
    //Currently only handles one elevator
    private Elevator elevator;
	
    private LinkedList<Event> eventList = new LinkedList<>();
	
    //List of completed events (mostly for debug purposes)
    private LinkedList<Event> completedEventList = new LinkedList<>();
	
    //Constructs the Scheduler
	public Scheduler()
    {

    }
    
    //Needs an additional setup method
    public void setup(Floor_subsystem floor_subsystem, Elevator elevator) {
    	this.floor_subsystem = floor_subsystem;
    	this.elevator = elevator;
    }
    
    //receive_request from the floor system or elevators
    public synchronized void receive_request(Event event) {
    	eventList.add(event);
    	notifyAll();
    }
    
    //Allows elevator to request an event
    public synchronized void request_event() {
    	if(eventList.size() != 0) {
    		elevator.receive_Request(eventList.get());
    	}
    	notifyAll();
    }
    
    
    //Sends data back to the floor subsystem
    private void send_data() {
    	
    }
    
    //Gets data back from the elevator
    private void receive_data(Event event) {
    	completedEventList.add(event);
    	eventList.remove(event);
    	send_data();

    }
    
    //Prints the contents of the event list
    public synchronized void printList() {
        for(Event e: eventList) {
        	System.out.println(e);
        }
    }

    public void run()
    {
        while(true) {
        	printList();


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
    }
}
