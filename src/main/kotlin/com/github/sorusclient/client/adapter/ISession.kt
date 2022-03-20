package com.github.sorusclient.client.adapter

interface ISession {
    fun getUUID(): String
    fun getAccessToken(): String
    fun getUsername(): String
}