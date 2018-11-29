package org.nikok.jumpnrun.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import org.nikok.jumpnrun.core.JumpNRun

internal class SlidePagerAdapter(
    fm: FragmentManager,
    jumpNRun: JumpNRun,
    pager: ViewPager
) : FragmentPagerAdapter(fm) {
    private val challengesFragment = ChallengesFragment.newInstance(jumpNRun)

    private val playFragment = PlayFragment.newInstance(jumpNRun)

    private val gamesFragment = GamesFragment.newInstance(jumpNRun)

    init {
        jumpNRun.addCurrentGameListener { old, new ->
            if (old != new) {
                playFragment.displayGame(new)
                pager.currentItem = PLAY
            }
        }
    }

    fun update(index: Int) {
        when (index) {
            CHALLENGES -> challengesFragment.update()
        }
    }

    override fun getItem(pos: Int): Fragment {
        return when (pos) {
            CHALLENGES -> challengesFragment
            PLAY -> playFragment
            GAMES -> gamesFragment
            else -> error("")
        }
    }

    override fun getCount(): Int = 3

    companion object {
        const val CHALLENGES = 0
        const val PLAY = 1
        const val GAMES = 2
    }
}