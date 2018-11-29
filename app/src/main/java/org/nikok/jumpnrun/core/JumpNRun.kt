package org.nikok.jumpnrun.core

import android.os.Parcel
import android.os.Parcelable
import org.nikok.jumpnrun.games.EscapeTheSquares
import org.nikok.jumpnrun.version.AddOn
import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput
import java.net.URL
import kotlin.reflect.KClass

class JumpNRun : Externalizable, Parcelable {
    private val addOns by lazy { mutableMapOf(MAIN_ADDON_URL to 0) }

    private val _challenges: MutableMap<Class<out Game<*>>, MutableList<Challenge<*>>> = HashMap()

    private val _games: MutableList<GameFactory> = ArrayList()

    var credits: Credits.Modifiable = Credits.Modifiable(1000); private set

    var currentGame: GameFactory = EscapeTheSquares.FACTORY
        set(value) {
            val old = field
            field = value
            notifyCurrentGameChanged(old, value)
        }

    fun addCurrentGameListener(listener: (old: GameFactory, new: GameFactory) -> Unit) {
        currentGameListeners.add(listener)
    }

    private fun notifyCurrentGameChanged(new: GameFactory, old: GameFactory) {
        currentGameListeners.forEach { it.invoke(old, new) }
    }

    private val currentGameListeners = mutableListOf<(GameFactory, GameFactory) -> Unit>()

    fun challenges(gameCls: KClass<out Game<*>>): List<Challenge<*>> =
        _challenges.getOrPut(gameCls.java) { mutableListOf() }

    val games: List<GameFactory> get() = _games

    fun <R : GameResult, G : Game<R>> registerChallenge(gameCls: KClass<G>, challenge: Challenge<R>) {
        _challenges.getOrPut(gameCls.java) { mutableListOf() }.add(challenge)
    }

    fun registerGame(factory: GameFactory) {
        _games.add(factory)
    }

    fun playedGame(result: GameResult) {
        for (challenge in challenges(result.gameCls)) {
            @Suppress("UNCHECKED_CAST")
            challenge as Challenge<GameResult>
            challenge.playedGame(result)
        }
        credits.earn(result.credits)
    }

    override fun writeExternal(output: ObjectOutput) {
        with(output) {
            writeInt(version)
            writeObject(_challenges)
            writeObject(_games)
            writeInt(credits.count)
            writeObject(currentGame)
            writeObject(addOns)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun readExternal(input: ObjectInput) {
        with(input) {
            version = readInt()
            val challenges = readObject() as HashMap<Class<Game<*>>, MutableList<Challenge<*>>>
            _challenges.clear()
            _challenges.putAll(challenges)
            val games = readObject() as ArrayList<GameFactory>
            _games.clear()
            _games.addAll(games)
            val count = readInt()
            credits = Credits.Modifiable(count)
            currentGame = readObject() as GameFactory
            addOns.putAll(readObject() as Map<URL, Int>)
        }
    }

    private var version: Int = 0

    fun update() {
        for ((url, myVersion) in addOns) {
            val addOn = AddOn.parse(url)
            val maxVersion = addOn.maxVersion
            if (myVersion >= maxVersion) break
            for (version in addOn.versions) {
                val versionNumber = version.versionNumber
                if (versionNumber in (myVersion + 1)..maxVersion) {
                    for (patch in version.patches(JumpNRun::class.java.classLoader!!))
                        patch.apply(this)
                }
            }
            addOns[url] = maxVersion
        }
    }

    companion object CREATOR : Parcelable.Creator<JumpNRun> {
        val INSTANCE = JumpNRun()

        private val MAIN_ADDON_URL = JumpNRun::class.java.getResource("main.xml") ?: error("Main addon not found")

        override fun createFromParcel(parcel: Parcel): JumpNRun {
            return INSTANCE
        }

        override fun newArray(size: Int): Array<JumpNRun?> {
            return arrayOfNulls(size)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(version)
    }

    override fun describeContents(): Int {
        return 0
    }
}