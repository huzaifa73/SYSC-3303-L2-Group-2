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
	
	private EventList eventList = new EventList();
	
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
    public void receive_request(Event event) {
    	eventList.put(event);
    	
    	//For iteration 1, send the request to elevator right away
    	//Further down the line, an algorithm implemented in the run() function will decide when to send requests
    	send_request(event);
    }
    
    //Send a request to the elevator
    public void request_event() {
    	if(eventList.getCount() != 0) {
    		elevator.receive_Request(eventList.get());
    	}
    	
    }
    
    //Send a request to the elevator
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
