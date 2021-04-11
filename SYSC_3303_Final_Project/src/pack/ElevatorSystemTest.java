package pack;

import static org.junit.jupiter.api.Assertions.*;

import javax.naming.directory.InvalidAttributesException;

import org.junit.jupiter.api.Test;

class ElevatorSystemTest {
		

	@Test
	//Test receive request method
	void testReceiveRequest() {
		Elevator elevator = new Elevator(1);
		
		try {
			Event event = new Event(false, 0, "Down", 4, 2, 0);
			
			elevator.readInfo(event);
			
			assertEquals(event.getTargetFloor(), elevator.getTargetFloor());
			assertEquals(event.getTimeString(), elevator.getTimeString());
		} catch (InvalidAttributesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
    void testElevatorState() {
        Elevator elevator = new Elevator(0); //create elevator 

        assertEquals(ElevatorStates.idleState, elevator.getState()); //checks that the initial state in the elevator is state 0

        Event event = new Event();
        
        //elevator.receiveRequest(event);

    }

    @Test
    void ElevatorReceivedInfo() {
        Elevator elevator = new Elevator(1); //create elevator 

        Event event = new Event(); //Create new event
        elevator.readInfo(event); //received event from scheduler

        assertNotNull(elevator.getReceivedInfo()); // assert that the receivedInfo is not null

    }
	


}
