package pack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ElevatorSystemTest {
	Scheduler scheduler = new Scheduler();
	Elevator elevator = new Elevator(scheduler);
	
	

	@Test
	void testReceiveRequest() {
		FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler);
		Elevator elevator = new Elevator(scheduler);
		
		Event event = new Event();
		elevator.receiveRequest(event);
		
		assertEquals(event.getCurrentFloor(), elevator.getCurrentFloor());
		assertEquals(event.getTargetFloor(), elevator.getTargetFloor());
		assertEquals(event.getTimeString(), elevator.getTimeString());
		assertEquals(event.getUpDown(), elevator.getUpDown());
		
		
		
	}
	
	


}
