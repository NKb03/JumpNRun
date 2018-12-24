package org.nikok.jumpnrun.core

import android.os.Parcel
import android.os.Parcelable
import org.nikok.jumpnrun.games.EscapeTheSquares
import org.nikok.jumpnrun.version.AddOn
import java.io.Externalizable
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import kotlin.math.max
import kotlin.reflect.KClass

class JumpNRun : Externalizable, Parcelable {
    private val addOns by lazy { mutableMapOf(MAIN_ADDON to 0) }

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

    fun update() {
        for ((url, myVersion) in addOns) {
            val addOn = AddOn.parse(url)
            var maxVersion = 0
            for (version in addOn.versions) {
                val versionNumber = version.versionNumber
                if (versionNumber > myVersion) {
                    for (patch in version.patches(JumpNRun::class.java.classLoader!!))
                        patch.apply(this)
                    if (versionNumber > maxVersion) maxVersion = versionNumber
                }
            }
            addOns[url] = max(maxVersion, myVersion)
        }
    }

    override fun writeExternal(output: ObjectOutput) {
        try {
            with(output) {
                writeObject(_challenges)
                writeObject(_games)
                writeInt(credits.count)
                writeObject(currentGame)
                writeObject(addOns)
            }
        } catch (exc: IOException) {
            exc.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun readExternal(input: ObjectInput) {
        try {
            with(input) {
                val challenges = readObject() as HashMap<Class<Game<*>>, MutableList<Challenge<*>>>
                _challenges.clear()
                _challenges.putAll(challenges)
                val games = readObject() as ArrayList<GameFactory>
                _games.clear()
                _games.addAll(games)
                val count = readInt()
                credits = Credits.Modifiable(count)
                currentGame = readObject() as GameFactory
                addOns.putAll(readObject() as Map<String, Int>)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {}

    companion object CREATOR : Parcelable.Creator<JumpNRun> {
        val INSTANCE = JumpNRun()

        private const val MAIN_ADDON = "main.xml"

        override fun createFromParcel(parcel: Parcel): JumpNRun {
            return INSTANCE
        }

        override fun newArray(size: Int): Array<JumpNRun?> {
            return arrayOfNulls(size)
        }
    }

    override fun describeContents(): Int {
        return 0
    }
}