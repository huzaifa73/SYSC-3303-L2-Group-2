package pack;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class SchedulerTest {
	Scheduler scheduler = new Scheduler();
	@Before
	public void setup() throws Exception{
		FloorSubsystem floor_subsystem = new FloorSubsystem(scheduler);
		Elevator elevator = new Elevator(scheduler);
		scheduler.setup(floor_subsystem, elevator);
		
		
	}
	@Test
	void testReceiveRequest() {
		Event event = new Event();
		scheduler.receive_request(event);
		assertEquals(1, scheduler.getList().size());
		
	}
	
	@Test
	void testReceiveData() {
		Event event = new Event();
		scheduler.receive_request(event);
		assertEquals(1, scheduler.getList().size());
		
		
		scheduler.receiveData(event);
		assertEquals(0, scheduler.getList().size());
		
	}

	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
