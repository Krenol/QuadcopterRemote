package com.krenol.rpi.quadcopterremote

import com.google.gson.annotations.SerializedName


class DataClasses {
    data class Joystick(
            var offset: Float?,
            var degrees: Float?,
            var rotation: Int?
    ) {

    }

    data class GPS(
        var altitude: Double?,
        var longitude: Double?,
        var latitude: Double?
    ) {

    }

    data class Output(
            var throttle: Int?,
            var joystick: Joystick,
            var gps: GPS
    ) {
    }

    data class Dummy(
        @SerializedName("gps") val gps: DummyGPS,
        @SerializedName("joystick") val joystick: DummyJoy,
        @SerializedName("throttle") val throttle: Double
    )
    data class DummyGPS(
        @SerializedName("altitude") val altitude: Double?,
        @SerializedName("longitude") val longitude: Double?,
        @SerializedName("latitude") val latitude: Double?
    )
    data class DummyJoy(
        @SerializedName("offset") val offset: Double?,
        @SerializedName("degrees") val degrees: Double?,
        @SerializedName("rotation") val rotation: Double?
    )

    data class Input(
            @SerializedName("angles") val angles: Angles,
            @SerializedName("position") val position: Position,
            @SerializedName("sensors") val sensors: Sensors
    )

    data class Angles(
            @SerializedName("roll") val roll: Float,
            @SerializedName("pitch") val pitch: Float,
            @SerializedName("yaw") val yaw: Float
    )

    data class Position(
            @SerializedName("altitude") val altitude: Float,
            @SerializedName("latitude") val latitude: Float,
            @SerializedName("longitude") val longitude: Float
    )

    data class Sensors(
            @SerializedName("barometric_height") val barometric_height: Double
    )
}
