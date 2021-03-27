package pack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ElevatorSystemTest {
	Scheduler scheduler = new Scheduler();
	Elevator elevator = new Elevator(scheduler, 0);
	
	

	@Test
	//Test receive request method
	void testReceiveRequest() {
		FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler);
		Elevator elevator = new Elevator(scheduler, 1);
		
		Event event = new Event();
		//elevator.receiveRequest(event);
		
		assertEquals(event.getTargetFloor(), elevator.getTargetFloor());
		assertEquals(event.getTimeString(), elevator.getTimeString());
		
		
		
	}
	
	@Test
    void testElevatorState() {
        FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler); //create floorsubsystem
        Elevator elevator = new Elevator(scheduler, 0); //create elevator 

        assertEquals(ElevatorStates.idleState, elevator.getState()); //checks that the initial state in the elevator is state 0

        Event event = new Event();
        
        //elevator.receiveRequest(event);

    }

    @Test
    void ElevatorReceivedInfo() {
        FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler); //create floorsubsystem
        Elevator elevator = new Elevator(scheduler, 1); //create elevator 

        Event event = new Event(); //Create new event
        //elevator.receiveRequest(event); //received event from scheduler

        assertNotNull(elevator.getReceivedInfo()); // assert that the receivedInfo is not null

    }
	


}
