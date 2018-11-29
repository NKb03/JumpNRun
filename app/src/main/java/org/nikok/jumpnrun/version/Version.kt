package org.nikok.jumpnrun.version

import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlElement

data class Version(
    @XmlAttribute val versionNumber: Int,
    @XmlElement(name = "patch") val patchClassNames: List<String>
) {
    fun patches(classLoader: ClassLoader): List<Patch> = patchClassNames.map { clsName ->
        val cls = try {
            classLoader.loadClass(clsName).kotlin
        } catch (cnf: ClassNotFoundException) {
            throw RuntimeException("Patch class registered for version $versionNumber not found")
        }
        val obj = cls.objectInstance ?: throw RuntimeException("Patch class has no object instance")
        obj as? Patch ?: throw RuntimeException("Object instance of Patch class is not a patch")
    }

    private constructor() : this(-1, emptyList())

    constructor(versionNumber: Int, vararg patches: Patch)
            : this(versionNumber, patches.map { it.javaClass.name })
}