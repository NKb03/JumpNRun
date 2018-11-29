package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.core.JumpNRun
import org.nikok.jumpnrun.games.EscapeTheSquares

object Version2 : Patch {
    override fun apply(jumpNRun: JumpNRun) {
        EscapeTheSquares.FACTORY.buyFrom(jumpNRun.credits)
    }
}