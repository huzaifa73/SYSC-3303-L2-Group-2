package pack;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

class FloorSubsystemTest {
	Scheduler scheduler = new Scheduler();

	File ioFile = new File("FloorInputFile.txt");
	
	FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, ioFile);

	@Test
	void testReadRequestEvents() {
		
		assertNotNull(floorSubsystem.readRequestEvents());
	}

}
