package com.github.sorusclient.client.setting

import com.github.sorusclient.client.server.ServerIntegrationManager
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import java.io.File

object SettingManager {

    private val profileFile = File("sorus/profile")
    private val settingContainers: MutableMap<String?, SettingContainer> = HashMap()
    lateinit var mainProfile: Profile
    var currentProfile: Profile? = null
        private set
    private val allProfiles: MutableList<Profile> = ArrayList()

    val mainCategory: DisplayedCategory = DisplayedCategory("Main")

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
        mainCategory.loadForced(json)
    }

    private fun handleLeaveForced() {
        mainCategory.clearForced()
    }

    fun register(settingContainer: SettingContainer) {
        settingContainers[settingContainer.id] = settingContainer
    }

    fun loadProfiles() {
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

    fun load(profileId: String) {
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
        this.load(mainProfile)
        allProfiles.remove(profile)
        profile.delete()
    }

    private fun loadInternal(profile: Profile) {
        val profiles: MutableList<Profile> = ArrayList()
        var tempProfile: Profile? = profile
        while (tempProfile != null) {
            profiles.add(0, tempProfile)
            tempProfile = tempProfile.parent
        }

        for (profileParent in profiles) {
            mainCategory.load(JSONObject(profileParent.readSettings()).toMap(), profileParent == profile)
        }
    }

    fun getAllProfiles(): List<Profile> {
        return allProfiles
    }

    fun saveCurrent() {
        save(currentProfile!!)
    }

    private fun save(profile: Profile) {
        profile.writeSettings(JSONObject(mainCategory.save() as Map<*, *>).toString(2))
    }

    private fun updateProfile(profile: Profile?) {
        val settings = JSONObject(profile!!.readSettings())
        val newSettings: MutableMap<String?, Any?> = HashMap()
        for ((key, value) in settings.toMap()) {
            val settingContainer = settingContainers[key]
            if (profile === mainProfile || settingContainer == null || !settingContainer.shared) {
                newSettings[key] = value
            }
        }
        profile.writeSettings(JSONObject(newSettings).toString(2))
        for (profile1 in profile.children.values) {
            updateProfile(profile1)
        }
    }

    fun createNewProfile() {
        val currentNames: MutableList<String> = ArrayList()
        for (profile in allProfiles) {
            if (profile.id.length > 1) {
                currentNames.add(profile.id.substring(1, profile.id.length - 1))
            }
        }
        var newProfile: Profile? = null
        var i = 0
        while (newProfile == null) {
            val id = "profile$i"
            if (!currentNames.contains(id)) {
                newProfile = mainProfile.createProfile("/$id/")
            }
            i++
        }
        allProfiles.add(newProfile)
        this.load(newProfile)
    }

}