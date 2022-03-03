package com.github.sorusclient.client.feature

object FeatureManager {

    val features: MutableList<Any> = ArrayList()

    inline fun <reified T> get(): T {
        for (feature in features) {
            if (feature is T) {
                return feature
            }
        }

        return null!!
    }

}