package pack;

/**
 * A class for use by FloorSubsystem when it is reading the inputfile for generating 
 * inserted errors 
 * 
 * @author Cameron Maccoll
 * @version 1.0
 */

public enum SystemError {
    NO_ERROR (0),
    DOOR_FAULT (1),
    TRAVEL_FAULT (2);

    public final int errorCode;

    private SystemError(int inputCode) {
        this.errorCode = inputCode;
    }
}
