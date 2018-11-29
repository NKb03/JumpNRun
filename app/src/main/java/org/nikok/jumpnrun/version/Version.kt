package org.nikok.jumpnrun.version

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element

data class Version(
    @Attribute val versionNumber: Int,
    @Element(name = "patch") val patchClassNames: List<String>
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
}