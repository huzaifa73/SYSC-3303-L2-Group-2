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
    private FloorSubsystem floorSubsystem;
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
    public void setup(FloorSubsystem floorSubsystem, Elevator elevator) {
    	this.floorSubsystem = floorSubsystem;
    	this.elevator = elevator;
    }
    
    //receive_request from the floor system or elevators
    public synchronized void receiveRequest(Event event) {
    	eventList.add(event);
    	notifyAll();
    }
    
    //Allows elevator to request an event
    public synchronized void requestEvent() {
    	if(eventList.size() != 0) {
    		elevator.receiveRequest(eventList.peekFirst());
    	}
    	notifyAll();
    }
    
    
    //Sends data back to the floor subsystem
    private void sendData() {
    	
    }
    
    //Gets data back from the elevator
    public void receiveData(Event event) {
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
