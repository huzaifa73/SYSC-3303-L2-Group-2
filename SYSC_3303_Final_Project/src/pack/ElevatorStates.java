package pack;

/**
 * Elevator states for state machine
 * @author Blake, Hovish
 *
 *
 * IdleState: Initial State
 * There is currently No Task
 * The elevator is not moving
 * Requests tasks from scheduler
 *
 * moveState:
 * There is a task
 * The elevator is currently not on the target floor
 * Moves to target floor
 * Checks if a new task takes priority
 *
 * destinationState:
 * There is a task
 * The elevator is on the target floor
 * Completes the task
 *
 */
public enum ElevatorStates {
	
	idleState, 
	moveState, 
	destinationState, 

}
