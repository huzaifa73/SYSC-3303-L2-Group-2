/**
 * 
 */
package pack;

import static org.junit.jupiter.api.Assertions.*;

import javax.naming.directory.InvalidAttributesException;

import org.junit.jupiter.api.Test;

/**
 *  Test Class for the Elevator Subsystem
 * @author Blake
 * @version: March 26, 2020
 *
 */
class ElevatorTest {
	
	//Elevator elevator = new Elevator(scheduler, 0);
	//Event event;
	
	/**
	 * Test that the Elevator State machine  is in the idleState 
	 */
	@Test
    void testElevator_ReadEvent() {
		//Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(10); //create elevator 

        //assertNotEquals(5, elevator.getCurrentFloor());  //Checks that the current floor is not 5
        assertEquals(ElevatorStates.idleState, elevator.getState()); //checks that the initial state in the elevator is in the idleState

		try {
			Event event = new Event(true, 0, "Up", 4, 2, 0);
			elevator.readInfo(event);
			
			assertEquals(MotorState.STOPPED, elevator.getMotorState());  //Checks that the MotorState is STOPPED
			assertEquals(2, elevator.getTargetFloor()); //Checks that the targetFloor is 2
			assertEquals(10, elevator.getID()); //Checks that the ID of the Elevator is 0
			assertEquals(SystemError.NO_ERROR, elevator.getSystemError()); //Checks that there are no errors in the elevator 
			
		} catch (InvalidAttributesException e) {
			 //TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Test
    void testElevator_IdleState() {
        Elevator elevator = new Elevator(9); //create elevator 

        assertEquals(ElevatorStates.idleState, elevator.getState()); //checks that the initial state in the elevator is state 0

    }
	
	/**
	 * Test that the Elevator State machine  is in the moveState 
	 */
	@Test
    void testElevator_moveState() {
        Elevator elevator = new Elevator(11); //create elevator 

        assertNotEquals(5, elevator.getCurrentFloor());  //Checks that the current floor is not 5
        assertEquals(ElevatorStates.idleState, elevator.getState()); //checks that the initial state in the elevator is state Idle

		try {
			Event event = new Event(true, 0, "Up", 4, 2, 0);
			elevator.readInfo(event);
			elevator.changeState();
			
			assertEquals(ElevatorStates.moveState, elevator.getState()); //checks that the initial state in the elevator is in the moveState
			
		} catch (InvalidAttributesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	/**
	 * Test that the Elevator State machine  is in the destinationState 
	 */
	@Test
    void testElevator_DestinationState() {
		//Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(12); //create elevator 

		try {
			Event event = new Event(true, 0, "Up", 4, 1, 0);  //Target State is 1 and the current Floor of the Elevator is 1
			elevator.readInfo(event);
			elevator.changeState();
			
			assertEquals(ElevatorStates.destinationState, elevator.getState()); //checks that the initial state in the elevator is in the destination State
			
		} catch (InvalidAttributesException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Test that there was a Soft Error for the Door Fault Error
	 */
	@Test
    void testElevator_DoorError() {
		//Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(13); //create elevator 

		try {
			Event event = new Event(true, 0, "Up", 4, 1, 1);  //Target State is 1 and the current Floor of the Elevator is 1, default error is Soft (Door Fault)
			elevator.readInfo(event);
			elevator.changeState();
			
			assertEquals(false, elevator.getSoftError()); //checks that the initial state in the elevator is in the destination State
			
		} catch (InvalidAttributesException e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * Test that there was a Hard Error for the Floor fault Error
	 */
	@Test
    void testElevator_FloorError() {
		//Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(14); //create elevator 

		try {
			Event event = new Event(true, 0, "Up", 4, 1, 2);  //Target State is 1 and the current Floor of the Elevator is 1, default error is Hard (Floor Fault)
			elevator.readInfo(event);
			elevator.changeState();
			
			assertEquals(elevator.getSystemError(), SystemError.TRAVEL_FAULT); //checks that the initial state in the elevator is in the Stuck State
			
		} catch (InvalidAttributesException e) {
			e.printStackTrace();
		}
    }


}
