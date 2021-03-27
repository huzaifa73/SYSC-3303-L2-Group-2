/**
 * The Elevator class implements a State Machine design that continually requests 
 * the Scheduler for new Events, processes them, and then notifies the Scheduler once it is 
 * finished to receive the next Event.
 * 
 * @authors Desmond, Hovish
 * @verison 1.00
 */

package pack;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

class Elevator implements Runnable{
	private Scheduler scheduler;
    private int id;
    //private ArrayList<String> statusDirection;
    
    private volatile static Event newReceivedInfo;
    private Event oldReceivedInfo;
    private Event sendingInfo;
    
    //private ArrayList<Boolean> elevatorLamps;
    private boolean doorOpen;
    
    //Keep track of button lamps that are pressed with list //TODO
    
    
    private MotorState motorState;
    private ElevatorStates state;
    
    private volatile static int currentFloor;
    private volatile static int targetFloor;
    private int tempTargetFloor;
    private String timeString;
    
    private boolean successIdleState;
    private boolean successMoveState;
    private boolean successDestinationState;


    
    /**
     * Create a new Elevator with the assigned Scheduler
     * 
     * @param scheduler The scheduler object to be used for this elevator
     */
    public Elevator(Scheduler scheduler, int ID) 
    {
        this.scheduler = scheduler;
        doorOpen = false;
        motorState = MotorState.STOPPED;
        state = ElevatorStates.idleState;
        
        //elevatorLamps = new ArrayList();
        //statusDirection = new ArrayList();
        this.id = ID;
        this.currentFloor = 1;
        this.targetFloor = 1;
        tempTargetFloor = -1;
        sendingInfo = new Event();
        //newReceivedInfo = new Event();
        oldReceivedInfo = new Event();

    }
    
    /**
     * Requests a new Event from the Scheduler, will block until one is received
     
    public void readEvent() {
    	//request recievedInfo from Scheduler
    	scheduler.requestEvent(); 
    }
    */
    
    /**
     * Called from the Scheduler class, used to send the previously requested event to the Elevator
     * 
     * @param event The Event object to be sent to the Elevator
     
    public void receiveRequest(Event event) {

    	newReceivedInfo = event;
    	printWrapper("RECEIVE REQUEST: " + newReceivedInfo.toString() + "\nCurrent Elevator Floor " + currentFloor);
    	readInfo(newReceivedInfo);
    	
    	printState();
	}
	*/
	
    
    /**
     * Called when an Event is completed to notify the Scheduler
     */
    public void sendEvent() {
    	scheduler.receiveData(sendingInfo);
    }
    
    /**
     * Called only from receiveRequest
     * Reads the recievedInfo from the Event passed from the scheduler
     * 
     * @param event The Event to load data from
     */
    public void readInfo(Event data) {
    	//Extract  info from DataObject
    	sendingInfo = data;

    	//currentFloor = data.getCurrentFloor();   
    	this.targetFloor = data.getTargetFloor();
    	//upDown = data.getUpDown();
    	timeString = data.getTimeString();
    }
    
    
    /**
     * Pushes the button in the elevator and updates button/lamp status
     * @param button The button that was pressed
     */
    public void pushButton(int button) {
    	//elevatorLamps.get(button) = true;
    	tempTargetFloor = button;
    	readInfo(newReceivedInfo);
    }
    
    //Getters for testing
    public MotorState getMotorState(){
		return this.motorState;
	}
	
	public String getTimeString(){
		return this.timeString;
	}

	public int getTargetFloor(){
		return this.targetFloor;
	}

	public int getCurrentFloor(){
		return this.currentFloor;
	}
	
	public int getID() {  
		return id;
	}
	
	public Event getEvent() {
		return sendingInfo;
	}
    
    /**
     * Continually request the Scheduler for a new Event and process it using the state machine
     */
    public void run()
    {
    	
        while(true) {
        	
        	//readEvent();  //Request an event from the scheduler 
        	//recieveAndSend();
        	printState();
        	
        	if(newReceivedInfo != null) {
        		changeState(); //Change the state of the elevator System
        	}
        	
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {}
        	
        }
        
    }
    

    /**
     * Change the state of the Elevator according to the variables in the system and the current state
     */
	private void changeState() {
	    
		switch(state) {
		
		case idleState :
			//Set the motorState to STOPPED  and open the door
			motorState = motorState.STOPPED;
			doorOpen = true;
			
			//Check if we received a task and check the targetFloor 
			if (newReceivedInfo != null) {
				
				if (currentFloor == targetFloor) {
					state = state.destinationState;
				}
				else {
					state = state.moveState;
				}
			}
			else {
				printWrapper("Elevator stopped and waiting...");
			}
			break;
			
		case moveState:

			//Check if there is new Received info from the Scheduler
			if (newReceivedInfo != null) {
				//Check if targetFloot is greater than currentFloor
				if (targetFloor != currentFloor && targetFloor > currentFloor) {
					printWrapper("going up...");
					doorOpen = false;
					motorState = motorState.UP;
					move();
					
				}
				//Check if targetFloor is less than currentFloor
				else if (targetFloor != currentFloor && targetFloor < currentFloor) {
					printWrapper("going down...");
					doorOpen = false;
					motorState = motorState.DOWN;
					move();
				}else { //Impossible state
					printWrapper("State Machine Error: s1");
				}
			}
			break;
			
		case destinationState:
			if (newReceivedInfo != null) {
				if (targetFloor == currentFloor) {
					printState();
					//Change state of motor to stopped and open door
					motorState = motorState.STOPPED;
					doorOpen = true;
					sendingInfo = newReceivedInfo;
					//Task is complete, clear the task info
					newReceivedInfo = null; 
					oldReceivedInfo = null;
					printWrapper("Elevator " + id + " reached target and is stopped on floor: " + currentFloor + " event: " + sendingInfo);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//sendEvent();
					state = state.idleState;				
				}
			}
			break;
		}
	}

	/**
	 * Moves the Elevator up or down a floor while taking into account the time 
	 */
	private void move() {
		
		while(currentFloor != targetFloor) {
			//Set oldReceivedInfo equal to newReceivedInfo
			oldReceivedInfo = newReceivedInfo;
			
			//Check if motorState equals UP. If true, then move elevator UP
			if (motorState.equals(MotorState.UP)) {
				try {
					Thread.sleep(9500);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentFloor++;
				printWrapper("Elevator " + id + " moved to: " + currentFloor);
				
			}
			//Check if motorState equals UP. If true, then move elevator DOWN
			else if (motorState.equals(MotorState.DOWN)) {
				try {
					Thread.sleep(9500);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				currentFloor--;
				printWrapper("Elevator " + id + " moved to: " + currentFloor);
			}
			
			//Request an event from the scheduler to see if there's an updated one
			//readEvent();
			
			//Check if there is a new request
			if (oldReceivedInfo != newReceivedInfo) {
				state = state.moveState;
				break;			
			}
		}
		//Go to state 2 if currentFloor == targetFloor
		if (currentFloor == targetFloor) {
			state = state.destinationState;	
		}
		
	}
	
	//Getter for test purposes
	public Event getReceivedInfo() {
        return newReceivedInfo;
    }
	
	//Get state for test purposes
	public ElevatorStates getState(){
        return this.state;
    }
	
	 private void printWrapper(String msg) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    	LocalDateTime now = LocalDateTime.now();
				
			System.out.println("_____________________________________________________");
			System.out.println("                 Elevator");
			System.out.println("-----------------------------------------------------");
			System.out.println("Log at time: " + dtf.format(now));
			System.out.println(msg);
			System.out.println("_____________________________________________________");
	}
	 
	 private void printState() {
		 printWrapper("State: " + state + " \ncurrentFloor: " + currentFloor + " \ntargetFloor: " + targetFloor + "\nnewReceivedInfo: " + newReceivedInfo);
	 }
	 
}
