package pack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class SchedulerTest {
	Scheduler scheduler = new Scheduler();
	@Before
	public void setup() throws Exception{
		
		
	}
	@Test
	void testReceiveRequest() {
		FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler);
		Elevator elevator = new Elevator(scheduler);
		scheduler.setup(floor_subsystem, elevator);
		Event event = new Event();
		scheduler.receiveRequest(event);
		assertEquals(1, scheduler.getList().size());
		
	}
	
	@Test
	void testReceiveData() {
		
		FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler);
		Elevator elevator = new Elevator(scheduler);
		scheduler.setup(floor_subsystem, elevator);
		
		//add event
		Event event = new Event();
		scheduler.receiveRequest(event);
		assertEquals(1, scheduler.getList().size());
		
		//remove event
		scheduler.receiveData(event);
		assertEquals(0, scheduler.getList().size());
		
	}


}
