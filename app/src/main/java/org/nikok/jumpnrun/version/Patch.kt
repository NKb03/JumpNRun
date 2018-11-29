package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.core.JumpNRun

abstract class Patch {
    abstract fun apply(jumpNRun: JumpNRun)
}