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
    private int id; //id of the elevator
    //private ArrayList<String> statusDirection;
    
    //set of fields to store the events for the elevator.		
    private Event newReceivedInfo;
    private Event oldReceivedInfo;
    private Event sendingInfo;
    
    private final long averageDoorClosing = 939000000; //average time taken to close a door.
    private final long averageFloorMoving = 950000000; //average time taken to move between two floors.
    
    private SystemError systemError; //Field Storing the event error status.
    //private ArrayList<Boolean> elevatorLamps;
    private boolean doorOpen; //boolean to storing if the door is open, true if open, false otherwise
    
    //Keep track of button lamps that are pressed with list //TODO
    
    
    private MotorState motorState;
    private MotorState previousDirection;
    private ElevatorStates state;
    
    //fields storing information about the elevator	
    private int currentFloor;
    private int targetFloor;
    private int tempTargetFloor;
    private String timeString;
	

    //Fields use to store  if a change in state was successful.
    private boolean successIdleState;
    private boolean successMoveState;
    private boolean successDestinationState; 
    
    private boolean elevator_activated; //Elevator Is Available to run or not
    private boolean door_stuck; //Boolean controlling if door is stuck.

    private ElevatorInterface eleInt;
    
    /**
     * Create a new Elevator with the assigned Scheduler, Constructor.
     * 
     * @param scheduler The scheduler object to be used for this elevator
     * @param ID : The ID used to identify the elevator by the components of the Elevator System.
     */
    public Elevator(Scheduler scheduler, int ID) 
    {
        this.scheduler = scheduler;
        doorOpen = false;
        motorState = MotorState.STOPPED; //constructs with it being stopped
        state = ElevatorStates.idleState; //Initially the Elevator is idle
        
        systemError = SystemError.NO_ERROR; //Constructs the elevator with no errors.
        elevator_activated = true; // Activates the elevator as soon as it is constructed.
        door_stuck = false; // door not stuck.
        
        //elevatorLamps = new ArrayList();
        //statusDirection = new ArrayList();
        this.id = ID; //Sets the ID
        this.currentFloor = 1;
        this.targetFloor = -1;
        tempTargetFloor = -1;
        sendingInfo = new Event();
        //newReceivedInfo = new Event();
        oldReceivedInfo = new Event();

    }
    
    /**
     * Deals with the Case when there is a hard fault in the system when the Elevator is Stucked between 2 floors.
     * @return 
	*/
	public void elevator_hard_fault(){
		System.out.println("systemerror " + systemError);
		if(systemError == SystemError.TRAVEL_FAULT){ // Deactivates the Elevator	
			//Prints where the Elevator is Stuck.	
			
			elevator_activated =false; //turns of the elevator from the system
			System.out.println("Elevator"+ id +"is Stuck between floors");
			System.out.println("Elevator is Stucked between floor: "+ currentFloor +" and "+ floorstuck()); 
		}
	}
	
	/**
	*Soft Handler Deals with the DOOR_FAULT transient error, where the door is not close.
	*/
	public void elevator_soft_fault(){

		//Checking if its an error
		if(systemError == SystemError.DOOR_FAULT){ 

			//tries to close the door 5 times
			for(int i=0;i<5;i++){
				long starttime = System.nanoTime(); //Gets the start time each time.

				//Printing 
				printWrapper("Door is closing...");
				
				try {
					Thread.sleep(9390/10);// Makes the code faster for testing, change for final submission.
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} //Waiting for 9.39 seconds 
				long endtime = System.nanoTime();//gets the endTime for closing the door
			        long elapsedtime = endtime - starttime; // Calculates the time elapsed. 
					
					//if the elapsed time is more than twice the average for closing the door
					if((elapsedtime > (10*averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						System.out.println("The door is still stuck."); //Prints
						continue; //Continue the for loop and tries to close again if condition is not met. 
					}else{
						doorOpen = false; //Succeeds in closing the door
						System.out.println("The door closed after "+ (i+1)+ " trials."); 
						return; //returns to the caller method.
					}
				
			}
		}
	}
	
	 /**
		* Prints out where the elevator is Stuck
		* @return The upper limit of the floor where the floor might have been stuck.
		*/
	private int floorstuck(){
		
			if(previousDirection == MotorState.UP){ //going up
				motorState = MotorState.STUCK; 
				return (currentFloor+1); //returns the maximum floor that the elevator might be stuck on.			
			}else if(previousDirection == MotorState.DOWN){ //going down
				motorState = MotorState.STUCK;
				return (currentFloor-1); //returns the maximum floor that the elevator might be stuck on.	
			}
			return -1;
		}
	
	/**
	* Getter method used to know if the elevator is activated.
	* @return true if elevator can be used, false otherwise.
	*/
	public boolean getElevatorActivation(){
		return elevator_activated;
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
    	printWrapper("Event Complete: " + sendingInfo);
    	scheduler.receiveData(sendingInfo);
    }
    
    /**
     * Called only from receiveRequest
     * Reads the recievedInfo from the Event passed from the scheduler
     * 
     * @param event The Event to load data from
     */
    public void readInfo(Event data) {
    	printWrapper("Read info: " +  data);
    	//Extract  info from DataObject
    	//sendingInfo = data;
    	
    	newReceivedInfo = data;

    	//currentFloor = data.getCurrentFloor();   
    	this.targetFloor = data.getTargetFloor();
    	//upDown = data.getUpDown();
    	timeString = data.getTimeString();
    	systemError = data.getErrorType(); //setting the error Type initially.
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
    /**
    *@return the motorstate.
    *
    */
    public MotorState getMotorState(){
    	return this.motorState;
    }
    
     
    /**
    *@return the time.
    *
    */	
    public String getTimeString(){
	return this.timeString;
    }
	
    /**
    *@return the target floor.
    *
    */
    public int getTargetFloor(){
	return this.targetFloor;
    }
     
    /**
    *@return the current floor of the elevator.
    */	
    public int getCurrentFloor(){
	return this.currentFloor;
     }
     
     /**
     *@return the id of the elevator
     */	
     public int getID() {  
	return id;
      }
	
     public Event getEvent() {
	return sendingInfo;
      }
	
      public SystemError getSystemError() {
	return systemError;
      }
    
    /**
     * Continually request the Scheduler for a new Event and process it using the state machine
     */
    public void run()
    {
    	
        while(systemError != SystemError.TRAVEL_FAULT) {
        	
        	//readEvent();  //Request an event from the scheduler 
        	//recieveAndSend();
        	//printState();
        	//&& systemError == SystemError.NO_ERROR
        	if(newReceivedInfo != null) {
        		changeState(); //Change the state of the elevator System
        	}
        	
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {}
        	
        }
        printWrapper("HARD ERROR: Travel Fault from Elevator! Need Emergency Technician Right Away!");
    }
    

    /**
     * Change the state of the Elevator according to the variables in the system and the current state
     */
	private void changeState() {
	    
		switch(state) {
		
		case idleState :
			//Set the motorState to STOPPED  and open the door
			previousDirection= motorState;
			motorState = motorState.STOPPED;
			long starttime = System.nanoTime(); //gets the time for which the door has been opened.
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
					starttime = System.nanoTime();
					printWrapper("Door is closing...");
					try {
						Thread.sleep(939);  //initial value: 9390
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //Waiting for 9.39 seconds 
					

					long endtime = System.nanoTime();//gets the endTime for closing the door
			        long elapsedtime = endtime -starttime;
					
					//if the elapsed time is more than twice the average for closing the door
					if((elapsedtime > (10*averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						System.out.println("The Door is Stuck on Elevator"+ id);
						//Soft Error Handle.
						elevator_soft_fault(); //Handles the Door not closing error.
					}
					
					
					printWrapper("going up...");
					doorOpen = false;
					motorState = motorState.UP; //Sets the state to going up
					previousDirection = motorState; //Stores the previous direction of the elevator.
					move();
					
				}
				//Check if targetFloor is less than currentFloor
				else if (targetFloor != currentFloor && targetFloor < currentFloor) {
					starttime = System.nanoTime();
					printWrapper("Door is closing...");
					try {
						Thread.sleep(9390/10); //initial value: 9390
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} //Waiting for 9.39 seconds 
					

					long endtime = System.nanoTime();//gets the endTime for closing the door
					long elapsedtime = endtime -starttime;
					
					//if the elapsed time is more than twice the average for closing the door
					if((elapsedtime > (10*averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						System.out.println("The Door is Stuck on Elevator"+ id);
						//Soft Error Handle.
						elevator_soft_fault(); //Handles the Door not closing error.
					}
					
					
					
					
					printWrapper("going down...");
					doorOpen = false;
					motorState = motorState.DOWN;
					previousDirection = motorState;
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
					sendingInfo = new Event(sendingInfo);
					eleInt.send(sendingInfo);
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
		
		long starttime;
		long finaltime;
		long elapsedtime;
		
		if((systemError == SystemError.TRAVEL_FAULT)){ //checks if upon calling move the door is still open or elevator is stuck
			elevator_hard_fault(); //Handles the TRAVEL FAULT;
			return;//exits the method.
		}
		
		while(currentFloor != targetFloor) {
			//Set oldReceivedInfo equal to newReceivedInfo
			oldReceivedInfo = newReceivedInfo;
			
			//Check if motorState equals UP. If true, then move elevator UP
			if (motorState.equals(MotorState.UP)) {
				starttime = System.nanoTime(); //gets the StartTime for moving up by 1 floor.
				try {
					Thread.sleep(9500/10);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finaltime = System.nanoTime();//Gets end time After moving up by 1 floor.
				elapsedtime = finaltime - starttime; //Computes the amount of time taken to move by 1 floor, repeats for every floor.

				//Actual TRAVEL_Fault	
				if(elapsedtime>(2 * averageFloorMoving)){ //verifying if the floor is taking too long to move
					systemError = SystemError.TRAVEL_FAULT;
					elevator_hard_fault();  //handler to handle the Travel Fault.
					return; //(Exit the while loop) 
				}
				
				
				currentFloor++;
				printWrapper("Elevator " + id + " moved to: " + currentFloor);
				
			}
			//Check if motorState equals UP. If true, then move elevator DOWN
			else if (motorState.equals(MotorState.DOWN)) {
				starttime = System.nanoTime(); //gets the StartTime for moving down by 1 floor.
				try {
					Thread.sleep(9500/10);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				finaltime = System.nanoTime();//Gets end time After moving down by 1 floor.			
				elapsedtime = finaltime - starttime; //Time taken to move down.

				//Actual TRAVEL_Fault	
				if(elapsedtime>(2 * averageFloorMoving)){
					systemError = SystemError.TRAVEL_FAULT;
					//call  hard error handling method
					elevator_hard_fault(); //Causes a hard fault if time taken is too big.
					motorState =  MotorState.STUCK;
					return; //exits the method 
				}

				currentFloor--; //decrements floor since going down.
				printWrapper("Elevator " + id + " moved to: " + currentFloor); //Prints formatted information.
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
	
	
	//sets the elevator interface
    	public void setElevatorInterface(ElevatorInterface eleInt) {
    		this.eleInt = eleInt;
    	}
	
	/**
	*Getter for test purposes used to return the received info that is an event
	*@return The received Event of the elevator.
	*/
	public Event getReceivedInfo() {
        	return newReceivedInfo;
        }
	
	/**
	*Getter method used to get the state of the elevator.
	*@return ElevatorStates - The state of the Elevator.
	*/
	public ElevatorStates getState(){
        	return this.state;
         }
	 
	/**
	*Code used to print the status of the Elevator.
	*@param - msg that needs to be formatted and printed
	*/
	 private void printWrapper(String msg) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    	LocalDateTime now = LocalDateTime.now();
				
			System.out.println("_____________________________________________________");
			System.out.println("                 Elevator: " + id);
			System.out.println("-----------------------------------------------------");
			System.out.println("Log at time: " + dtf.format(now));
			System.out.println(msg);
			System.out.println("_____________________________________________________");
	}
	 
	 private void printState() {
		 printWrapper("State: " + state + " \ncurrentFloor: " + currentFloor + " \ntargetFloor: " + targetFloor + "\nnewReceivedInfo: " + newReceivedInfo);
	 }
	 
}
