package com.github.sorusclient.client.setting;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

public class Profile {

    public static Profile read(File folder) {
        Map<String, Profile> children = new HashMap<>();

        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (!file.getName().equals("settings.json")) {
                children.put(file.getName(), Profile.read(new File(folder, file.getName())));
            }
        }

        Profile profile = new Profile(new File(folder, "settings.json"), folder.getName(), children);
        for (Profile childProfile : profile.children.values()) {
            childProfile.setParent(profile);
        }

        return profile;
    }

    private Profile parent;
    private final File settingsFile;
    private final Map<String, Profile> children;
    private final String id;

    private Profile(File settingsFile, String id, Map<String, Profile> children) {
        this.settingsFile = settingsFile;
        this.id = id;
        this.children = children;
    }

    public void setParent(Profile parent) {
        this.parent = parent;
    }

    public Profile getParent() {
        return parent;
    }

    public Map<String, Profile> getChildren() {
        return children;
    }

    public String readSettings() {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.settingsFile);
            String string = IOUtils.toString(fileInputStream);
            fileInputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeSettings(String settings) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.settingsFile);
            IOUtils.write(settings, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Profile getProfile(String profileId) {
        if (profileId.equals("/")) {
            return this;
        } else {
            profileId = profileId.substring(1);
            String nextProfileId = profileId.substring(0, profileId.contains("/") ? profileId.indexOf("/") : profileId.length());
            String nextProfileArguments = profileId.substring(profileId.indexOf("/"));

            Profile nextProfile = this.children.get(nextProfileId);
            if (nextProfile == null) return null;
            return nextProfile.getProfile(nextProfileArguments);
        }
    }

    public String getId() {
        if (this.parent != null) {
            return "/" + this.id + "/";
        } else {
            return "/";
        }
    }

    public Profile createProfile(String id) {
        String idSimplified = id.substring(1, id.length() - 1);
        File file = new File(this.settingsFile.getParentFile(), idSimplified);
        file.mkdirs();
        try {
            FileWriter settingsWriter = new FileWriter(new File(file, "settings.json"));
            settingsWriter.write("{}");
            settingsWriter.close();

            Profile profile = Profile.read(file);
            this.children.put(idSimplified, profile);
            profile.setParent(this);

            return profile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void delete() {
        this.getParent().children.remove(this.id);
        try {
            FileUtils.deleteDirectory(this.settingsFile.getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
