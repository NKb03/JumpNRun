package org.nikok.jumpnrun.version

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.Serializer
import org.simpleframework.xml.core.Persister

@Root(name = "addon")
class AddOn {
    @field: Attribute
    lateinit var name: String
        private set

    @field: ElementList(entry = "version", inline = true)
    lateinit var versions: List<Version>
        private set

    companion object {
        private val serializer: Serializer = Persister()

        fun parse(resource: String): AddOn {
            val cls = AddOn::class.java
            return serializer.read(cls, cls.getResourceAsStream(resource)!!)
        }
    }

    override fun toString(): String {
        return "AddOn(name='$name', versions=$versions)"
    }
}