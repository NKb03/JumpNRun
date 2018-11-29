package org.nikok.jumpnrun.control

import android.content.Context
import org.nikok.jumpnrun.core.JumpNRun
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ContextDataSaver(private val context: Context) : DataSaver {
    override fun update(jumpNRun: JumpNRun) {
        if (isFirstUse() || NEVER_SAVE) {
            jumpNRun.update()
            saveData(jumpNRun)
        } else {
            loadData(jumpNRun)
            jumpNRun.update()
        }
    }

    private fun isFirstUse() = !context.filesDir.list().contains(DATA_FILE)

    override fun loadData(jumpNRun: JumpNRun) {
        val input = context.openFileInput(DATA_FILE)
        val ios = ObjectInputStream(input)
        ios.use { jumpNRun.readExternal(it) }
    }

    override fun saveData(jumpNRun: JumpNRun) {
        val output = context.openFileOutput(DATA_FILE, Context.MODE_PRIVATE)
        val oos = ObjectOutputStream(output)
        oos.use { jumpNRun.writeExternal(it) }
    }

    companion object {
        private const val DATA_FILE = "data.ser"

        private const val NEVER_SAVE = false
    }
}