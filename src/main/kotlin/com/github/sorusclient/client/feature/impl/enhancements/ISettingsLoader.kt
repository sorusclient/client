package com.github.sorusclient.client.feature.impl.enhancements

interface ISettingsLoader {

     fun save(cached: Map<String, Any>): Map<String, Any>

     fun load(map: Map<String, Any>)

}