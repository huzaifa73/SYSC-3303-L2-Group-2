package pack;

/**
 * Elevator states for state machine
 * @author Blake, Hovish
 *
 *
 * State 0: Initial State
 * There is currently No Task
 * The elevator is not moving
 * Requests tasks from scheduler
 *
 * State 1:
 * There is a task
 * The elevator is currently not on the target floor
 * Moves to target floor
 * Checks if a new task takes priority
 *
 * State 2:
 * There is a task
 * The elevator is on the target floor
 * Completes the task
 *
 */
public enum ElevatorStates {
	
	State0, 
	State1, 
	State2, 

}
