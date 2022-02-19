package com.github.sorusclient.client.setting

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.*
import java.util.*

class Profile private constructor(
    private val settingsFile: File,
    private val idInner: String,
    val children: MutableMap<String, Profile>
) {
    var parent: Profile? = null

    val id: String
        get() {
            return if (parent != null) {
                "/$idInner/"
            } else {
                "/"
            }
        }

    fun readSettings(): String? {
        return try {
            val fileInputStream = FileInputStream(settingsFile)
            val string = IOUtils.toString(fileInputStream)
            fileInputStream.close()
            string
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun writeSettings(settings: String?) {
        try {
            val fileOutputStream = FileOutputStream(settingsFile)
            IOUtils.write(settings, fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getProfile(profileId: String): Profile? {
        var profileId = profileId
        return if (profileId == "/") {
            this
        } else {
            profileId = profileId.substring(1)
            val nextProfileId =
                profileId.substring(0, if (profileId.contains("/")) profileId.indexOf("/") else profileId.length)
            val nextProfileArguments = profileId.substring(profileId.indexOf("/"))
            val nextProfile = children[nextProfileId] ?: return null
            nextProfile.getProfile(nextProfileArguments)
        }
    }

    fun createProfile(id: String): Profile? {
        val idSimplified = id.substring(1, id.length - 1)
        val file = File(settingsFile.parentFile, idSimplified)
        file.mkdirs()
        return try {
            val settingsWriter = FileWriter(File(file, "settings.json"))
            settingsWriter.write("{}")
            settingsWriter.close()
            val profile = read(file)
            children[idSimplified] = profile
            profile.parent = this
            profile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun delete() {
        parent!!.children.remove(id)
        try {
            FileUtils.deleteDirectory(settingsFile.parentFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun read(folder: File): Profile {
            val children: MutableMap<String, Profile> = HashMap()

            for (file in Objects.requireNonNull(folder.listFiles())) {
                if (file.name != "settings.json") {
                    children[file.name] = read(File(folder, file.name))
                }
            }
            val profile = Profile(File(folder, "settings.json"), folder.name, children)
            for (childProfile in profile.children.values) {
                childProfile.parent = profile
            }
            return profile
        }
    }
}