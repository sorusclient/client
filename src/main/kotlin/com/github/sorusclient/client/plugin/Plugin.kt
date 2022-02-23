package com.github.sorusclient.client.plugin

import java.io.File

data class Plugin(val id: String, val version: String, val name: String, val description: String, val file: File, val logo: String?)