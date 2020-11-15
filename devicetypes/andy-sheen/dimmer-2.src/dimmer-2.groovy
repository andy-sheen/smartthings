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

    tiles(scale: 2) {

        // Multi Tile:
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL", range:"(0..100)") {
                attributeState "level", action:"setLevel"
            }
        }

        // Instantaneous Power:
        valueTile("instMode", "device.dispPower", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Now:', action:"refresh", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_refresh.png"
        }
        valueTile("power", "device.dispPower", decoration: "flat", width: 2, height: 1) {
            state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
        }

        // Accumulated Energy:
        valueTile("energyLastReset", "device.energyLastReset", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Since:  ${currentValue}', action:"resetEnergy", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_stopwatch_reset.png"
        }
        valueTile("energy", "device.dispEnergy", width: 2, height: 1) {
            state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
        }

        // Other Tiles:
        standardTile("nightmode", "device.nightmode", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}', action:"toggleNightmode", icon:"st.Weather.weather4"
        }
        valueTile("scene", "device.scene", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Scene: ${currentValue}'
        }
        standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }
        standardTile("syncPending", "device.syncPending", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Sync Pending', action:"sync", backgroundColor:"#FF6600"
            state "0", label:'Synced', action:"", backgroundColor:"#79b821"
        }
        standardTile("fault", "device.fault", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue} Fault', action:"clearFault", backgroundColor:"#FF6600", icon:"st.secondary.tools"
            state "clear", label:'${currentValue}', action:"", backgroundColor:"#79b821", icon:""
        }
        standardTile("test", "device.power", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Test', action:"test"
        }

        // Tile Layouts:
        main(["switch"])
        details([
            "switch",
            "instMode","power",
            "nightmode",
            "energyLastReset","energy",
            "scene",
            //"refresh",
            //"test",
            "syncPending",
            "fault"
        ])
    }

    preferences {

        section { // GENERAL:
            input (
                type: "paragraph",
                element: "paragraph",
                title: "GENERAL:",
                description: "General device handler settings."
            )

            input (
                name: "configLoggingLevelIDE",
                title: "IDE Live Logging Level: Messages with this level and higher will be logged to the IDE.",
                type: "enum",
                options: [
                    "0" : "None",
                    "1" : "Error",
                    "2" : "Warning",
                    "3" : "Info",
                    "4" : "Debug",
                    "5" : "Trace"
                ],
//                defaultValue: "3", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configLoggingLevelDevice",
                title: "Device Logging Level: Messages with this level and higher will be logged to the logMessage attribute.",
                type: "enum",
                options: [
                    "0" : "None",
                    "1" : "Error",
                    "2" : "Warning"
                ],
//                defaultValue: "2", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configSyncAll",
                title: "Force Full Sync: All device parameters, association groups, and protection settings will " +
                "be re-sent to the device. This will take several minutes and you may need to press the 'sync' " +
                "tile a few times.",
                type: "boolean",
//                defaultValue: false, // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configProactiveReports",
                title: "Proactively Request Reports: Additonal requests for status reports will be made. " +
                "Use only if status reporting is unreliable.",
                type: "boolean",
//                defaultValue: false, // iPhone users can uncomment these lines!
                required: true
            )
        }

        section { // PROTECTION:
            input type: "paragraph",
                element: "paragraph",
                title: "PROTECTION:",
                description: "Prevent unintentional control (e.g. by a child) by disabling the physical switches and/or RF control."

            input (
                name: "configProtectLocal",
                title: "Local Protection: Applies to physical switches:",
                type: "enum",
                options: [
                    "0" : "Unprotected",
                    //"1" : "Protection by sequence", // Not supported by Fibaro Dimmer 2.
                    "2" : "No operation possible"
                ],
//                defaultValue: "0", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configProtectRF",
                title: "RF Protection: Applies to Z-Wave commands sent from hub or other devices:",
                type: "enum",
                options: [
                    "0" : "Unprotected",
                    "1" : "No RF control"//,
                    //"2" : "No RF response" // Not supported by Fibaro Dimmer 2.
                ],
//                defaultValue: "0", // iPhone users can uncomment these lines!
                required: true
            )

        }

        section { // NIGHTMODE:
            input type: "paragraph",
                element: "paragraph",
                title: "NIGHTMODE:",
                description: "Nightmode forces the dimmer to switch on at a specific level (e.g. low-level during the night).\n" +
                    "Nightmode can be enabled/disabled manually using the new Nightmode tile, or scheduled below."

            input type: "number",
                name: "configNightmodeLevel",
                title: "Nightmode Level: The dimmer will always switch on at this level when nightmode is enabled.",
                range: "1..100",
//                defaultValue: "10", // iPhone users can uncomment these lines!
                required: true

            input type: "boolean",
                name: "configNightmodeForce",
                title: "Force Nightmode: If the dimmer is on when nightmode is enabled, the Nightmode Level is applied immediately " +
                    "(otherwise it's only applied next time the dimmer is switched on).",
//                defaultValue: true, // iPhone users can uncomment these lines!
                required: true

            input type: "time",
                name: "configNightmodeStartTime",
                title: "Nightmode Start Time: Nightmode will be enabled every day at this time.",
                required: false

            input type: "time",
                name: "configNightmodeStopTime",
                title: "Nightmode Stop Time: Nightmode will be disabled every day at this time.",
                required: false
        }

        generatePrefsParams()

        generatePrefsAssocGroups()

    }
}
