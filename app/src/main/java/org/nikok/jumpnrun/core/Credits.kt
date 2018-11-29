package org.nikok.jumpnrun.core

import java.io.Serializable

abstract class Credits : Serializable {
    abstract val count: Int

    abstract fun setOnChange(listener: (Int, Int) -> Unit)

    fun canBuy(item: Buyable) = item.price <= count

    class Modifiable(initialCredits: Int) : Credits() {
        private var _onChange: (Int, Int) -> Unit = { _, _ -> }

        override var count: Int = initialCredits
            private set(value) {
                val old = field
                field = value
                _onChange(old, value)
            }

        override fun setOnChange(listener: (old: Int, new: Int) -> Unit) {
            _onChange = listener
        }

        fun buy(price: Int) {
            check(price <= count) { "Not enough money" }
            count -= price
        }

        fun earn(reward: Int) {
            count += reward
        }
    }
}