package org.nikok.jumpnrun.ui

import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import org.nikok.jumpnrun.R
import org.nikok.jumpnrun.core.Challenge
import org.nikok.jumpnrun.core.JumpNRun

class ChallengesFragment : ListFragment() {
    private val jumpNRun: JumpNRun by parcelableArgument(JUMPNRUN)

    private val challenges get() = jumpNRun.challenges(jumpNRun.currentGame.gameCls.kotlin)

    private lateinit var adapter: GamesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        arguments
        super.onCreate(savedInstanceState)
        adapter = GamesAdapter()
        jumpNRun.addCurrentGameListener { _, _ ->
            adapter = GamesAdapter()
            listAdapter = adapter
        }
        listAdapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.update()
    }

    private inner class GamesAdapter : BaseAdapter() {
        private val views = mutableMapOf<Challenge<*>, View>()

        private val inflater by lazy { layoutInflater }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val challenge = challenges[position]
            val view = convertView.takeIf { it?.id == R.layout.challenge_item }
                ?: inflater.inflate(R.layout.challenge_item, parent, false)
            configureChallengeView(challenge, view)
            configureColor(view, challenge)
            views[challenge] = view
            return view
        }

        fun update() {
            for ((ch, v) in views.entries) {
                configureChallengeView(ch, v)
                configureColor(v, ch)
            }
        }

        @Suppress("DEPRECATION")
        private fun configureColor(view: View, challenge: Challenge<*>) {
            if (challenge.achieved) {
                if (challenge.consumed) {
                    view.background = resources.getDrawable(R.color.consumed_challenge)
                } else {
                    view.background = resources.getDrawable(R.color.achieved_challenge)
                }
            } else {
                view.background = resources.getDrawable(R.color.unachieved_challenge)
            }
        }

        private fun configureChallengeView(challenge: Challenge<*>, view: View) {
            with(view) {
                val rewardView: TextView = findViewById(R.id.rewardView)
                rewardView.text = "${challenge.credits}"
                val nameView: TextView = findViewById(R.id.challenge_name_view)
                nameView.text = challenge.name
                val progressBar: ProgressBar = findViewById(R.id.progressBar)
                progressBar.progress = challenge.progress
                val descriptionView: TextView = findViewById(R.id.descriptionView)
                progressBar.max = challenge.max
                descriptionView.text = challenge.description
                if (challenge.achieved && !challenge.consumed) {
                    setOnClickListener {
                        challenge.consume(jumpNRun.credits)
                        view.background = resources.getDrawable(R.color.consumed_challenge)
                        setOnClickListener { }
                    }
                    setOnHoverListener { _, _ ->
                        view.background.alpha - 10
                        true
                    }
                }
            }
        }

        override fun getItem(position: Int): Challenge<*> = challenges[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = challenges.size
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_challenges, container, false)
    }

    fun update() {
        adapter.update()
    }

    companion object {
        private const val JUMPNRUN = "JUMPNRUN"
        fun newInstance(jumpNRun: JumpNRun) = ChallengesFragment().apply {
            arguments = Bundle().apply {
                putParcelable(JUMPNRUN, jumpNRun)
            }
        }
    }
}
