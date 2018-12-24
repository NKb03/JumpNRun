package org.nikok.jumpnrun.games

import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent.*
import android.view.View
import android.view.animation.RotateAnimation
import android.widget.TextView
import kotlinx.android.synthetic.main.infobar_ets.view.*
import org.nikok.jumpnrun.R
import org.nikok.jumpnrun.core.EventHandler
import org.nikok.jumpnrun.core.Game
import org.nikok.jumpnrun.core.GameFactory
import org.nikok.jumpnrun.core.GameResult
import org.nikok.jumpnrun.graphic.*
import org.nikok.jumpnrun.util.ColorGenerator
import org.nikok.jumpnrun.util.Cycler
import org.nikok.jumpnrun.util.nextFloat
import org.nikok.jumpnrun.util.transient
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow


class EscapeTheSquares : Game<EscapeTheSquares.Result>(), EventHandler {
    private val ball = Circle()
    private val obstacles = mutableSetOf<Rect>()

    private val coins: MutableSet<GameElement> = mutableSetOf()

    private val downMovingElements get() = sequenceOf(obstacles, coins)

    @Transient
    private lateinit var levelBar: View

    private var killedObstacles = 0

    private var escapedObstacles = 0

    private var nCoins: Int = 0

    private var credits: Int = 0
        set(value) {
            val old = field
            field = value
            updateCredits(old)
        }

    private fun updateCredits(old: Int) {
        ValueAnimator.ofInt(old, credits).apply {
            duration = 300
            addUpdateListener {
                val value = animatedValue as Int
                levelBar.creditsText.text = "$value"
            }
        }.start()
    }

    override val result
        get() = Result(level, escapedObstacles, killedObstacles, nCoins, credits)

    class Result(
        val level: Int,
        val escapedSquares: Int,
        val killedObstacles: Int,
        val coins: Int,
        credits: Int
    ) :
        GameResult(EscapeTheSquares::class, credits)

    private fun checkObstacleCollisions() {
        val itr = obstacles.iterator()
        while (itr.hasNext()) {
            val el = itr.next()
            if (overlaps(ball, el)) {
                if (el.color == ball.color) {
                    itr.remove()
                    removeElement(el)
                    ball.color = colorGenerator.randomColor()
                    killedObstacles++
                    credits += 5
                } else {
                    ball.color = Color.RED
                    println("Making ball red")
                    lost()
                }
            }
        }
    }

    private fun checkCoinCollisions() {
        val itr = coins.iterator()
        while (itr.hasNext()) {
            val coin = itr.next()
            if (collidesWithBall(coin)) {
                itr.remove()
                removeElement(coin)
                nCoins++
                credits += 5
            }
        }
    }

    private fun collidesWithBall(coin: GameElement): Boolean {
        val r1 = coin.width / 2
        val x1 = coin.x + r1
        val y1 = coin.y + r1
        val (x2, y2) = ball.center
        return overlaps(x1, y1, x2, y2, r1, ball.radius)
    }

    private fun lost() {
        finish()
    }

    private var lastTouchX = Float.NaN

    override fun onTouch(action: Int, point: Point, element: GameElement?) {
        when (action) {
            ACTION_DOWN -> {
                lastTouchX = point.x
            }
            ACTION_MOVE -> {
                if (lastTouchX != Float.NaN) {
                    val delta = point.x - lastTouchX
                    ball.x = (ball.x + delta).coerceIn(0.0f, totalWidth - ball.radius * 2)
                    lastTouchX = point.x
                }
            }
            ACTION_UP -> {
                lastTouchX = Float.NaN
            }
        }
    }

    private var obstacleSpeed: Float = 2.0f

    private val downMoveCycler by transient {
        Cycler(7) {
            moveDownElements()
            checkObstacleCollisions()
            checkCoinCollisions()
        }
    }

    private val obstacleGenerator by transient {
        Cycler(INITIAL_OBSTACLE_GENERATION_SPEED) {
            generateNewObstacleOrCoin()
        }
    }

    override fun onResume() {
        addCycler(downMoveCycler)
        addLevelCycler()
        addCycler(obstacleGenerator)
        addEventHandler(this)
        background = PaintDrawable(Color.BLACK)
        initInfoBar()
        initBall()
    }

    private fun initInfoBar() {
        levelBar = setInfoBar(R.layout.infobar_ets)
        updateLevelInfoBar()
    }

    private fun updateLevelInfoBar() {
        val levelView = levelBar.findViewById<TextView>(R.id.levelText)
        levelView.text = "$level"
    }

    private fun addLevelCycler() {
        addCycler(5000) {
            level++
            val v = (level * 5.0f).pow(0.5f)
            obstacleGenerator.frameRate = (INITIAL_OBSTACLE_GENERATION_SPEED / v).toInt()
            obstacleSpeed = v
            updateLevelInfoBar()
        }
    }

    private val random by transient { ThreadLocalRandom.current() }

    private var level = 1

    private fun generateNewObstacleOrCoin() {
        if (random.nextBoolean()) {
            val obs = randomRect()
            obstacles.add(obs)
            addElement(obs)
//            eventuallyStartAnimation(obs)
        } else if (random.nextInt(5) == 0) {
            val coin = createCoin()
            coins.add(coin)
            addElement(coin)
        }
    }

    private fun eventuallyStartAnimation(obs: Rect) {
        if (random.nextInt(-10, level) > 0) {
            println("Starting animation")
            val rotate = RotateAnimation(0f, 360f)
            rotate.duration = 10000
            obs.startAnimation(rotate)
        }
    }

    private val coinWidth get() = totalWidth / 10

    private fun createCoin() = DrawableGameElement(R.drawable.coin1, resources).apply {
        width = coinWidth
        height = coinWidth
        x = random.nextFloat(totalWidth - coinWidth)
        y = 0.0f
    }

    private fun randomRect(): Rect = Rect().apply {
        width = random.nextFloat(20.0f, totalWidth / 10)
        height = random.nextFloat(20.0f, totalHeight / 10)
        val maxX = (totalWidth - width)
        x = random.nextFloat(maxX)
        color = colorGenerator.randomColor()
        y = 0.0f
    }

    private fun moveDownElements() {
        for (elements in downMovingElements) {
            val itr = elements.iterator()
            while (itr.hasNext()) {
                val obs = itr.next()
                obs.y += obstacleSpeed
                if (obs.y > totalHeight) {
                    itr.remove()
                    removeElement(obs)
                    if (obs is Rect) {
                        escapedObstacles++
                    }
                }
            }
        }
    }

    private fun initBall() {
        ball.color = colorGenerator.randomColor()
        ball.radius = totalWidth / 20
        ball.cx = totalWidth / 2
        ball.cy = totalHeight / 3 * 2
        addElement(ball)
    }

    private val colorGenerator = ColorGenerator()

    companion object {
        private const val INITIAL_OBSTACLE_GENERATION_SPEED = 700

        val FACTORY = GameFactory {
            gameName = "Escape The Squares"
            price = 0
            gameCls = EscapeTheSquares::class
            iconRes = R.drawable.baum
            backgroundRes = R.drawable.baum
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}
