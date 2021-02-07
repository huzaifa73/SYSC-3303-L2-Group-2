# SYSC-3303-L2-Group-2
SYSC 3303 - L2 - Group 2
------------------------
ITERATION 1
------------------------

Roles: 
-----------------------
Hovish: Elevator.java, UML Diagram, Sequence Diagram, test cases
Desmond: Elevator.java, UML Diagram, Sequence Diagram, test cases
Jerry: Schedule.java, Event.java, UML Diagram, Sequence Diagram, test cases
Cam: Floor_subsystem.java, Event.java, UML Diagram, Sequence Diagram, test cases
Huzaifa: Floor_subsystem.java, UML Diagram, Sequence Diagram, test cases

-----------------------
version: javase-1.8

------------------------

Files:

ElevatorSystem.java:

    A class to run the elevator system which initializes and runs the threads for the floor subsystem, scheduler, and elevator.
    
FloorSubsystem.java:

    A class that could make requests for elevators events
    
Scheduler.java:

    A class to schedule the events for the elevators. In this iteration it only acts as a communication system between the floor subsystem and elevator. This class is used
    to make sure there is mutual exclusion between the threads and to prevent sychronized access to the data. 
    
Elevator.java:

    A class to execute and request for elevator events.
    
Event.java:

    The blueprint of a data object that contains the details of an elevator event.
   

Setup Instructions:

Zip File:
1) Download and unzip the file containing the code
2) Open up Eclipse and start a new project using the folder containing the code
3) If any configuration needs to be done, such as setting up SDK, then set it up
4) In the class ElevatorSystem, run the main method to see threads executing. 
5) To test the functionality of the system use the SchedulerTest.java, ElevatorTest.java, FloorSubsystemTest.java. Run the main method of 
   the class to test the test cases. 
