package org.nikok.jumpnrun.games

import android.animation.ValueAnimator
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.PaintDrawable
import android.view.MotionEvent
import android.view.View
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

class JetpackJoyride : Game<JetpackJoyride.Result>(), EventHandler {
    private var nCoins = 0

    override val result: Result
        get() = Result(nCoins)

    class Result(coins: Int) : GameResult(JetpackJoyride::class, coins)

    private val ball = Circle()

    private val obstacles = mutableSetOf<Rect>()

    private val coins: MutableSet<GameElement> = mutableSetOf()

    private val leftMovingElements get() = sequenceOf(obstacles, coins)

    @Transient
    private lateinit var levelBar: View

    private var killedObstacles = 0

    private var escapedObstacles = 0

    private var credits: Int = 0
        set(value) {
            val old = field
            field = value
            updateCredits(old)
        }

    private fun updateCredits(old: Int) {
        ValueAnimator.ofInt(old, credits).apply {
            duration = CREDIT_UPDATE_DURATION
            addUpdateListener {
                val value = animatedValue as Int
                levelBar.creditsText.text = "$value"
            }
        }.start()
    }

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
                    credits += EAT_SQUARE_EARN
                } else {
                    ball.color = Color.RED
                    finish()
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
                credits += COIN_CREDIT_EARN
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

    private var fingerDown: Boolean = false

    override fun onTouch(action: Int, point: Point, element: GameElement?) {
        when (action) {
            MotionEvent.ACTION_DOWN -> fingerDown = true
            MotionEvent.ACTION_UP -> fingerDown = false
        }
    }

    private var obstacleSpeed: Float = 2.0f

    private val cycler by transient {
        Cycler(FRAME_RATE) {
            leftMoveElements()
            checkObstacleCollisions()
            checkCoinCollisions()
            moveBall()
        }
    }

    private fun moveBall() {
        if (fingerDown) {
            moveBallUp()
        } else {
            moveBallDown()
        }
    }

    private fun moveBallDown() {
        ball.y = (ball.y + MOVE_DOWN_SPEED).coerceAtMost(totalHeight - ball.height)
    }

    private fun moveBallUp() {
        ball.y = (ball.y - MOVE_UP_SPEED).coerceAtLeast(top)
    }

    private val obstacleGenerator by transient {
        Cycler(INITIAL_OBSTACLE_GENERATION_SPEED, ::generateNewObstacleOrCoin)
    }

    override fun onResume() {
        initBall()
        addCycler(cycler)
        startLevelCycling()
        addCycler(obstacleGenerator)
        addEventHandler(this)
        background = PaintDrawable(Color.BLACK)
        initInfoBar()
    }

    private fun initInfoBar() {
        levelBar = setInfoBar(R.layout.infobar_ets)
        updateLevelInfoBar()
    }

    private fun updateLevelInfoBar() {
        val levelView = levelBar.findViewById<TextView>(R.id.levelText)
        levelView.text = "$level"
    }

    private fun startLevelCycling() {
        addCycler(LEVEL_DURATION) {
            level++
            val v = interpolateLevel(level)
            obstacleGenerator.frameRate = (INITIAL_OBSTACLE_GENERATION_SPEED / v).toInt()
            obstacleSpeed = v
            updateLevelInfoBar()
        }
    }

    private val random get() = ThreadLocalRandom.current()

    private var level = 1

    private fun generateNewObstacleOrCoin() {
        if (random.nextBoolean()) {
            val obs = randomRect()
            obstacles.add(obs)
            addElement(obs)
        } else if (random.nextInt(COIN_PROBABILITY) == 0) {
            val coin = createCoin()
            coins.add(coin)
            addElement(coin)
        }
    }

    private val coinWidth get() = totalWidth * COIN_WIDTH_FACTOR

    private fun createCoin() = DrawableGameElement(R.drawable.coin1, resources).apply {
        width = coinWidth
        height = coinWidth
        x = totalWidth
        y = random.nextFloat(totalHeight - height)
    }

    private fun randomRect(): Rect = Rect().apply {
        width = random.nextFloat(20.0f, totalWidth / 10)
        height = random.nextFloat(20.0f, totalHeight / 10)
        x = totalWidth - width
        color = colorGenerator.randomColor()
        y = random.nextFloat(totalHeight - height)
    }

    private fun leftMoveElements() {
        for (elements in leftMovingElements) {
            val itr = elements.iterator()
            while (itr.hasNext()) {
                val el = itr.next()
                el.x -= obstacleSpeed
                if (el.x + el.width <= 0.0f) {
                    itr.remove()
                    removeElement(el)
                    if (el is Rect) {
                        escapedObstacles++
                    }
                }
            }
        }
    }

    private fun initBall() {
        addElement(ball)
        ball.color = colorGenerator.randomColor()
        ball.radius = totalHeight * BALL_RADIUS_FACTOR
        ball.x = 0.0f
        ball.cy = totalHeight / 2
    }

    private val colorGenerator = ColorGenerator()

    companion object {
        private const val INITIAL_OBSTACLE_GENERATION_SPEED = 700

        private const val COIN_PROBABILITY = 5

        private const val MOVE_UP_SPEED = 10

        private const val MOVE_DOWN_SPEED = 5

        private const val COIN_CREDIT_EARN = 5

        private const val CREDIT_UPDATE_DURATION = 300L

        private const val EAT_SQUARE_EARN = 5

        private const val FRAME_RATE = 7

        private fun interpolateLevel(l: Int) = (l * 5.0f).pow(0.5f)

        private const val LEVEL_DURATION = 5000

        private const val COIN_WIDTH_FACTOR = 0.05f

        val FACTORY = GameFactory {
            gameName = "Jetpack Joyride"
            gameCls = JetpackJoyride::class
            price = 1000
            iconRes = R.drawable.baum
            backgroundRes = R.drawable.baum
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }
        private const val BALL_RADIUS_FACTOR = 0.04f
    }
}