# SYSC-3303-L2-Group-2
SYSC 3303 - L2 - Group 2

Files:
ElevatorSystem.java:
    A class to run the elevator system which initializes and runs the threads for the floor subsystem, scheduler, and elevator.
FloorSubsystem.java:
    A class that could make requests for elevators events
Scheduler.java:
    A class to schedule the events for the elevators. In this iteration it only acts as a communication system between the floor subsystem and elevator
Elevator.java:
    A class to execute and request for elevator events.
Event.java:
    The blueprint of a data object that contains the details of an elevator event.
EventList.java:
    A class to keep a the list of events for the scheduler with mutal exclusion to prevent sychronized access to the data

Setup Instructions:
