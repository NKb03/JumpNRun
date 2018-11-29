package org.nikok.jumpnrun.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Parcelable
import android.support.v4.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.cast

internal class SerializableArgumentDelegate<T : Serializable>(private val key: String, private val cls: KClass<T>) :
    ReadOnlyProperty<Fragment, T> {
    private var cached: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        cached?.let { return it }
        val args = thisRef.arguments ?: throw NoSuchElementException("No args passed")
        val value = args.getSerializable(key) ?: throw RuntimeException("No such argument $key")
        val casted = cls.cast(value)
        cached = casted
        return casted
    }
}

internal inline fun <reified T : Serializable> serializableArgument(name: String): ReadOnlyProperty<Fragment, T> =
    SerializableArgumentDelegate(name, T::class)

internal class ParcelableArgumentDelegate<T : Parcelable>(private val key: String) :
    ReadOnlyProperty<Fragment, T> {
    private var cached: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        cached?.let { return it }
        val args = thisRef.arguments ?: throw NoSuchElementException("No args passed")
        val value = args.getParcelable<T>(key) ?: throw NoSuchElementException("No key $key")
        cached = value
        return value
    }
}

internal fun <T : Parcelable> parcelableArgument(name: String): ReadOnlyProperty<Fragment, T> =
    ParcelableArgumentDelegate(name)

internal fun Context.showAlert(build: AlertDialog.Builder.() -> Unit) {
    AlertDialog.Builder(this).apply(build).show()
}

internal inline fun <reified T : Serializable> serializableExtra(name: String): ReadOnlyProperty<Activity, T> =
    SerializableExtraDelegate(name, T::class)

internal class SerializableExtraDelegate<T : Any>(private val name: String, private val cls: KClass<T>) :
    ReadOnlyProperty<Activity, T> {
    private var cached: T? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        cached?.let { return it }
        val value = thisRef.intent.getSerializableExtra(name) ?: throw NoSuchElementException("No such extra $name")
        val casted = cls.cast(value)
        cached = casted
        return casted
    }
}

internal fun <T : Parcelable> parcelableExtra(name: String): ReadOnlyProperty<Activity, T> =
    IntentExtraDelegate(name)

internal class IntentExtraDelegate<T : Parcelable>(private val name: String) : ReadOnlyProperty<Activity, T> {
    private var cached: T? = null

    override fun getValue(thisRef: Activity, property: KProperty<*>): T {
        cached?.let { return it }
        val value = thisRef.intent.getParcelableExtra<T>(name) ?: throw NoSuchElementException("No such extra $name")
        cached = value
        return value
    }
}