package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public enum StatusCodes {

    /**
     * Ready to execute
     */
    Ready(0),
    /**
     * Waiting in the execution queue
     */
    Waiting(1),
    /**
     * Currently running
     */
    Running(2),
    /**
     * Completely normally
     */
    Completed(3),
    /**
     * Terminated with errors
     */
    Failed(4),
    /**
     * Cancelled by user before execution had begun
     */
    Cancelled(5),
    /**
     * Unknown experimentID
     */
    Unknown(6),
    /**
     * Invalid experiment
     */
    Invalid(7);

    private StatusCodes(int value) {
        this.value = value;
    }
    private final int value;

    public static StatusCodes ToStatusCode(int value)
    {
        switch (value) {
            case 0: return Ready;
            case 1: return Waiting;
            case 2: return Running;
            case 3: return Completed;
            case 4: return Failed;
            case 5: return Cancelled;
            case 6: return Unknown;
            case 7: return Invalid;
            default: return Unknown;
        }
    }
}
