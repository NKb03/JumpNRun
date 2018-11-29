package org.nikok.jumpnrun.core

import android.content.pm.ActivityInfo
import android.support.annotation.DrawableRes
import kotlin.reflect.KClass

class GameFactory private constructor(
    val gameName: String,
    @DrawableRes val iconRes: Int,
    override val price: Int,
    gameCls: KClass<out Game<*>>,
    @DrawableRes val backgroundRes: Int,
    val screenOrientation: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
) : Buyable() {
    val gameCls = gameCls.java

    fun createGame(): Game<*> {
        return gameCls.newInstance()
    }

    class Builder @PublishedApi internal constructor() {
        lateinit var gameName: String
        @Suppress("RedundantSetter")
        var iconRes: Int = -1
            set(@DrawableRes value) {
                field = value
            }
            get() = if (field == -1) throw IllegalStateException("iconRes not yet set") else field
        var price: Int = -1
            set(value) {
                require(value >= 0)
                field = value
            }
            get() = if (field == -1) throw IllegalStateException("Price not yet set") else field
        lateinit var gameCls: KClass<out Game<*>>
        @Suppress("RedundantSetter")
        var backgroundRes = -1
            set(@DrawableRes value) {
                field = value
            }
            get() = if (field == -1) throw IllegalStateException("backgroundRes not yet set") else field

        var screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        @PublishedApi
        internal fun build() = GameFactory(gameName, iconRes, price, gameCls, backgroundRes, screenOrientation)
    }

    companion object {
        inline operator fun invoke(block: Builder.() -> Unit): GameFactory = Builder().apply(block).build()
    }
}