package org.nikok.jumpnrun.core

import android.animation.TimeAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.constraint.ConstraintSet.*
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import org.nikok.jumpnrun.graphic.GameElement
import org.nikok.jumpnrun.graphic.GameElementView
import org.nikok.jumpnrun.graphic.Point
import org.nikok.jumpnrun.util.Cycler
import org.nikok.jumpnrun.util.transient
import java.io.Serializable
import kotlin.math.absoluteValue

abstract class Game<out R : GameResult> : Serializable {
    abstract val result: R

    var background: Drawable
        get() = root?.background ?: error("Not yet resumed")
        set(value) {
            if (root == null) error("Not yet resumed")
            root!!.background = value
        }

    protected var totalWidth = 0.0f; private set

    protected var totalHeight: Float = 0.0f; private set

    protected var top = 0.0f; private set

    protected var left = 0.0f; private set

    protected val bottom get() = top + totalHeight

    protected val right get() = left + totalWidth

    @Transient
    private var resumed = false

    private val elements = mutableSetOf<GameElement>()

    private var root: GameView? = null

    protected val resources: Resources get() = context.resources

    @Transient
    private lateinit var context: Activity

    @Transient
    private lateinit var jumpNRun: JumpNRun

    internal fun resume(ctx: Activity, main: JumpNRun, w: Float, h: Float): View {
        return if (!resumed) {
            totalWidth = w
            totalHeight = h
            context = ctx
            jumpNRun = main
            val r = GameView(ctx)
            root = r
            initCycle()
            addAllElementsToView()
            onResume()
            resumed = true
            r
        } else root!!
    }

    private val eventHandlers by transient { mutableSetOf<EventHandler>() }

    private val animator: TimeAnimator by transient { TimeAnimator() }

    protected abstract fun onResume()

    fun addEventHandler(handler: EventHandler) {
        eventHandlers.add(handler)
    }

    fun removeEventHandler(handler: EventHandler) {
        eventHandlers.remove(handler)
    }

    fun setInfoBar(view: View) {
        root?.setInfoBar(view) ?: error("Not yet resumed")
    }

    fun setInfoBar(@LayoutRes id: Int): View {
        val v = context.layoutInflater.inflate(id, root, false)
        setInfoBar(v)
        return v
    }

    internal fun pauseLoop() {
        animator.pause()
    }

    internal fun startLoop() {
        animator.start()
    }

    fun addCycler(cycler: Cycler) {
        cyclers.add(cycler)
    }

    fun addCycler(frameRate: Int, action: () -> Unit) {
        addCycler(Cycler(frameRate, action))
    }

    protected fun addElement(gameElement: GameElement) {
        check(gameElement !in elements) { "Game element already added" }
        elements.add(gameElement)
        val v = gameElement.createView(context)
        root?.addElement(v)
    }

    private fun addAllElementsToView() {
        val r = root!!
        for (element in elements) {
            val v = element.view ?: error("Uninitialized view")
            r.addElement(v)
        }
    }

    protected fun removeElement(gameElement: GameElement) {
        check(gameElement in elements) { "Game element cannot be removed" }
        elements.remove(gameElement)
        val v = gameElement.view ?: error("Uninitialized view")
        root?.removeElement(v)
    }

    private fun initCycle() {
        animator.setTimeListener { _, _, deltaTime ->
            for (cycler in cyclers) {
                cycler.elapsed(deltaTime)
            }
        }
    }

    private val cyclers by transient { mutableSetOf<Cycler>() }

    private fun initCycling() {
        animator.setTimeListener { _, _, deltaTime ->
            for (cycler in cyclers) {
                cycler.elapsed(deltaTime)
            }
        }
    }

    fun finish() {
        pauseLoop()
        jumpNRun.playedGame(result)
        context.finish()
    }

    private inner class GameView(context: Context) : ConstraintLayout(context) {
        private var infoBar: View? = null

        private val constraints = ConstraintSet()

        private val elements = RelativeLayout(context)

        fun setInfoBar(view: View) {
            view.measure(0, 0)
            val oldHeight = infoBar?.measuredHeight?.toFloat() ?: 0.0f
            val newHeight = view.measuredHeight.toFloat()
            totalHeight += oldHeight - newHeight
            this@Game.top = newHeight
            infoBar?.let {
                constraints.clear(it.id)
                removeView(infoBar)
            }
            addView(view)
            with(constraints) {
                connect(PARENT_ID, TOP, view.id, TOP)
                connect(view.id, BOTTOM, elements.id, TOP)
            }
            infoBar = view
        }

        fun removeElement(v: View) {
            elements.removeView(v)
        }

        fun addElement(v: View) {
            elements.addView(v)
        }

        private inner class SwipeDetector : GestureDetector.SimpleOnGestureListener() {

            var currentElement: GameElement? = null
            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val from = Point(e1.x, e1.y)
                val to = Point(e2.x, e2.y)
                val distanceX = e2.x - e1.x
                val distanceY = e2.y - e1.y
                val absDistX = distanceX.absoluteValue
                val absDistY = distanceY.absoluteValue
                if (
                    absDistX > absDistY &&
                    absDistX > SWIPE_DISTANCE_THRESHOLD &&
                    velocityX.absoluteValue > SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (distanceX > 0) {
                        onSwipeRight(from, to, velocityX, velocityY, currentElement)
                    } else {
                        onSwipeLeft(from, to, velocityX, velocityY, currentElement)
                    }
                } else if (
                    absDistY > SWIPE_DISTANCE_THRESHOLD &&
                    velocityY.absoluteValue > SWIPE_VELOCITY_THRESHOLD
                ) {
                    if (distanceY > 0) {
                        onSwipeDown(from, to, velocityX, velocityY, currentElement)
                    } else {
                        onSwipeUp(from, to, velocityX, velocityY, currentElement)
                    }
                }
                return true
            }

        }

        private val swipeDetector = SwipeDetector()

        private val gestureDetector = GestureDetector(context, swipeDetector)

        init {
            initEventHandlers()
            addView(elements)
        }

        private fun initEventHandlers() {
            setOnTouchListener { v, event: MotionEvent ->
                val el = (v as? GameElementView)?.element
                handleTouchEvent(el, event)
                true
            }
            setOnClickListener { v ->
                if (v is GameElementView) {
                    handleClickEvent(v)
                }
            }
            setOnLongClickListener { v ->
                if (v is GameElementView) {
                    handleLongClickEvent(v)
                    true
                } else false
            }
        }

        private fun handleTouchEvent(element: GameElement?, event: MotionEvent) {
            swipeDetector.currentElement = element
            gestureDetector.onTouchEvent(event)
            val p = Point(event.x, event.y)
            onTouch(event.actionMasked, p, element)
        }

        private fun handleLongClickEvent(v: GameElementView) {
            onLongClick(v.element)
        }

        private fun handleClickEvent(v: GameElementView) {
            onClick(v.element)
        }
    }

    private fun onClick(element: GameElement) {
        for (handler in eventHandlers) {
            handler.onClick(element)
        }
    }

    private fun onLongClick(element: GameElement) {
        for (handler in eventHandlers) {
            handler.onLongClick(element)
        }
    }

    private fun onTouch(action: Int, p: Point, element: GameElement?) {
        for (handler in eventHandlers) {
            handler.onTouch(action, p, element)
        }
    }

    private fun onSwipeRight(from: Point, to: Point, velocityX: Float, velocityY: Float, element: GameElement?) {
        for (handler in eventHandlers) {
            handler.onSwipeRight(from, to, velocityX, velocityY, element)
        }
    }

    private fun onSwipeLeft(from: Point, to: Point, velocityX: Float, velocityY: Float, element: GameElement?) {
        for (handler in eventHandlers) {
            handler.onSwipeLeft(from, to, velocityX, velocityY, element)
        }
    }

    private fun onSwipeDown(from: Point, to: Point, velocityX: Float, velocityY: Float, element: GameElement?) {
        for (handler in eventHandlers) {
            handler.onSwipeDown(from, to, velocityX, velocityY, element)
        }
    }

    private fun onSwipeUp(from: Point, to: Point, velocityX: Float, velocityY: Float, element: GameElement?) {
        for (handler in eventHandlers) {
            handler.onSwipeUp(from, to, velocityX, velocityY, element)
        }
    }

    private fun checkStarted() {
        check(resumed) { "Game not resumed" }
    }

    companion object {
        private const val SWIPE_DISTANCE_THRESHOLD = 50
        private const val SWIPE_VELOCITY_THRESHOLD = 50
    }
}