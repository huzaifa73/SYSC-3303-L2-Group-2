package pack;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    
    private schedulerState state;
	
    //State Machine for scheduler
    //State 0: event list is empty
    //State 1: event list has content to provide elevator
    private enum schedulerState {
    	state0,
    	state1
    }

    //Constructs the Scheduler
    public Scheduler()
    {
	state = schedulerState.state0;
    }
    
    //Needs an additional setup method
    public void setup(FloorSubsystem floorSubsystem, Elevator elevator) {
    	this.floorSubsystem = floorSubsystem;
    	this.elevator = elevator;
    }
    
    //receive_request from the floor system or elevators
    public synchronized void receiveRequest(Event event) {
    	
    	//loop through list
    	int curr = elevator.getCurrentFloor();
	
	//Check for redundant requests
    	boolean redundant = false;
    	int targetFloor = event.getTargetFloor();
    	for(Event e: eventList) {
    		if(e.getTargetFloor() == targetFloor) {
    			redundant = true;
    			//No need to add this event to the list. We will already complete it.
    		}
    	}
    
    	//For non redundant requests
    	if(redundant == false) {    	
		//Check if the event floor is on the way. Then check if the direction is correct
		if(elevator.getUpDown() == (curr <= event.getTargetFloor()) || (event.getUpDown() == (event.getTargetFloor() <= elevator.getTargetFloor()))) { //TODO check what this means...

			//if the target floor is in the right direction:
			//put it in the order of closest to current floor of that elevator
			for (int i = 0; i < eventList.size(); i++) {
				if((event.getTargetFloor() - curr) < (eventList.get(i).getTargetFloor() - curr)) {
					eventList.add(i, event);
				}
			}
		}else {
			//Otherwise add to end of list
			printWrapper("Received request in the opposite direction");
			eventList.add(event);
		}
	}
    	
    	//TODO check that event was added.
    	setState();
    	System.out.println("New event added: " + eventList.get(eventList.size()-1));
    	notifyAll();
    }
    
    private void setState() {
    	if(eventList.size() == 0) {
    		state = schedulerState.state0;
    	}else {
    		state = schedulerState.state1;
    	}
    }
    
    //Allows elevator to request an event
    public synchronized void requestEvent() {
    	if(state == schedulerState.state1) {
		printWrapper("Sent Elevator Task");
    		elevator.receiveRequest(eventList.peekFirst());
    	}
    	notifyAll();
    }
    
    
    //Sends data back to the floor subsystem
    private void sendData(Event event) {
	printWrapper("Sent completed event to floor: " + event);
    	floorSubsystem.completeTransfer(event);
    }
    
    //Gets data back from the elevator
    public void receiveData(Event event) {
    	completedEventList.add(event);
    	eventList.remove(event);
    	sendData(event);
    	setState();

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
    
    
    
	private void printWrapper(String msg) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    		LocalDateTime now = LocalDateTime.now();
		
		System.out.println("_____________________________________________________");
		System.out.println("                 Scheduler");
		System.out.println("-----------------------------------------------------");
		System.out.println("Log at time: " + dtf.format(now));
		System.out.println(msg);
		System.out.println("_____________________________________________________");
	}
}
