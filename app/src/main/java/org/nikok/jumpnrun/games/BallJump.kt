package org.nikok.jumpnrun.games

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

class BallJump : Game<BallJump.Result>(), EventHandler {
    private var level: Int = 0

    private val ball = Circle()

    private val obstacles = mutableSetOf<Rect>()

    private var obstacleSpeed: Float = 2.0f

    private val obstacleGenerator by transient {
        Cycler(INITIAL_OBSTACLE_GENERATION_FRAME_RATE) {
            generateNewObstacle()
        }
    }

    private val moveForward by transient {
        Cycler(5) {
            moveForward()
            checkCollisions()
        }
    }

    private var lost: Boolean = false

    private fun checkCollisions() {
        val itr = obstacles.iterator()
        while (itr.hasNext()) {
            val el = itr.next()
            if (overlaps(ball, el)) {
                if (el.color == ball.color) {
                    itr.remove()
                    removeElement(el)
                    ball.color = colorGenerator.randomColor()
                } else {
                    ball.color = Color.RED
                    lost = true
                    Thread.sleep(2000)
                    finish()
                }
            }
        }
    }

    private val moveBallDown by transient {
        Cycler(10) {
            moveBallDown()
        }
    }

    private val newLevel: Cycler by transient {
        Cycler(5000) {
            newLevel()
        }
    }

    private val jumpUpAnimator: Animator get() = createJumpAnimator()
    private fun createJumpAnimator(): ObjectAnimator {
        return ObjectAnimator.ofFloat(
            ball,
            "y",
            ball.y,
            ball.y - ball.height * 3
        ).also { an ->
            an.duration = JUMP_DURATION
            an.setInterpolator { f -> f.pow(0.5f) }
            an.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                    ballIsJumping = true
                }

                override fun onAnimationEnd(animation: Animator?) {
                    ballIsJumping = false
                }

                override fun onAnimationCancel(animation: Animator?) {
                    ballIsJumping = false
                }

                override fun onAnimationStart(animation: Animator?) {
                    ballIsJumping = true
                }
            })
        }
    }

    private var ballIsJumping = false

    private fun moveBallDown() {
        if (ballIsJumping) return
        if (ball.y + ball.height * 2 >= totalHeight) return
        val preventingObstacle = obstacles.find { it.upperSide.intersects(ball) }
        if (preventingObstacle != null) {
            ball.y = preventingObstacle.y
        } else {
            ball.y += 5.0f
        }
    }

    private fun newLevel() {
        level++
        val v = (level * 5.0f).pow(0.5f)
        obstacleGenerator.frameRate = (INITIAL_OBSTACLE_GENERATION_FRAME_RATE / v).toInt()
        obstacleSpeed = v
    }

    private fun moveForward() {
        val itr = obstacles.iterator()
        while (itr.hasNext()) {
            val obs = itr.next()
            obs.x -= obstacleSpeed
            if (obs.x + obs.width <= 0.0f) {
                itr.remove()
                removeElement(obs)
            }
        }
    }

    private val random get() = ThreadLocalRandom.current()

    private fun generateNewObstacle() {
        val rect = Rect().apply {
            height = totalHeight / LANES
            width = random.nextFloat(totalWidth / 10, totalWidth / 5)
            val lane = random.nextInt(LANES)
            x = totalWidth
            y = lane * totalHeight / LANES
            color = colorGenerator.randomColor()
        }
        addElement(rect)
        obstacles.add(rect)
    }

    override val result: Result
        get() = Result(0)

    override fun onSwipeUp(from: Point, to: Point, velocityX: Float, velocityY: Float, startElement: GameElement?) {
        if (!ballIsJumping && !lost) {
            jumpUpAnimator.start()
        }
    }

    override fun onResume() {
        initBall()
        addCycler(obstacleGenerator)
        addCycler(moveForward)
        addCycler(newLevel)
        addCycler(moveBallDown)
        background = ColorDrawable(Color.BLACK)
        addEventHandler(this)
    }

    private fun initBall() {
        with(ball) {
            color = colorGenerator.randomColor()
            radius = totalHeight * BALL_HEIGHT_FACTOR
            x = totalWidth / 7
            y = totalHeight - height * 2
        }
        addElement(ball)
    }

    private val colorGenerator = ColorGenerator()

    data class Result(val points: Int) : GameResult(BallJump::class, points)

    companion object {
        private const val BALL_HEIGHT_FACTOR = 0.05f

        val FACTORY = GameFactory {
            gameCls = BallJump::class
            backgroundRes = R.drawable.baum
            iconRes = R.drawable.baum
            screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            price = 1000
            gameName = "Ball Jump"
        }

        private const val LANES = 5
        private const val INITIAL_OBSTACLE_GENERATION_FRAME_RATE: Int = 5000
        private const val JUMP_DURATION: Long = 300
    }
}