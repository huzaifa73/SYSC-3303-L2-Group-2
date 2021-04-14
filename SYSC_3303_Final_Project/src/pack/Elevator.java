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
	private ElevatorSystemGUI gui;
	private Scheduler scheduler;
    private int id; //id of the elevator
    //private ArrayList<String> statusDirection;
    
    //set of fields to store the events for the elevator.		
    private Event newReceivedInfo;
    private Event oldReceivedInfo;
    private Event sendingInfo;
    
    private final int NANO_SECOND_CONVERSION = 1000000000;
    private long averageDoorClosing = 939000000; //average time taken to close a door. (in ns)
    private long averageFloorMoving = 950000000; //average time taken to move between two floors. (in ns)

    private SystemError systemError; //Field Storing the event error status.
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
    private boolean softError;
    private int passengerCount;
    
    //*****Iteration 5
    private Boolean elevatorLamps[]; //Array of Elevator lamps 
    private int count = 0;
    
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
        
        //****ADDED Iteration 5
        elevatorLamps = new Boolean[22]; //Initialized default as 22 floors

    }
	
    public Elevator(Scheduler scheduler, int ID, ElevatorSystemGUI gui, double doorTime, double floorTime) 
    {
    	// Give doorTime in s, convert to nanoseconds
    	averageDoorClosing = (long)(doorTime * NANO_SECOND_CONVERSION);
    	averageFloorMoving = (long)(floorTime * NANO_SECOND_CONVERSION);
    	
    	passengerCount = 0;
        this.scheduler = scheduler;
        this.gui = gui;
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
        previousDirection = MotorState.UP; //TODO Added 

    }
	
    
    /**
     * Create a new Elevator without the scheduler parameter
     * 
     * @param ID : The ID used to identify the elevator by the components of the Elevator System.
     */
    public Elevator(int ID) 
    {
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
     * Deals with the Case when there is a hard fault in the system when the Elevator is Stuck between 2 floors.
     * @return 
	*/
	public void elevator_hard_fault(){
		System.out.println("systemerror " + systemError);
		if(systemError == SystemError.TRAVEL_FAULT){ // Deactivates the Elevator	
			//Prints where the Elevator is Stuck.
			gui.setElevatorState(id, "OUT_OF_ORDER");

			printWrapper("HARD ERROR: Travel Fault from Elevator! Need Emergency Technician Right Away! Stuck between " + currentFloor + " and " + floorstuck());
			
			elevator_activated =false; //turns of the elevator from the system
			//System.out.println("Elevator " + id + " is Stucked between floor: "+ currentFloor +" and "+ floorstuck()); 
		}
	}
	
	/**
	*Soft Handler Deals with the DOOR_FAULT transient error, where the door is not close.
	*/
	public void elevator_soft_fault(){
		System.out.println("The Door is Stuck on Elevator"+ id);

		//Checking if its an error
		if(systemError == SystemError.DOOR_FAULT){ 
			softError = true;
			gui.setElevatorState(id, "Door Stuck");
			
			int count = 0;

			//tries to close the door 5 times
			for(int i=0;i<5;i++){
				long starttime = System.nanoTime(); //Gets the start time each time.

				//Printing 
				printWrapper("Attempting to close door...");
				
				try {
					// Convert nano second unit to ms
					Thread.sleep(averageDoorClosing/NANO_SECOND_CONVERSION*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} //Waiting for 9.39 seconds 
				long endtime = System.nanoTime();//gets the endTime for closing the door
			        long elapsedtime = endtime - starttime; // Calculates the time elapsed. 
					
					//if the elapsed time is more than twice the average for closing the door
			        
					if((elapsedtime > (10 * averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						printWrapper("The door is still stuck after " + (i+1) + " attempts"); //Prints Door status update
						count = 1+1;
						continue; //Continue the for loop and tries to close again if condition is not met. 
					}else{
						doorOpen = false; //Succeeds in closing the door
						return; //returns to the caller method.
					}
				
			}
			systemError = SystemError.NO_ERROR;
			printWrapper("The door closed after "+ count + " trials."); 
		    gui.setElevatorState(id, "IDLE");
		}
	}
	
	 /**
		* Prints out where the elevator is Stuck
		* @return The upper limit of the floor where the floor might have been stuck.
		*/
	private int floorstuck(){
		
			if (currentFloor == 1) {
				return 2;
			}
			else if (currentFloor == 22) {
				return 21;
			}
			else if(previousDirection == MotorState.UP){ //going up
				motorState = MotorState.STUCK; 
				return (currentFloor+1); //returns the maximum floor that the elevator might be stuck on.			
			}else if(previousDirection == MotorState.DOWN){ //going down
				motorState = MotorState.STUCK;
				return (currentFloor-1); //returns the maximum floor that the elevator might be stuck on.	
			}
			return currentFloor+1;
		}
	
	/**
	* Getter method used to know if the elevator is activated.
	* @return true if elevator can be used, false otherwise.
	*/
	public boolean getElevatorActivation(){
		return elevator_activated;
	}
	


    
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
    	
    	this.elevatorLamps = elevatorLamps;
    	
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
     *@return the motorstate.
     *
     */
     public MotorState getPreviousDirection(){
     	return this.previousDirection;
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
	
	public boolean getSoftError() { 
    	  return softError;
      }
	
	public void setSystemError(SystemError error) {
		systemError = error;
	}
    
    /**
     * Continually request the Scheduler for a new Event and process it using the state machine
     */
    public void run()
    {
    	
        while(elevator_activated) {
        	
        	if(newReceivedInfo != null) {
        		changeState(); //Change the state of the elevator System
        	}
        	
	        try {
	            Thread.sleep(1000);
	        } catch (InterruptedException e) {}
        	
        }
        if(!elevator_activated) {
        	printWrapper("Elevator is deactivated");
        }
        checkElevatorErrorState();
        //printWrapper("HARD ERROR: Travel Fault from Elevator! Need Emergency Technician Right Away!");
    }
    

    /**
     * Change the state of the Elevator according to the variables in the system and the current state
     */
	public void changeState() {
	    
		//gui.setElevatorState(id, );
		switch(state) {
		
		case idleState :
			gui.setElevatorState(id,"IDLE");
			//Set the motorState to STOPPED  and open the door
			//previousDirection= motorState;
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
			//gui.setElevatorState(id,"MOVING");
			//Check if there is new Received info from the Scheduler
			if (newReceivedInfo != null) {
				//Check if targetFloot is greater than currentFloor
				if (targetFloor != currentFloor && targetFloor > currentFloor) {
					long starttime = System.nanoTime(); //gets the time for which the door has been opened.
					printWrapper("Door is closing...");
					try {
						Thread.sleep(averageDoorClosing/NANO_SECOND_CONVERSION*1000);  //initial value: 9390
					} catch (InterruptedException e) {
						e.printStackTrace();
					} //Waiting for 9.39 seconds 
					

					long endtime = System.nanoTime();//gets the endTime for closing the door
			        long elapsedtime = endtime -starttime;
					
					//if the elapsed time is more than twice the average for closing the door
					if((elapsedtime > (10*averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						elevator_soft_fault(); //Handles the Door not closing error.
					}
					
					systemError = SystemError.NO_ERROR;
					printWrapper("going up...");
					doorOpen = false;
					motorState = motorState.UP; //Sets the state to going up
					previousDirection = motorState; //Stores the previous direction of the elevator.
					move();
					
				}
				//Check if targetFloor is less than currentFloor
				else if (targetFloor != currentFloor && targetFloor < currentFloor) {
					long starttime = System.nanoTime();
					printWrapper("Door is closing...");
					try {
						Thread.sleep(averageDoorClosing/NANO_SECOND_CONVERSION*1000); //initial value: 9390
					} catch (InterruptedException e) {
						e.printStackTrace();
					} //Waiting for 9.39 seconds 
					

					long endtime = System.nanoTime();//gets the endTime for closing the door
					long elapsedtime = endtime -starttime;
					
					//if the elapsed time is more than twice the average for closing the door
					if((elapsedtime > (10*averageDoorClosing))||(systemError == SystemError.DOOR_FAULT)){
						//Soft Error Handle.
						elevator_soft_fault(); //Handles the Door not closing error.
					}
					
					
					
					systemError = SystemError.NO_ERROR;
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
			checkElevatorErrorState();
			printWrapper("" + newReceivedInfo);
			if (newReceivedInfo != null) {
				if (targetFloor == currentFloor) {
					gui.setElevatorState(id,"ARRIVED"); //TODO
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
					
					/**
					 * ?: Event should only be marked complete if it is an elevator event?
					 * Put first block in else?
					 */
					//Event Complete. Send to scheduler
					printWrapper("EVENT COMPLETE: " + sendingInfo);
					sendingInfo.setIsComplete(true);
					eleInt.send(sendingInfo);
					
					count++;
					
					//If it was a floor request complete, send back another event
					if(sendingInfo.isFloorRequest) {
						sendingInfo = new Event(sendingInfo);
						sendingInfo.setCurrentFloor(currentFloor);
						sendingInfo.setElevatorNumber(id);  //TODO
						printWrapper("NEW CREATED EVENT: (" + count + ")"  + sendingInfo);
						eleInt.send(sendingInfo);
						gui.setPassengerCount(id+1, ++passengerCount);
					}
					else {
						gui.setPassengerCount(id+1, --passengerCount);
					}
					
					try { //TODO TESTING
						Thread.sleep(1000); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					gui.setElevatorState(id, "IDLE");
					
				}
			}
			state = state.idleState;
			checkElevatorErrorState();
			
			break;
		}
	}

	/**
	 * Method: Checks if the Elevator is in a System Error State
	 */
	private void checkElevatorErrorState() {
		//Check for Door fault
		if (systemError == SystemError.DOOR_FAULT) {
			elevator_soft_fault();
		}
		//Else check for the TRAVEL_FAULT
		else if (systemError == SystemError.TRAVEL_FAULT) {
			elevator_hard_fault();
		}
		
	}

	/**
	 * Moves the Elevator up or down a floor while taking into account the time 
	 */
	private void move() {
		
		long starttime;
		long finaltime;
		long elapsedtime;
		
		gui.setElevatorState(id, "MOVING");
		
		if((systemError == SystemError.TRAVEL_FAULT)){ //checks if upon calling move the door is still open or elevator is stuck
			elevator_hard_fault(); //Handles the TRAVEL FAULT;
			return;//exits the method.
		}
		
		while((currentFloor != targetFloor) && elevator_activated) {
			//Set oldReceivedInfo equal to newReceivedInfo
			oldReceivedInfo = newReceivedInfo;
			
			//Check if motorState equals UP. If true, then move elevator UP
			if (motorState.equals(MotorState.UP) && currentFloor < targetFloor) { //TODO
				starttime = System.nanoTime(); //gets the StartTime for moving up by 1 floor.
				try {
					Thread.sleep(averageFloorMoving/NANO_SECOND_CONVERSION*1000);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				finaltime = System.nanoTime();//Gets end time After moving up by 1 floor.
				elapsedtime = finaltime - starttime; //Computes the amount of time taken to move by 1 floor, repeats for every floor.

				//Actual TRAVEL_Fault
				if((elapsedtime>(10 * averageFloorMoving)) || (systemError == SystemError.TRAVEL_FAULT)){ //verifying if the floor is taking too long to move or TRAVEL_FAULT
					systemError = SystemError.TRAVEL_FAULT;
					elevator_hard_fault();  //handler to handle the Travel Fault.
					return; //(Exit the while loop) 
				}
				
				
				currentFloor++;
				printWrapper("Elevator " + id + " moved to: " + currentFloor + " . Target Floor: " + targetFloor); //TODO
				gui.setElevatorFloor(id, currentFloor);
				
			}
			//Check if motorState equals UP. If true, then move elevator DOWN
			else if (motorState.equals(MotorState.DOWN) && currentFloor > targetFloor) { //TODO
				starttime = System.nanoTime(); //gets the StartTime for moving down by 1 floor.
				try {
					Thread.sleep(averageFloorMoving/NANO_SECOND_CONVERSION*1000);  //The time it takes the elevator to move one floor
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				finaltime = System.nanoTime();//Gets end time After moving down by 1 floor.			
				elapsedtime = finaltime - starttime; //Time taken to move down.

				//Actual TRAVEL_Fault	
				if((elapsedtime>(10 * averageFloorMoving)) || (systemError == SystemError.TRAVEL_FAULT)){ //verifying if the floor is taking too long to move or TRAVEL_FAULT
					systemError = SystemError.TRAVEL_FAULT;
					//call  hard error handling method
					elevator_hard_fault(); //Causes a hard fault if time taken is too big.
					motorState =  MotorState.STUCK;
					return; //exits the method 
				}

				currentFloor--; //decrements floor since going down.
				printWrapper("Elevator " + id + " moved to: " + currentFloor + " . Target Floor: " + targetFloor); //TODO //Prints formatted information.
				gui.setElevatorFloor(id, currentFloor);
			}
			
			//Request an event from the scheduler to see if there's an updated one
			//readEvent();
			
			/**
			 * ?: Doesn't this overwrite the event, losing the current one?
			 */
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
	 * Setter method to change the elevators lamp list
	 * @param elevatorLamps
	 */
	public void setElevatorLamps(Boolean[] elevatorLamps) {
		this.elevatorLamps = elevatorLamps;
	}
	
	/**
	 * Setter method to change Elevator Activation
	 * @param b
	 */
	public void setActivation(boolean b) {
		elevator_activated = b;
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
