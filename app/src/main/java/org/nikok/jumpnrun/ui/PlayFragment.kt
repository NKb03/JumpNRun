package org.nikok.jumpnrun.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.nikok.jumpnrun.R
import org.nikok.jumpnrun.core.GameFactory
import org.nikok.jumpnrun.core.JumpNRun

class PlayFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        displayGame(main.currentGame)
    }

    fun displayGame(game: GameFactory) {
        try {
            view!!.background = resources.getDrawable(game.backgroundRes)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val main: JumpNRun by parcelableArgument(JUMPNRUN)

    companion object {
        private const val JUMPNRUN: String = "JUMPNRUN"

        fun newInstance(main: JumpNRun) = PlayFragment().apply {
            arguments = Bundle().apply {
                putParcelable(JUMPNRUN, main)
            }
        }
    }
}