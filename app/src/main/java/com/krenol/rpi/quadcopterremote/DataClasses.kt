package com.krenol.rpi.quadcopterremote

import com.google.gson.annotations.SerializedName


class DataClasses {
    data class Joystick(
            var offset: Float?,
            var degrees: Float?,
            var rotation: Int?
    ) {

    }

    data class Output(
            var throttle: Int?,
            var joystick: Joystick
    ) {
    }

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
