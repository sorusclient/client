package com.github.sorusclient.client.bootstrap.loader

import org.apache.commons.io.IOUtils
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import kotlin.collections.ArrayList

class ClassLoader : URLClassLoader(arrayOfNulls(0), null) {

    private val transformers: MutableList<Any> = ArrayList()
    private val canTransformMethod: Method
    private val transformMethod: Method
    private val exclusions: MutableList<String> = ArrayList()
    private val urls: MutableList<URL> = ArrayList()
    private val realParent = ClassLoader::class.java.classLoader

    init {
        exclusions.add("java.")
        exclusions.add("jdk.")
        exclusions.add("javax.")
        exclusions.add("sun.")
        exclusions.add("com.sun.")
        exclusions.add("org.xml.")
        exclusions.add("org.w3c.")
        exclusions.add("org.apache.")
        exclusions.add("org.slf4j.")
        exclusions.add("com.mojang.blocklist.")
        exclusions.add("com.github.sorusclient.client.bootstrap.transformer.Transformer")
        exclusions.add("com.github.glassmc.loader.api.ClassDefinition")

        canTransformMethod = this.loadClass("com.github.sorusclient.client.bootstrap.transformer.Transformer")!!.getMethod(
            "canTransform",
            String::class.java
        )

        transformMethod = this.loadClass("com.github.sorusclient.client.bootstrap.transformer.Transformer")!!.getMethod(
            "transform",
            String::class.java,
            ByteArray::class.java
        )
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String): Class<*>? {
        for (exclusion in exclusions) {
            if (name.startsWith(exclusion)) {
                try {
                    return realParent.loadClass(name)
                } catch (ignored: ClassNotFoundException) {
                }
            }
        }
        var clazz = super.findLoadedClass(name)
        if (clazz == null) {
            val data = getModifiedBytes(name)
            clazz = super.defineClass(name, data, 0, data.size)
        }
        return clazz
    }

    @Throws(ClassNotFoundException::class)
    override fun loadClass(name: String, resolve: Boolean): Class<*>? {
        val clazz = this.loadClass(name)
        if (resolve) {
            resolveClass(clazz)
        }
        return clazz
    }

    @Throws(ClassNotFoundException::class)
    fun getModifiedBytes(name: String): ByteArray {
        var data = loadClassData(name)
        for (transformer in getTransformers()) {
            val formattedName = name.replace(".", "/")

            if (canTransformMethod.invoke(transformer, formattedName) as Boolean) {
                data = transformMethod.invoke(transformer, formattedName, data) as ByteArray
            }
        }

        if (data.isEmpty()) {
            throw ClassNotFoundException(name)
        }
        return data
    }

    private fun getTransformers(): List<Any> {
        //val transformers: MutableList<Any> = ArrayList()
        //transformers.addAll(this.transformers[0])
        //transformers.addAll(this.transformers[1])
        //transformers.addAll(this.transformers[2])
        return this.transformers
    }

    private fun loadClassData(className: String): ByteArray {
        return try {
            val resources = getResources(className.replace(".", "/") + ".class")
            val locations: MutableList<String> = ArrayList()
            val datas: MutableList<ByteArray> = ArrayList()
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                locations.add(resource.toString())
                datas.add(IOUtils.toByteArray(resource))
            }

            val result: Array<ByteArray> = if (datas.size > 0) {
                datas.toTypedArray()
            } else {
                arrayOf(ByteArray(0))
            }

            result[0]
        } catch (e: IOException) {
            e.printStackTrace()
            ByteArray(0)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            ByteArray(0)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    @Throws(IOException::class)
    override fun getResources(name: String): Enumeration<URL> {
        val parentResources: List<URL> = Collections.list(realParent.getResources(name))
        val filteredURLs: MutableList<URL> = ArrayList(parentResources)
        for (pathUrl in Collections.list(findResources(name))) {
            for (url in urls) {
                if (pathUrl.file.contains(url.file)) {
                    filteredURLs.add(pathUrl)
                }
            }
        }
        return Collections.enumeration(filteredURLs)
    }

    override fun getResource(name: String): URL? {
        try {
            val resources = getResources(name)
            if (resources.hasMoreElements()) {
                return resources.nextElement()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return realParent.getResource(name)
    }

    public override fun addURL(url: URL) {
        urls.add(url)
        super.addURL(url)
    }

    fun removeURL(url: URL) {
        urls.remove(url)
    }

    fun addTransformer(transformer: Class<*>) {
        this.transformers.add(transformer.getConstructor().newInstance())
        /*try {
            val listIndex: Int
            listIndex = when (order) {
                TransformerOrder.FIRST -> 0
                TransformerOrder.LAST -> 2
                TransformerOrder.DEFAULT -> 1
                else -> -1
            }
            transformers[listIndex].add(transformer.getConstructor().newInstance())
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }*/
    }
}