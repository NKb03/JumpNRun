package org.nikok.jumpnrun.util

import java.io.Serializable
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal fun Random.nextFloat(origin: Float, bound: Float): Float {
    require(bound > origin) { "Bound must be greater than origin" }
    return nextFloat() * (bound - origin) + origin
}

internal fun Random.nextFloat(bound: Float) = nextFloat(0.0f, bound)

internal fun <E> List<E>.randomElement(): E {
    val index = ThreadLocalRandom.current().nextInt(size)
    return get(index)
}


internal fun <T : Any> transient(init: () -> T): ReadOnlyProperty<Any?, T> =
    Transient(init)

private class Transient<T : Any>(private var init: (() -> T)? = null) : ReadOnlyProperty<Any?, T>, Serializable {
    @kotlin.jvm.Transient
    private var _value: T? = null

    private val value: T
        get() = _value ?: init!!().also { _value = it }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}