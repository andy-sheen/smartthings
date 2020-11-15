metadata {
    definition (name: "Fibaro Dimmer 2", namespace: "andy-sheen", author: "David Lomas") {
        capability "Actuator"
        capability "Switch"
        capability "Switch Level"
        capability "Light"
        capability "Sensor"
        capability "Power Meter"
        capability "Energy Meter"
        capability "Polling"
        capability "Refresh"

        // Custom (Virtual) Capabilities:
        //capability "Fault"
        //capability "Logging"
        //capability "Scene Controller"

        // Standard (Capability) Attributes:
        attribute "switch", "string"
        attribute "level", "number"
        attribute "power", "number"
        attribute "energy", "number"

        // Custom Attributes:
        attribute "fault", "string"             // Indicates if the device has any faults. 'clear' if no active faults.
        attribute "logMessage", "string"        // Important log messages.
        attribute "energyLastReset", "string"   // Last time that Accumulated Engergy was reset.
        attribute "syncPending", "number"       // Number of config items that need to be synced with the physical device.
        attribute "nightmode", "string"         // 'Enabled' or 'Disabled'.
        attribute "scene", "number"             // ID of last-activated scene.

        // Display Attributes:
        // These are only required because the UI lacks number formatting and strips leading zeros.
        attribute "dispPower", "string"
        attribute "dispEnergy", "string"

        // Custom Commands:
        command "reset"
        command "resetEnergy"
        command "enableNightmode"
        command "disableNightmode"
        command "toggleNightmode"
        command "clearFault"
        command "sync"
        command "test"

        // Fingerprints (new format):
        fingerprint mfr: "010F", prod: "0102", model: "1000"
        fingerprint type: "1101", mfr: "010F", cc: "5E,86,72,59,73,22,31,32,71,56,98,7A"
        fingerprint type: "1101", mfr: "010F", cc: "5E,86,72,59,73,22,31,32,71,56,98,7A", sec: "20,5A,85,26,8E,60,70,75,27", secOut: "2B"
    }

}
