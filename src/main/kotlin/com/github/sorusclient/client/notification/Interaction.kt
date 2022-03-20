package com.github.sorusclient.client.notification

sealed class Interaction {

    data class Button(var text: String = "", var onClick: (() -> Unit)? = null, var closeOnInteract: Boolean = true): Interaction()

}