package org.nikok.jumpnrun.version

import android.os.Build
import android.support.annotation.RequiresApi
import java.io.InputStream
import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "addon")
data class AddOn(
    @XmlAttribute val name: String,
    @XmlAttribute val maxVersion: Int,
    @XmlElement(name = "version") val versions: List<Version>
) {
    private constructor() : this("", -1, emptyList())

    companion object {
        private val unmarshaller = createUnmarshaller()

        private fun createUnmarshaller(): Unmarshaller {
            val context = JAXBContext.newInstance(AddOn::class.java)
            return context.createUnmarshaller()
        }

        fun parse(xmlInput: InputStream): AddOn {
            return unmarshaller.unmarshal(xmlInput) as AddOn
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun main(args: Array<String>) {
            val url = AddOn::class.java.getResource("main.xml")!!
            val input = url.openStream()
            val addOn = AddOn.parse(input)
            println(addOn)
        }
    }
}