package com.github.sorusclient.client.adapter

interface ITranslatableText : IText {
    val key: String?
    val arguments: List<Any>
}