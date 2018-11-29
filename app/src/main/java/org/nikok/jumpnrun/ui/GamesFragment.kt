package org.nikok.jumpnrun.ui


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import org.nikok.jumpnrun.R
import org.nikok.jumpnrun.core.GameFactory
import org.nikok.jumpnrun.core.JumpNRun

@Suppress("UNCHECKED_CAST")
internal class GamesFragment : ListFragment() {
    private val main: JumpNRun by parcelableArgument(JUMPNRUN)

    private val games get() = main.games

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = GamesAdapter()
    }

    private inner class GamesAdapter : BaseAdapter() {
        private val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val game = games[position]
            return gameView(convertView, parent, game)
        }

        private fun gameView(
            convertView: View?,
            parent: ViewGroup,
            game: GameFactory
        ): View {
            val view = convertView ?: inflater.inflate(R.layout.game_item, parent, false)
            val nameView: TextView = view.findViewById(R.id.game_name_view)
            nameView.text = game.gameName
            val icon: ImageView = view.findViewById(R.id.game_icon)
            icon.setImageResource(game.iconRes)
            val btn = view.findViewById<Button>(R.id.play_or_buy_game)
            configureButton(btn, game)
            return view
        }

        private fun configureButton(button: Button, game: GameFactory) {
            button.text = game.gameName
            if (game.bought) configureButtonGameBought(button, game)
            else configureButtonGameNotBought(button, game)
        }

        @SuppressLint("SetTextI18n")
        private fun configureButtonGameNotBought(button: Button, game: GameFactory) {
            button.text = "Buy for ${game.price}"
            button.setOnClickListener {
                if (!main.credits.canBuy(game)) {
                    AlertDialog.Builder(context).apply {
                        setTitle("Cannot buy $game")
                    }.show()
                } else {
                    game.buyFrom(main.credits)
                    configureButton(button, game)
                }
            }
        }

        private fun configureButtonGameBought(button: Button, game: GameFactory) {
            button.text = getText(R.string.play)
            button.setCompoundDrawables(null, null, null, null)
            button.setOnClickListener {
                main.currentGame = game
            }
        }

        override fun getItem(position: Int): GameFactory = games[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = games.size
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(jumpNRun: JumpNRun) =
            GamesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(JUMPNRUN, jumpNRun)
                }
            }

        private const val JUMPNRUN: String = "JUMPNRUN"
    }
}
