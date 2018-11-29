package org.nikok.jumpnrun.core

import java.io.Serializable

abstract class Buyable : Serializable {
    fun buyFrom(credits: Credits.Modifiable) {
        check(!bought) { "Already Bought" }
        credits.buy(price)
        bought = true
    }

    var bought = false
        private set

    abstract val price: Int
}