package com.github.sorusclient.client.setting

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Util {

    @Suppress("UNCHECKED_CAST")
    fun <T> toJava(wantedClass: Class<T>?, jsonSetting: Any?): T? {
        var wantedClass = wantedClass
        if (jsonSetting is Int) {
            return when (wantedClass) {
                Double::class.java -> {
                    jsonSetting.toDouble() as T
                }
                Long::class.java -> {
                    jsonSetting.toLong() as T
                }
                else -> {
                    jsonSetting as T
                }
            }
        } else if (jsonSetting is Boolean) {
            return jsonSetting as T
        } else if (jsonSetting is Double) {
            return jsonSetting as T
        } else if (jsonSetting is BigDecimal) {
            if (wantedClass == java.lang.Double::class.java) {
                return jsonSetting.toDouble() as T
            } else if (wantedClass == Double::class.javaPrimitiveType) {
                return jsonSetting.toDouble() as T
            }
        } else if (jsonSetting is String) {
            if (wantedClass != null && wantedClass.superclass == Enum::class.java) {
                try {
                    return wantedClass.getDeclaredField(jsonSetting as String?)[null] as T
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            } else {
                return jsonSetting as T
            }
        } else if (jsonSetting is Map<*, *>) {
            if (jsonSetting.contains("list")) {
                val list = ArrayList<Any>()
                val wantedClassInner = Class.forName(jsonSetting["type"] as String)
                for (thing in jsonSetting["list"] as List<Any>) {
                    toJava(wantedClassInner, thing)?.let { list.add(it) }
                }

                return list as T
            }
            val jsonSettingMap = jsonSetting as Map<String, Any>
            if (wantedClass == null) {
                val className = jsonSettingMap["class"] as String?
                if (className != null) {
                    try {
                        wantedClass = Class.forName(className) as Class<T>
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
            if (wantedClass != null && wantedClass == MutableMap::class.java) {
                val map: MutableMap<String, Any> = HashMap()
                for ((key, value) in jsonSetting) {
                    toJava<Any>(null, value)?.let { map[key] = it }
                }
                return map as T
            } else if (wantedClass != null) {
                try {
                    val constructor = Objects.requireNonNull(wantedClass).getDeclaredConstructor()
                    constructor.isAccessible = true
                    val wantedObject = constructor.newInstance()
                    for ((key, value) in jsonSetting) {
                        if (key == "class") continue
                        val field = wantedClass.getDeclaredField(key)
                        field.isAccessible = true
                        field[wantedObject] = toJava(field.type, value)
                    }
                    return wantedObject
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: NoSuchFieldException) {
                    e.printStackTrace()
                } catch (e: NoSuchMethodException) {
                    e.printStackTrace()
                } catch (e: InvocationTargetException) {
                    e.printStackTrace()
                }
            }
        } else if (jsonSetting is List<*>) {
            val list: MutableList<Any> = ArrayList()
            for (`object` in jsonSetting) {
                list.add(toJava<Any>(null, `object`)!!)
            }
            return list as T
        }
        return null
    }

    fun toData(any: Any): Any {
        return any as? Boolean
            ?: (any as? Int
                ?: (any as? Long
                    ?: (any as? Double
                        ?: (any as? Float
                            ?: (any as? String
                                ?: if (any is Enum<*>) {
                                    any.name
                                } else if (any is List<*>) {
                                    val data: MutableMap<String, Any> = HashMap()
                                    val dataInner: MutableList<Any> = ArrayList()
                                    data["type"] = any[0]!!.javaClass.name
                                    data["list"] = dataInner
                                    for (inData in any) {
                                        dataInner.add(toData(inData!!))
                                    }
                                    data
                                } else if (any is Map<*, *>) {
                                    val data: MutableMap<String, Any> = HashMap()
                                    for ((key, value) in any as Map<String, Any>) {
                                        data[key] = toData(value)
                                    }
                                    data
                                } else {
                                    val data: MutableMap<String, Any> = HashMap()
                                    for (field in any.javaClass.declaredFields) {
                                        if (Modifier.isStatic(field.modifiers) || Modifier.isTransient(field.modifiers)) continue
                                        try {
                                            field.isAccessible = true
                                            data[field.name] = toData(field.get(any))
                                        } catch (e: IllegalAccessException) {
                                            e.printStackTrace()
                                        }
                                    }
                                    data["class"] = any.javaClass.name
                                    data
                                })))))
    }
}