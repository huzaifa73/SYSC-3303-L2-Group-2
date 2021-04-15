package pack;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * The Scheduler class implements a State Machine that provides
 * functionality for the Floor subsystem to send the events it processes 
 * from an input file, which can then be passed on to an Elevator requesting new events
 * @author Jerry, Desmond, Hovish, Cam
 * @version 3/27/2021
 */

class Scheduler implements Runnable{
    private FloorSubsystem floorSubsystem;
    //Currently only handles one elevator
    private Elevator elevator;
    //elevator List
    
    private ElevatorSystemGUI gui;
    
    DatagramSocket sendSocket, receiveSocket;
    DatagramPacket sendPacket, sendPacketFloor, recievePacket;
    
    private ArrayList<ElevatorInterface> elevatorInterfacesList = new ArrayList<ElevatorInterface>(); //List of the elevator interfaces
    
    private ArrayList<LinkedList<Event>> elevatorQueues = new ArrayList<LinkedList<Event>>();
    
    //private LinkedList<Event> elevatorEventList;
	
    private LinkedList<Event> eventList = new LinkedList<>();
	
    //List of completed events (mostly for debug purposes)
    private LinkedList<Event> completedEventList = new LinkedList<>();
    
    private volatile static LinkedList<Event> currentEventQueue = new LinkedList<>(); //From the elevator or the floor
    
    private schedulerState state;
    private int totalEventsCount;
    
    private int elevatorNumber = 4;
    private int portElevator = 69;
    //private boolean isfloorRequest;
    private int recieveCount;
    private int sentCount;
	
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
	
    //Constructs the Scheduler with the GUI as an input
    public Scheduler(ElevatorSystemGUI gui, double doorTime, double floorTime, File inputFile)
    {
    	this.gui = gui;
       try {
    	sendSocket = new DatagramSocket();
        	
        receiveSocket = new DatagramSocket(1999);
       }catch (SocketException se) {
    	   se.printStackTrace();
    	   System.exit(1);
       }
       
    // Get total number of events to be processed
	try {		
		BufferedReader bReader = new BufferedReader( //new buffered reader to read requests events
				new FileReader(inputFile));
		
		int count = 0;
		while((bReader.readLine()) != null) {
			count++;
		}
		bReader.close();
		totalEventsCount = count;
		System.out.println("NUMBER OF LINES IN FILE: " + count);
		
	} catch (IOException e) {
		System.err.println("Error reading file in scheduler: " + e);
		System.exit(1);
	}
       
		gui.setCompleteCount(0, 2*totalEventsCount);
       
        state = schedulerState.emptyState;
        elevatorQueues = new ArrayList<LinkedList<Event>>();
   		//elevatorInterfacesList = new ArrayList<ElevatorInterface>();
   	
   		setUpElevatorQueues(gui, doorTime, floorTime);
  
    }
    
    /**
     * Method: Receives a Packet 
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
            Thread.sleep(500);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }


       //get data from packet
       data = recievePacket.getData();
       Event currentEvent = Event.rebuildEvent(data);
       
       recieveCount++;
       printWrapper("Recieved Event " + recieveCount + " from Floor Request: " + currentEvent.getIsFloorRequest());
       //private int sentCount;
       
       
       //printWrapper("Completed Event: " + currentEvent); 
       if(currentEvent.getIsComplete()) {
    	   printWrapper("Got a completed event: " + currentEvent.getElevatorNumber());

    	   printWrapper("Elevator + " + currentEvent.getElevatorNumber() + " Current Queue: " + elevatorQueues.get(currentEvent.getElevatorNumber()));
    	   printWrapper("Completed Event: " + elevatorQueues.get(currentEvent.getElevatorNumber()).peek()); 
    	   
    	   LinkedList<Event> currentQueue = elevatorQueues.get(currentEvent.getElevatorNumber());
    	   
    	   boolean eventRemoved = false;
    	   
    	   //Need to know the Elevator Queue is popping the right Event ---Pop if target and destination are the same TODO
    	   for (int j = 0; j < currentQueue.size(); j++) {
    		   if ((currentQueue.get(j).getTargetFloor() == currentEvent.getTargetFloor()) && (currentQueue.get(j).getFinalDestination() == currentEvent.getFinalDestination())) {
    			   elevatorQueues.get(currentEvent.getElevatorNumber()).remove(j);
    			   eventRemoved = true;
    		   }
    	   }
    	   
    	   if (!eventRemoved) {
    		   printWrapper("ERROR! Event not POPPED From Elevator " + currentEvent.getElevatorNumber()+ " Queue. NOTE: Event Target: " + currentEvent.getTargetFloor() + ". Event Destination: " + currentEvent.getFinalDestination() + ". FROM: " + currentQueue );
    	   }
    	   
    	   printWrapper("Remaining Elevator " + currentEvent.getElevatorNumber() + " Queue: " + elevatorQueues.get(currentEvent.getElevatorNumber()));
    	   
    	   //Remove Event from currentEventQueue TODO
    	   boolean mainListRemoved = false;
    	   for (int j = 0; j < currentEventQueue.size(); j++) {
    		   if ((currentEventQueue.get(j).getCurrentFloor() == currentEvent.getCurrentFloor()) && (currentEventQueue.get(j).getFinalDestination() == currentEvent.getFinalDestination())) { //TODO Changed from Target to Current Floor
    			   currentEventQueue.remove(j);
    			   mainListRemoved = true;
    		   }
    	   }
    	   if (!mainListRemoved) {
    		   printWrapper("ERROR! Event not POPPED From Scheduler Queue");
    	   }
    	   printWrapper("Remaining Scheduler Queue: " + currentEventQueue);
    	   
    	   
    	   
    	   completedEventList.add(currentEvent);
    	   gui.setCompleteCount(completedEventList.size(), 2*totalEventsCount);
       		System.out.println("CURRENT NUMBER OF COMPLETED EVENTS: " + completedEventList.size() + 
       			"\nOUT OF: " + totalEventsCount);
       		
       		
       		//Send Another Event to Elevator if Scheduler's currentEventQueue is EMPTY and Elevator's QUEUE is NOT EMPTY//TODO
       		if ((currentEventQueue.isEmpty()) && !(elevatorQueues.get(currentEvent.getElevatorNumber()).isEmpty())){
       			sendPacket(currentEvent.getElevatorNumber());
       		}
       }
       else if (!currentEvent.isFloorRequest) { //TODO
    	   //New Event Create by Elevator -> This needs to go to the same elevator 
    	   elevatorScheduler(currentEvent.getElevatorNumber(), currentEvent);
    	   printWrapper("Received Button Event from Elevator: " + currentEvent.getElevatorNumber());
       }
       
       else {
    	   //
           currentEventQueue.add(currentEvent);
           printWrapper("Recently Added Scheduler Queue: " + currentEventQueue);
           printWrapper("Got data from something ... " + currentEvent);
       }
    }
	
	   /**
     * Method: Stop the Elevator Threads
     */
    public void stopElevatorThreads() {
    	for(int j = 0; j <elevatorInterfacesList.size(); j++ ) {
				elevatorInterfacesList.get(j).getElevator().setActivation(false);		
		}
    }
    

    /**
     * Method: Send Packet to Elevators
     */
    private void sendPacket(int elevatorIndex) {
	if(elevatorQueues.get(elevatorIndex).size() == 0) {
    		return;
    	}
    	Event eventPeeked = (elevatorQueues.get(elevatorIndex)).peek(); //TODO BEfore: peeked()
    	printWrapper("EVENT PEEKED " + eventPeeked);
        byte msg[] = Event.buildByteArray(eventPeeked);
        try {
			sendPacket =  new DatagramPacket(msg, msg.length,InetAddress.getLocalHost(),elevatorIndex+portElevator);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}

        try{
            printWrapper("About to send packet to elevator : " + elevatorIndex + " event: " + eventPeeked);
            sendSocket.send(sendPacket);
        }catch(IOException e){
            e.printStackTrace();
            System.exit(1);
        }	
        
        sentCount++;
        printWrapper("Sent Event " + sentCount + " from Floor Request: " + eventPeeked.getIsFloorRequest()); //TODO
    	
    }
    
    
    /**
     * Method: Sets up the elevator linkedList depending on Number of elevators
     */
    private void setUpElevatorQueues() {
    	
    	
    	ElevatorInterface eleInt = null;
    	
    	for (int i = 0; i < elevatorNumber ; i++) {
    		printWrapper("Making an elevator interface");
    		Thread eleInterface;
    		elevatorQueues.add(new LinkedList<Event>());
    		eleInt = new ElevatorInterface(portElevator+i, i, this);
    		elevatorInterfacesList.add(eleInt);
    		//printWrapper("Setup Elevator Interface ID: " + eleInt);
    		eleInterface = new Thread(eleInt, "eleInterface");
    		eleInterface.start();
 
    	}
		
    }
	
	
    /**
     * Method: Sets up the elevator linkedList depending on Number of elevators
     * Overloaded method, passes in the gui
     */
    private void setUpElevatorQueues(ElevatorSystemGUI gui, double doorTime, double floorTime) {
    	
    	
    	ElevatorInterface eleInt = null;
    	
    	for (int i = 0; i < elevatorNumber ; i++) {
    		printWrapper("Making an elevator interface");
    		Thread eleInterface;
    		elevatorQueues.add(new LinkedList<Event>());
    		eleInt = new ElevatorInterface(portElevator+i, i, this, gui, doorTime, floorTime);
    		elevatorInterfacesList.add(eleInt);
    		//printWrapper("Setup Elevator Interface ID: " + eleInt);
    		eleInterface = new Thread(eleInt, "eleInterface");
    		eleInterface.start();
    	}
    }
    
    /**
     * Method: Sorts the CurrentEvent towards the correct Elevator event list
     */
    private void schedulingAlgorithm() {
    	
    	Event currentEvent = currentEventQueue.pop();
    	if (!currentEvent.isFloorRequest) { //Elevator request
    		printWrapper("Event Requested by Elevator " + currentEvent.getElevatorNumber());
    		int currentElevator = currentEvent.getElevatorNumber();

		
    		elevatorScheduler(currentElevator, currentEvent);
    		
    	}
    	else { //Floor Request
    		//TODO destroy the evidence   ....
    		
    		if(currentEvent != null) {
    			printWrapper("YAY IT GOES HERE" + currentEvent.getElevatorNumber());
    			
            	printWrapper("Chose elevator " + currentEvent.getElevatorNumber());
            	elevatorScheduler(currentEvent.getElevatorNumber(), currentEvent);
            	return;
    			
    		}
    		
    		
    		
    		boolean eventDirection = currentEvent.getUpDown();
        	MotorState stateDirection = null;
        	if (eventDirection) {
        		stateDirection = MotorState.UP;
        	}
        	if (!eventDirection) {
        		stateDirection = MotorState.DOWN;
        	}
        	int personFloor = currentEvent.getCurrentFloor();
        	Elevator bestElevator;
        	ArrayList<Integer> floorDifferences = new ArrayList<Integer>();
        	ArrayList<MotorState> states =  new ArrayList<MotorState>();
        	ArrayList<Integer> correctDirection =  new ArrayList<Integer>(); //Indexes of the elevators in the correct Direction
        	
        	//Iterates through the ElevatorInterfaces and retrieves floor differences and MotorStates for each Elevator
        	for(ElevatorInterface elevatorInt: elevatorInterfacesList ) {
        		
        		        		
        		Elevator elevator = elevatorInt.getElevator(); //returns elevator object
        		int currentEleFloor = elevator.getCurrentFloor(); //int currentFloor
        		
        		//If an elevator is doing nothing, give it a task
        		if(elevator.getReceivedInfo() == null && (elevator.getMotorState()!= MotorState.STUCK)) {
        			LinkedList<Event> tempEle = new LinkedList<>();

                	printWrapper("Chose elevator " + elevator.getID());
                	elevatorScheduler(elevator.getID(), currentEvent);
                	return;
        		}
        		
        		MotorState motorState = elevator.getMotorState(); //Up, Down, Stopped
        		int idEle = elevator.getID(); //Add Id to elevator
        		
        		int difference = Math.abs(personFloor - currentEleFloor);
        		
        		floorDifferences.add(difference);
        		states.add(motorState);
        	}
        	
        	boolean eventAdded = false; //boolean to check if Event got scheduled
        	//Iterates through the ArrayList of MotorStates and chooses the Elevators in the correct Direction of the event
        	for(int i=0; i<4;i++){
        		if ((states.get(i) == stateDirection || states.get(i) == MotorState.STOPPED) && (states.get(i)!= MotorState.STUCK)) {
        			correctDirection.add(i);	
        			eventAdded = true;
        		}
        	}
        	
        	//Error check: Event did not get scheduled. Revert to default
        	if (!eventAdded) {
        		for(int i=0; i<4;i++){
            		if ((states.get(i)!= MotorState.STUCK)) {
            			correctDirection.add(i);	
            			eventAdded = true;
            		}
            	}
        	}
        	
        	//Chooses the Elevator closest to the Floor of current Event
        	int minimum = 100;
        	int minimum_index = 0;
        	for(int i = 0; i<4; i++){
        	    if((floorDifferences.get(i) < minimum) && (correctDirection.contains(i)) && (states.get(i)!= MotorState.STUCK)){
        	        minimum = floorDifferences.get(i);
        	        minimum_index = i;
        	    }
        	}   	
        	
        	printWrapper("Chose elevator " + minimum_index);
        	elevatorScheduler(minimum_index, currentEvent);
        	
    		
    	}
    	
    	//WHAT IF SCHEDULER QUEUE IS EMPTY???????TODO
    	
    }

    /**
     * Method: Sorts the events of the Elevator
     * @param tempEle
     */
	private void elevatorScheduler(int elevatorID, Event currentEvent) {
		//JX: ok... I have no clue why we are using a temp list that's passed in...
		
		//LinkedList<Event> elevatorQueue = elevatorQueues.get(elevatorID);
		
		
		//Check for Duplicates
		LinkedList<Event> currentQueue = elevatorQueues.get(currentEvent.getElevatorNumber());
		boolean duplicate = false;
		for (int j = 0; j < currentQueue.size(); j++) {
 		   if ((currentQueue.get(j).getTargetFloor() == currentEvent.getTargetFloor()) && (currentQueue.get(j).getFinalDestination() == currentEvent.getFinalDestination())) {
 			  duplicate = true;
 		   }
 	   }
		
		//Add the event
	    if (!duplicate) {
	    	elevatorQueues.get(elevatorID).add(currentEvent);   	
	    }
		
		Elevator currentElevator = elevatorInterfacesList.get(elevatorID).getElevator();
	
		
		printWrapper("elevator scheduler ... ");
		if(elevatorQueues.get(elevatorID).size() < 2) {
			
			printWrapper("Only Task: No need to sort");
		}
		
		/**
		 * ?: Null check should be at the start not here TODO
		 */
		//if the elevator is going up then sort the list in ascending order
		/**
		else if(currentEvent.getUpDown()){
			if(elevatorQueues.get(elevatorID) == null) {
				printWrapper("tempEle is NULL !!! ERROR");
			}else if(Comparator.comparing(Event::getTargetFloor) == null){
				printWrapper("The other thing is NULL !!! ERROR");
			}else {

			Comparator<Event> eventComparator = Comparator.comparingInt(Event::getTargetFloor);
			//forget sorting for now...
			Collections.sort(elevatorQueues.get(elevatorID), eventComparator);
			}
		}
		
		//if the elevator is going down then sort the list in descending order
		else if(!currentEvent.getUpDown()){
		
		//forget sorting for now...
			Collections.sort(elevatorQueues.get(elevatorID), Comparator.comparingInt(Event::getTargetFloor).reversed());
		}
		**/

		//Sort in ascending order if the elevator is going up
		else if (currentElevator.getPreviousDirection().equals(MotorState.UP)) {
				if (currentEvent.getTargetFloor() > currentElevator.getCurrentFloor()) {
					Comparator<Event> eventComparator = Comparator.comparingInt(Event::getTargetFloor);
					Collections.sort(elevatorQueues.get(elevatorID), eventComparator);
					printWrapper("MEEEEEEEEEEE@@@@@@@@@@@");
				}
		}
		//Sort in descending order if the elevator is going down
		else if (currentElevator.getPreviousDirection().equals(MotorState.DOWN)) {
				if (currentEvent.getTargetFloor() < currentElevator.getCurrentFloor()) {
					Comparator<Event> eventComparator = Comparator.comparingInt(Event::getTargetFloor).reversed();
					Collections.sort(elevatorQueues.get(elevatorID), eventComparator);	
					printWrapper("MEEEEEEEEEEE22222222222");
				}
			
		}
		
		printWrapper("Event Target Floor: " + currentEvent.getTargetFloor() + " && Elevator Current Floor: " + currentElevator.getCurrentFloor());
		
		//New Algorithm for Improvement TODO
		//if (ElevatorMotorState == UP){
			//if (Event Target < Elevator.currentFloor){
					//Add event to END of LIST
		    //else {
				//add event to list and sort in ascending Order
		       //}
		//else if (ElevatorMotorState == DOWN) {
			//if (Event Target > Elevator.currentFloor){
					//Add event to END of LIST
		    //else {
				//add event to list and sort in descending Order
		       //}
		  //}
		//else {
			//add Event to Queue at End of List
	     //}
		
		printWrapper("Elevator Queue: " + elevatorQueues.get(elevatorID)); //print elevator queue
		
		
		sendLamps(elevatorQueues.get(elevatorID), elevatorID); //Send list of lamps to chosen elevator
		sendPacket(elevatorID);
		
		//The send socket should include the head of the event list in the linkedList of the arrayList containing the Elevator Lists

	}
	
         /**
	 * Method: Creates an array of lamps for each Elevator
	 * @param tempEle
	 */
	public void sendLamps(LinkedList<Event> tempEle, int elevatorID) {
		
		ArrayList tempArray = new ArrayList();
		
		for (Event e: tempEle) {
			tempArray.add(e.getTargetFloor());	
		}
		
		Boolean[] tempLamps = new Boolean[23]; //Initialize temp array for lamps
		tempLamps[0] = false;
		
		//Create Boolean list of lamps for elevator
		for (int i = 0; i < tempArray.size(); i++) {
			tempLamps[(int) tempArray.get(i)] = true;

		}
		
		//Send Boolean list to appropriate elevator
		for(int j = 0; j <elevatorInterfacesList.size(); j++ ) {
			if (j == elevatorID) {
				elevatorInterfacesList.get(j).getElevator().setElevatorLamps(tempLamps);
			}
		}	
	}

	//Needs an additional setup method
    public void setup(FloorSubsystem floorSubsystem) {
    	this.floorSubsystem = floorSubsystem;
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
    	
    	//Check that event was added.
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
    	System.out.println("CURRENT NUMBER OF COMPLETED EVENTS: " + completedEventList.size() + 
    			"\nOUT OF: " + totalEventsCount);
    	
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
        while(completedEventList.size() < 2 * totalEventsCount) { //TODO Change to 2 *
        	recieve();
        	printList();

        	if(currentEventQueue.size() > 0) {
        		schedulingAlgorithm();
        	}else {
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {}

			sendPacket(0);
			sendPacket(1);
			sendPacket(2);
			sendPacket(3);
		}
		
        }
        
        printWrapper("All events completed!");
        gui.stopTimer();
        
        for(int i = 0; i < elevatorInterfacesList.size(); i++) {
        	stopElevatorThreads();
        }
        
        // End program and stop all threads
        
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
