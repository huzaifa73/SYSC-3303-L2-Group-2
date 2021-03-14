package pack;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Provides;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The Scheduler class implements a State Machine that provides
 * functionality for the Floor subsystem to send the events it processes 
 * from an input file, which can then be passed on to an Elevator requesting new events
 * @author
 * @version 1.00
 */

class Scheduler implements Runnable{
    private FloorSubsystem floorSubsystem;
    //Currently only handles one elevator
    private Elevator elevator;
    //elevator List
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket sendPacket, sendPacketFloor, recievePacket;
    
    private ArrayList<ElevatorInterface> elevatorInterfacesList; //TODO
    
    private ArrayList<LinkedList<Event>> elevatorQueues; 
    
    //private LinkedList<Event> elevatorEventList;
	
    private LinkedList<Event> eventList = new LinkedList<>();
	
    //List of completed events (mostly for debug purposes)
    private LinkedList<Event> completedEventList = new LinkedList<>();
    
    private volatile static Event currentEvent; //From the elevator or the floor
    
    private schedulerState state;
    private int elevatorNumber = 4;
    private int portElevator = 2000;
    private volatile static boolean isfloorRequest;
	
    //State Machine for scheduler
    //State 0: event list is empty
    //State 1: event list has content to provide elevator
    private enum schedulerState {
    	emptyState,
    	activeState
    }

    //Constructs the Scheduler
    public Scheduler()
    {
       try {
    	sendSocket = new DatagramSocket();
        	
        receiveSocket = new DatagramSocket(1999);
       }catch (SocketException se) {
    	   se.printStackTrace();
    	   System.exit(1);
       }
       
        state = schedulerState.emptyState;
        elevatorQueues = new ArrayList<LinkedList<Event>>();
   		//elevatorInterfacesList = new ArrayList<ElevatorInterface>();
   	
   		setUpElevatorQueues();
  
    }
    
    /**
     * Method: Recieves a Packet 
     */
    private void recieve() {
    	
    	byte data[] = new byte[100];
        recievePacket = new DatagramPacket(data, data.length);
        Event eventToSend;
    	
    	
    	try {
            receiveSocket.receive(recievePacket); //receivePacket

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        //sleep
        try { 
            Thread.sleep(5000);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }


       //get data from packet
       data = recievePacket.getData();
       

       //call Event function rebuildEvent that will convert the byte into a Event object
       currentEvent = Event.rebuildEvent(data); //current Event being handled
       
       //Method to figure out if it's from the elevator or the floorsubsystem
       isfloorRequest = Event.getFloorRequest(); //*******UPDATE WITH NEW CLASS
       
       schedulingAlgorithm(); //Handle how the recieved event will be used
    }
    

	/**
     * Method: Send Packet to Elevators
     */
    private void sendPacket(int elevatorIndex) {
    	
    	Event eventPeeked = (elevatorQueues.get(elevatorIndex)).peek();
        byte msg[] = Event.builtByteArray(eventPeeked);
        sendPacket =  new DatagramPacket(msg, msg.length,InetAddress.getLocalHost(),elevatorIndex+2000);

        try{
            sendSocket.send(sendPacket);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }	
    	
    }
    
    
    /**
     * Method: Sets up the elevator linkedList depending on Number of elevators
     */
    private void setUpElevatorQueues() {
    	
    	for (int i = 0; i < elevatorNumber ; i++) {
    		elevatorQueues.add(new LinkedList<Event>());
    		elevatorInterfacesList.add(new ElevatorInterface(portElevator+i, i)); //
    	}
		
	}
    
    /**
     * Method: Sorts the CurrentEvent towards the correct Elevator event list
     */
    private void schedulingAlgorithm() {
    	
    	if (!isFloorRequest) { //Elevator request
    		
    		int currentElevator = currentEvent.getElevatorNumber();
    		LinkedList<Event> tempEle = new LinkedList<>();
        	tempEle = (elevatorQueues.get(currentElevator));
    		elevatorScheduler(tempEle, currentElevator);
    		
    	}
    	else { //Floor Request
    		boolean eventDirection = currentEvent.getUpDown();
        	MotorState stateDirection;
        	if (eventDirection) {
        		stateDirection = MotorState.UP;
        	}
        	if (!eventDirection) {
        		stateDirection = MotorState.DOWN;
        	}
        	int personFloor = currentEvent.getCurrentFloor();
        	Elevator bestElevator;
        	ArrayList<Integer> floorDifferences;
        	ArrayList<MotorState> states;
        	ArrayList<Integer> correctDirection; //Indexes of the elevators in the correct Direction
        	
        	//Iterates through the ElevatorInterfaces and retrieves floor differences and MotorStates for each Elevator
        	for(ElevatorInterface elevatorInt: elevatorInterfacesList ) {
        		Elevator elevator = elevatorInt.getElevator(); //returns elevator object
        		int currentEleFloor = elevator.getCurrentFloor(); //int currentFloor
        		MotorState motorState = elevator.getMotorState(); //Up, Down, Stopped
        		int idEle = elevator.getID(); //Add Id to elevator
        		
        		int difference = Math.abs(personFloor - currentEleFloor);
        		
        		floorDifferences.add(difference);
        		states.add(motorState);
        	}
        	
        	//Iterates through the ArrayList of MotorStates and chooses the Elevators in the correct Direction of the event
        	for(int i=0; i<4;i++){
        		if (states.get(i) == stateDirection || states.get(i) == MotorState.STOPPED) {
        			correctDirection.add(i);	
        		}
        	}
        	
        	//Chooses the Elevator closest to the Floor
        	int minimum = 8;
        	int minimum_index;
        	for(int i = 0; i<4; i++){
        	    if((floorDifferences.get(i)< minimum)&& (correctDirection.contains(i))){
        	        minimum = floorDifferences.get(i);
        	        minimum_index = i;
        	    }
        	}
        	
        	
        	LinkedList<Event> tempEle = new LinkedList<>();
        	tempEle = (elevatorQueues.get(minimum_index));
        	
        	elevatorScheduler(tempEle, minimum_index);
        	
    		
    	}
    	
    }

    /**
     * Method: Sorts the events of the Elevator
     * @param tempEle
     */
	private void elevatorScheduler(LinkedList<Event> tempEle, int elevatorID) {
	
		tempEle.add(currentEvent);
		
		
		//if the elevator is going down then sort the list in ascending order
		if(currentEvent.getUpDown()){
			Collections.sort(tempEle, Comparator.comparing(Event::getTargetFloor));
		}
		
		//if the elevator is going down then sort the list in descending order
		else if(!currentEvent.getUpDown()){
			Collections.sort(tempEle, Comparator.comparing(Event::getTargetFloor).reversed());
		}
		
		sendPacket(elevatorID);
		
		//The send socket should include the head of the event list in the linkedList of the arrayList containing the Elevator Lists

	}

	//Needs an additional setup method
    public void setup(FloorSubsystem floorSubsystem, Elevator elevator) {
    	this.floorSubsystem = floorSubsystem;
    	this.elevator = elevator;
    }
    
    /**
     * Called from the Elevator or Floor systems to add a new event to the Scheduler
     * @param event The event to be added
     */
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
    		if((eventList.size() > 0) && ((elevator.getMotorState() == MotorState.UP) == (curr <= event.getTargetFloor()) || (event.getUpDown() == (event.getTargetFloor() <= elevator.getTargetFloor())))) {

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
    
    /**
     * Set the current state of the Scheduler State Machine
     */
    private void setState() {
    	if(eventList.size() == 0) {
    		state = schedulerState.emptyState;
    	}else {
    		state = schedulerState.activeState;
    	}
    }
    
    /**
     * Called from the Elevator subsystem to request a new event be sent 
     
    public synchronized void requestEvent() {
    	if(state == schedulerState.state1) {
		printWrapper("Sent Elevator Task");
    		elevator.receiveRequest(eventList.peekFirst());
    	}
    	notifyAll();
    }
    */
    
    /**
     * Notify the Floor system of any completed Events
     * @param event
     */
    private void sendData(Event event) {
	printWrapper("Sent completed event to floor: " + event);
    	floorSubsystem.completeTransfer(event);
    }
    
    /**
     * Called from the Elevator class once an Event has been completed
     * @param event
     */
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
