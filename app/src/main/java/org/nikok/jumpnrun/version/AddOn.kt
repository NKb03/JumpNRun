package org.nikok.jumpnrun.version

import org.nikok.jumpnrun.core.JumpNRun
import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.net.URL

@Root(name = "addon")
class AddOn {
    @field: Attribute
    lateinit var name: String
        private set

    @field: Attribute
    var maxVersion: Int = -1
        private set
        get() = field.takeIf { it != -1 } ?: error("Max version not initialized")

    @field: ElementList(entry = "version", inline = true)
    lateinit var versions: List<Version>
        private set

    companion object {
        private val serializer: Serializer = Persister()

        fun parse(url: URL): AddOn {
            return serializer.read(AddOn::class.java, url.openStream())
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val url = JumpNRun::class.java.getResource("main.xml")!!
            val addOn = AddOn.parse(url)
            println(addOn)
        }
    }

    override fun toString(): String {
        return "AddOn(name='$name', maxVersion=$maxVersion, versions=$versions)"
    }
}