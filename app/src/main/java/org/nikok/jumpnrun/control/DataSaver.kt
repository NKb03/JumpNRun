package org.nikok.jumpnrun.control

import org.nikok.jumpnrun.core.JumpNRun

interface DataSaver {
    fun loadData(jumpNRun: JumpNRun)

    fun saveData(jumpNRun: JumpNRun)

    fun update(jumpNRun: JumpNRun)
}