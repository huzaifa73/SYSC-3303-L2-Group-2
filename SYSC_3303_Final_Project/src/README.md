# SYSC-3303-L2-Group-2
SYSC 3303 - L2 - Group 2

Files:

ElevatorSystem.java:

    A class to run the elevator system which initializes and runs the threads for the floor subsystem, scheduler, and elevator.
    
FloorSubsystem.java:

    A class that reads in events from a file and passes them on to the scheduler
    
Scheduler.java:

    A class to schedule the events for the elevators. It takes events received from both the Floor and the Elevators, and uses 
    an algorithm to decide the best Elevator to delegate the task to. It then uses UDP connection to send those events to the chosen elevator. 
    
Elevator.java:

    A class to execute the events received from the scheduler.


ElevatorInterface.java

   A class used as a stub for the Elevator class to implement UDP communications with the scheduler.
    
Event.java:

    The data object that contains the details of an elevator event.
    
-------------------
Setup Instructions:

To load project, place Iteration3.zip into the eclipse workspace, and go to 
File -> Open Projects from File System -> Archive 
Select the zip file. Click finish.

Navigate Iteration3.zip_expanded -> Interation3 -> pack -> ElevatorSystem.java

Run ElevatorSystem.java as main, see console for output.




