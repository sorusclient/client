package com.github.sorusclient.client.setting

import org.json.JSONObject
import java.io.File

object SettingManager {

    private val profileFile = File("sorus/profile")
    private val settingContainers: MutableMap<String?, SettingContainer> = HashMap()
    private lateinit var mainProfile: Profile
    var currentProfile: Profile? = null
        private set
    private val allProfiles: MutableList<Profile?> = ArrayList()

    fun register(settingContainer: SettingContainer) {
        settingContainers[settingContainer.id] = settingContainer
    }

    fun loadProfiles() {
        mainProfile = Profile.Companion.read(File(profileFile, "main"))
        addAllProfiles(mainProfile)
    }

    private fun addAllProfiles(profile: Profile?) {
        allProfiles.add(profile)
        allProfiles.addAll(profile!!.children.values)
    }

    fun load(profileId: String) {
        if (currentProfile != null) {
            save(currentProfile)
        }
        val profile = mainProfile.getProfile(profileId)!!
        load(profile)
    }

    private fun load(profile: Profile) {
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
        if (profile.parent != null) {
            loadInternal(profile.parent!!)
        }
        val profileSettings = profile.readSettings()
        val settingsJson = JSONObject(profileSettings)
        for ((key, value) in settingsJson.toMap()) {
            val settingContainer = settingContainers[key]
            if (settingContainer != null) {
                val jsonObject1 = value as Map<String, Any>
                settingContainer.shared = profile === mainProfile
                settingContainer.load(jsonObject1)
            }
        }
    }

    fun getAllProfiles(): List<Profile?> {
        return allProfiles
    }

    fun saveCurrent() {
        save(currentProfile)
    }

    private fun save(profile: Profile?) {
        val isMainProfile = profile == mainProfile
        val settingsJsonParent = JSONObject(mainProfile.readSettings()).toMap()
        val settingsJson: MutableMap<String?, Any?> = HashMap()
        for ((key, value) in settingContainers) {
            if (value.shared && !isMainProfile) {
                settingsJsonParent[key] = value.save()
            } else {
                settingsJson[key] = value.save()
            }
        }
        val jsonObject = JSONObject(settingsJson)
        profile!!.writeSettings(jsonObject.toString(2))
        if (!isMainProfile) {
            val jsonObjectParent = JSONObject(settingsJsonParent)
            profile.parent?.writeSettings(jsonObjectParent.toString(2))
            updateProfile(mainProfile)
        }
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
            if (profile!!.id.length > 1) {
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