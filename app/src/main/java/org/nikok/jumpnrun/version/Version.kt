package org.nikok.jumpnrun.version

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.ElementList

class Version {
    @field: Attribute
    var versionNumber: Int = -1
        private set
        get() = field.takeIf { it != -1 } ?: error("Version number not initialized")

    @field: ElementList(entry = "patch", inline = true)
    lateinit var patchClassNames: List<String>
        private set

    fun patches(classLoader: ClassLoader): List<Patch> = patchClassNames.map { clsName ->
        val cls = try {
            classLoader.loadClass(clsName).kotlin
        } catch (cnf: ClassNotFoundException) {
            throw RuntimeException("Patch class registered for version $versionNumber not found")
        }
        val obj = cls.objectInstance ?: throw RuntimeException("Patch class has no object instance")
        obj as? Patch ?: throw RuntimeException("Object instance of Patch class is not a patch")
    }

    override fun toString(): String {
        return "Version(versionNumber=$versionNumber, patchClassNames=$patchClassNames)"
    }
}