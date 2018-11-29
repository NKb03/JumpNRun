package org.nikok.jumpnrun.core

import android.os.Parcel
import java.io.Serializable

abstract class Reward : Serializable {
    abstract val credits: Int

    var consumed = false
        private set

    fun readConsumed(parcel: Parcel) {
        consumed = parcel.readByte() > 0
    }

    fun consume(credits: Credits.Modifiable) {
        check(!consumed) { "Reward already consumed" }
        credits.earn(this.credits)
        consumed = true
    }
}