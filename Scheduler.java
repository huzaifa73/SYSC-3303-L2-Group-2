package pack;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Class to run the scheduler system
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
    	System.out.println("New event added: " + eventList.get(eventList.size()-1));
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
    private void sendData(Event event) {
    	floorSubsystem.completeTransfer(event);
    }
    
    //Gets data back from the elevator
    public void receiveData(Event event) {
    	completedEventList.add(event);
    	eventList.remove(event);
    	sendData(event);

    }
    
    //Prints the contents of the event list
    public synchronized void printList() {
        for(Event e: eventList) {
        	System.out.println(e);
        }
    }
    
    public LinkedList<Event> getList(){
    	return eventList;
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
