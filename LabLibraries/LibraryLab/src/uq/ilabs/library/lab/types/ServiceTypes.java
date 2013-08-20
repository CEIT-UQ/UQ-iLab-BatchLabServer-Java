/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uq.ilabs.library.lab.types;

/**
 *
 * @author uqlpayne
 */
public enum ServiceTypes {

    Soap(0),
    Rest(1),
    Unknown(-1);
    //
    private static final ServiceTypes[] TYPES = {
        Soap,
        Rest,
        Unknown
    };
    //
    private static final String[] STRINGS = {
        "SOAP",
        "REST",
        "UNKNOWN"
    };
    //
    //<editor-fold defaultstate="collapsed" desc="Properties">
    private final int value;

    public int getValue() {
        return value;
    }
    //</editor-fold>

    /**
     * Constructor
     *
     * @param value
     */
    private ServiceTypes(int value) {
        this.value = value;
    }

    /**
     *
     * @param value
     * @return ServiceTypes
     */
    public static ServiceTypes ToType(int value) {
        /*
         * Search for the value
         */
        for (ServiceTypes serviceType : ServiceTypes.values()) {
            if (serviceType.getValue() == value) {
                return serviceType;
            }
        }

        /*
         * Value not found
         */
        return Unknown;
    }

    /**
     *
     * @param value
     * @return ServiceTypes
     */
    public static ServiceTypes ToType(String value) {
        if (value != null) {
            for (ServiceTypes serviceType : TYPES) {
                if (serviceType.toString().equals(value)) {
                    return serviceType;
                }
            }
        }
        return Unknown;
    }

    @Override
    public String toString() {
        return STRINGS[this.ordinal()];
    }
}
