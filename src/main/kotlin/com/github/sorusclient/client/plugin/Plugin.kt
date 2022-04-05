/*
 * Copyright (c) 2022. Danterus
 * Copyright (c) 2022. Sorus Contributors
 *
 * SPDX-License-Identifier: MPL-2.0
 */

package com.github.sorusclient.client.plugin

import java.io.File

data class Plugin(val id: String, val version: String, val name: String, val description: String, val file: File, val logo: String?)