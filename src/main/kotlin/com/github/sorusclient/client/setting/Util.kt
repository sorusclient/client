package com.github.sorusclient.client.setting

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.math.BigDecimal
import java.util.*

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
            if (wantedClass == Double::class.java) {
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
                    map[key] = toJava<Any>(null, value)!!
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

    fun toData(`object`: Any): Any {
        return `object` as? Boolean
            ?: (`object` as? Int
                ?: (`object` as? Long
                    ?: (`object` as? Double
                        ?: (`object` as? Float
                            ?: (`object` as? String
                                ?: if (`object` is Enum<*>) {
                                    `object`.name
                                } else if (`object` is List<*>) {
                                    val data: MutableList<Any> = ArrayList()
                                    for (inData in `object`) {
                                        data.add(toData(inData!!))
                                    }
                                    data
                                } else if (`object` is Map<*, *>) {
                                    val data: MutableMap<String, Any> = HashMap()
                                    for ((key, value) in `object` as Map<String, Any>) {
                                        data[key] = toData(value)
                                    }
                                    data
                                } else {
                                    val data: MutableMap<String, Any> = HashMap()
                                    for (field in `object`.javaClass.declaredFields) {
                                        if (Modifier.isStatic(field.modifiers)) continue
                                        try {
                                            field.isAccessible = true
                                            data[field.name] = toData(field[`object`])
                                        } catch (e: IllegalAccessException) {
                                            e.printStackTrace()
                                        }
                                    }
                                    data["class"] = `object`.javaClass.name
                                    data
                                })))))
    }
}