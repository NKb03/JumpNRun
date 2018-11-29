package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.core.JumpNRun

interface Patch {
    fun apply(jumpNRun: JumpNRun)
}