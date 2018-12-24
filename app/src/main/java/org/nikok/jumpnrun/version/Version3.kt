package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.core.JumpNRun
import org.nikok.jumpnrun.games.BallJump

object Version3 : Patch {
    override fun apply(jumpNRun: JumpNRun) {
        jumpNRun.registerGame(BallJump.FACTORY)
    }
}