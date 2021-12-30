package com.github.sorusclient.client.setting;

import org.json.JSONObject;

import java.io.File;
import java.util.*;

public class SettingManager {

    private final File PROFILE_FILE = new File("sorus/profile");
    private final Map<String, SettingContainer> settingContainers = new HashMap<>();

    private Profile mainProfile;
    private Profile currentProfile;

    private final List<Profile> allProfiles = new ArrayList<>();

    public void register(SettingContainer settingContainer) {
        this.settingContainers.put(settingContainer.getId(), settingContainer);
    }

    public void loadProfiles() {
        this.mainProfile = Profile.read(new File(PROFILE_FILE, "main"));
        this.addAllProfiles(this.mainProfile);
    }

    private void addAllProfiles(Profile profile) {
        this.allProfiles.add(profile);
        this.allProfiles.addAll(profile.getChildren().values());
    }

    public void load(String profileId) {
        if (this.currentProfile != null) {
            this.save(this.currentProfile);
        }
        Profile profile = mainProfile.getProfile(profileId);
        load(profile);
    }

    public void load(Profile profile) {
        this.currentProfile = profile;

        this.loadInternal(profile);
    }

    public void delete(Profile profile) {
        if (profile == this.mainProfile) return;
        this.load(this.mainProfile);

        this.allProfiles.remove(profile);

        profile.delete();
    }

    @SuppressWarnings("unchecked")
    private void loadInternal(Profile profile) {
        if (profile.getParent() != null) {
            this.loadInternal(profile.getParent());
        }

        String profileSettings = profile.readSettings();
        JSONObject settingsJson = new JSONObject(profileSettings);

        for (Map.Entry<String, Object> jsonObject : settingsJson.toMap().entrySet()) {
            SettingContainer settingContainer = this.settingContainers.get(jsonObject.getKey());
            if (settingContainer != null) {
                Map<String, Object> jsonObject1 = (Map<String, Object>) jsonObject.getValue();

                settingContainer.setShared(profile == this.mainProfile);

                settingContainer.load(jsonObject1);
            }
        }
    }

    public List<Profile> getAllProfiles() {
        return this.allProfiles;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void saveCurrent() {
        this.save(this.currentProfile);
    }

    public void save(String profileId) {
        Profile profile = mainProfile.getProfile(profileId);
        save(profile);
    }

    private void save(Profile profile) {
        boolean isMainProfile = profile.equals(this.mainProfile);

        Map<String, Object> settingsJsonParent = new JSONObject(this.mainProfile.readSettings()).toMap();
        Map<String, Object> settingsJson = new HashMap<>();

        for (Map.Entry<String, SettingContainer> settingContainer : this.settingContainers.entrySet()) {
            if (settingContainer.getValue().isShared() && !isMainProfile) {
                settingsJsonParent.put(settingContainer.getKey(), settingContainer.getValue().save());
            } else {
                settingsJson.put(settingContainer.getKey(), settingContainer.getValue().save());
            }
        }

        JSONObject jsonObject = new JSONObject(settingsJson);
        profile.writeSettings(jsonObject.toString(2));

        if (!isMainProfile) {
            JSONObject jsonObjectParent = new JSONObject(settingsJsonParent);
            profile.getParent().writeSettings(jsonObjectParent.toString(2));

            this.updateProfile(this.mainProfile);
        }
    }

    private void updateProfile(Profile profile) {
        JSONObject settings = new JSONObject(profile.readSettings());
        Map<String, Object> newSettings = new HashMap<>();

        for (Map.Entry<String, Object> jsonObject : settings.toMap().entrySet()) {
            SettingContainer settingContainer = this.settingContainers.get(jsonObject.getKey());
            if (profile == mainProfile || settingContainer == null || !settingContainer.isShared()) {
                newSettings.put(jsonObject.getKey(), jsonObject.getValue());
            }
        }

        profile.writeSettings(new JSONObject(newSettings).toString(2));

        for (Profile profile1 : profile.getChildren().values()) {
            this.updateProfile(profile1);
        }
    }

    public void createNewProfile() {
        List<String> currentNames = new ArrayList<>();
        for (Profile profile : this.allProfiles) {
            if (profile.getId().length() > 1) {
                currentNames.add(profile.getId().substring(1, profile.getId().length() - 1));
            }
        }

        Profile newProfile = null;
        int i = 0;
        while (newProfile == null) {
            String id = "profile" + i;
            if (!currentNames.contains(id)) {
                newProfile = this.mainProfile.createProfile("/" + id + "/");
            }
            i++;
        }

        this.allProfiles.add(newProfile);

        this.load(newProfile);
    }

}
