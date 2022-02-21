package com.github.sorusclient.client.setting

import com.github.sorusclient.client.server.ServerIntegrationManager
import com.github.sorusclient.client.setting.data.CategoryData
import com.github.sorusclient.client.setting.display.DisplayedCategory
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object SettingManager {

    private val profileFile = File("sorus/profile")
    lateinit var mainProfile: Profile
    var currentProfile: Profile? = null
        private set
    private val allProfiles: MutableList<Profile> = ArrayList()

    val settingsCategory: CategoryData = CategoryData()
    val mainUICategory: DisplayedCategory = DisplayedCategory("Main")

    init {
        profileFile.mkdirs()
    }

    fun initialize() {
        loadProfiles()
        load("/")
        ServerIntegrationManager.joinListeners["settings"] = this::handleJoinForced
        ServerIntegrationManager.leaveListeners.add(this::handleLeaveForced)
    }

    private fun handleJoinForced(json: Any) {
        settingsCategory.loadForced(json)
    }

    private fun handleLeaveForced() {
        settingsCategory.clearForced()
    }

    private fun loadProfiles() {
        val mainProfileFolder = File(profileFile, "main")
        if (!mainProfileFolder.exists()) {
            mainProfileFolder.mkdirs()
            FileUtils.write(File(mainProfileFolder, "settings.json"), "{}")
        }

        mainProfile = Profile.read(mainProfileFolder)
        addAllProfiles(mainProfile)
    }

    private fun addAllProfiles(profile: Profile) {
        allProfiles.add(profile)
        allProfiles.addAll(profile.children.values)
    }

    private fun load(profileId: String) {
        val profile = mainProfile.getProfile(profileId)!!
        load(profile)
    }

    fun load(profile: Profile) {
        currentProfile?.let { save(it) }
        currentProfile = profile
        loadInternal(profile)
    }

    fun delete(profile: Profile) {
        if (profile === mainProfile) return
        this.load(profile.parent!!)
        allProfiles.remove(profile)
        profile.delete()

        for (entry in HashMap(profile.parent!!.children)) {
            if (entry.value == profile) {
                profile.parent!!.children.remove(entry.key)
            }
        }
    }

    private fun loadInternal(profile: Profile) {
        val profiles: MutableList<Profile> = ArrayList()
        var tempProfile: Profile? = profile
        while (tempProfile != null) {
            profiles.add(0, tempProfile)
            tempProfile = tempProfile.parent
        }

        for (profileParent in profiles) {
            load(JSONObject(profileParent.readSettings()).toMap(), profileParent == profile)
        }
    }

    private fun load(json: Any, isPrimary: Boolean) {
        settingsCategory.load(json, isPrimary)
    }

    fun getAllProfiles(): List<Profile> {
        return allProfiles
    }

    fun saveCurrent() {
        save(currentProfile!!)
    }

    private fun save(profile: Profile) {
        profile.writeSettings(JSONObject(save() as Map<*, *>).toString(2))
    }

    private fun save(): Any {
        return settingsCategory.save()
    }

    fun createNewProfile(profile: Profile) {
        val currentNames: MutableList<String> = ArrayList()
        for (profile in profile.children.values) {
            if (profile.id.length > 1) {
                currentNames.add(profile.id.substring(1, profile.id.length - 1))
            }
        }
        var newProfile: Profile? = null
        var i = 0
        while (newProfile == null) {
            val id = "Profile$i"
            if (!currentNames.contains(id)) {
                newProfile = profile.createProfile("/$id/")
            }
            i++
        }
        allProfiles.add(newProfile)
        this.load(newProfile)
    }

}