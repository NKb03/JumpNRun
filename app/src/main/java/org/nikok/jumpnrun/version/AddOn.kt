package org.nikok.jumpnrun.version

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister
import java.net.URL

@Element(name = "addon")
data class AddOn(
    @Attribute(name = "name") val name: String,
    @Attribute(name = "maxVersion") val maxVersion: Int,
    @Attribute(name = "version") val versions: List<Version>
) {
    private constructor() : this("", -1, emptyList())

    companion object {
        private val serializer: Serializer = Persister()

        fun parse(url: URL): AddOn {
            return serializer.read(AddOn::class.java, url.toExternalForm())
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val url = AddOn::class.java.getResource("main.xml")!!
            val addOn = AddOn.parse(url)
            println(addOn)
        }
    }
}