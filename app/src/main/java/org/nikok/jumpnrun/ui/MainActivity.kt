package org.nikok.jumpnrun.ui

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.nikok.jumpnrun.R
import org.nikok.jumpnrun.control.ContextDataSaver
import org.nikok.jumpnrun.core.GameFactory
import org.nikok.jumpnrun.core.JumpNRun
import org.nikok.jumpnrun.ui.SlidePagerAdapter.Companion.PLAY

internal class MainActivity : AppCompatActivity() {
    private val jumpNRun = JumpNRun.INSTANCE

    private val saver = ContextDataSaver(this)

    private fun play(gameFactory: GameFactory) {
        val game = gameFactory.createGame()
        val activityCls = GameActivity.withScreenOrientation(gameFactory.screenOrientation)
        val intent = Intent(applicationContext, activityCls).apply {
            putExtra(GameActivity.GAME, game)
            putExtra(GameActivity.JUMPNRUN, jumpNRun as Parcelable)
        }
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainView()
        saver.update(jumpNRun)
        initTabs()
        initViewPager()
        initMoneyDisplay()
    }

    private fun initMoneyDisplay() {
        val credits = jumpNRun.credits
        val count = credits.count
        money_text.text = "$count"
        credits.setOnChange { old, new ->
            ValueAnimator.ofInt(old, new).apply {
                duration = 1000
                addUpdateListener {
                    val value = animatedValue as Int
                    money_text.text = "$value"
                }
            }.start()
        }
    }

    private fun showMainView() {
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        askIfClose()
    }

    private fun askIfClose() {
        showAlert {
            setTitle("Close App?")
            setMessage("Do you really want to close this App?")
            setPositiveButton("Yes close!") { _, _ ->
                finish()
            }
            setNegativeButton("Do not quit!") { _, _ -> }
        }
    }

    override fun onStop() {
        super.onStop()
        saver.saveData(jumpNRun)
    }

    private fun initViewPager() {
        val p = pager
        val adapter = SlidePagerAdapter(supportFragmentManager, jumpNRun, p)
        p.adapter = adapter
        val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(page: Int) {
                val id = tabIdxToNavigIDs[page] ?: error("No item id for page $page")
                navigation.selectedItemId = id
                adapter.update(page)
            }
        }
        p.currentItem = PLAY
        p.addOnPageChangeListener(pageChangeListener)
    }

    private fun initTabs() {
        val tabListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val id = item.itemId
            val idx = navigIDsToTabIdx[id] ?: error("No tab index for $item with id $id")
            pager.setCurrentItem(idx, true)
            true
        }
        navigation.setOnNavigationItemSelectedListener(tabListener)
        navigation.selectedItemId = tabIdxToNavigIDs[PLAY]!!
    }

    @Suppress("UNUSED_PARAMETER")
    fun playButtonPressed(view: View) {
        play(jumpNRun.currentGame)
    }

    companion object {
        private val navigIDsToTabIdx = mapOf(
            R.id.navigation_home to 0,
            R.id.navigation_dashboard to 1,
            R.id.navigation_notifications to 2
        )

        private val tabIdxToNavigIDs = navigIDsToTabIdx.reverse()

        private fun Map<Int, Int>.reverse() = entries.associate { (k, v) -> v to k }
    }
}