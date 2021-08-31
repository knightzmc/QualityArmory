package me.zombie_striker.qg;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GithubUpdater {

    public static void autoUpdate(final Plugin main, String author, String githubProject, String jarName) {
        try {
            String version = main.getDescription().getVersion();
            String parseVersion = version.replace(".", "");

            String tagName;
            URL api = new URL("https://api.github.com/repos/" + author + "/" + githubProject + "/releases/latest");
            URLConnection con = api.openConnection();
            con.setConnectTimeout(15000);
            con.setReadTimeout(15000);

            JsonObject json;
            try {
                json = new JsonParser().parse(new InputStreamReader(con.getInputStream())).getAsJsonObject();
            } catch (Exception e45) {
                return;
            }
            tagName = json.get("tag_name").getAsString();

            String parsedTagName = tagName.replace(".", "");

            int latestVersion = Integer.parseInt(parsedTagName.substring(1).replaceAll("[^\\d.]", ""));
            int parsedVersion = Integer.parseInt(parseVersion.replaceAll("[^\\d.]", ""));

            final URL download = new URL("https://github.com/" + author + "/" + githubProject + "/releases/download/"
                    + tagName + "/" + jarName);

            if (latestVersion > parsedVersion) {
                main.getLogger().info(() -> ChatColor.GREEN + "Found a new version of " + ChatColor.GOLD
                        + main.getDescription().getName() + ": " + ChatColor.WHITE + tagName
                        + ChatColor.LIGHT_PURPLE + " downloading now!!");

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        try (InputStream in = download.openStream()) {
                            Path pluginFile;
                            try {
                                pluginFile = new File(URLDecoder.decode(
                                        this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath(),
                                        "UTF-8")).toPath();
                            } catch (UnsupportedEncodingException e) {
                                throw new RuntimeException("You don't have a good text codec on your system", e);
                            }

                            Path tempInCaseSomethingGoesWrong = new File(main.getName() + "-backup.jar").toPath();
                            Files.copy(pluginFile, tempInCaseSomethingGoesWrong);

                            Files.deleteIfExists(pluginFile);

                            Files.copy(in, pluginFile, StandardCopyOption.REPLACE_EXISTING);

                            if (Files.size(pluginFile) < 1000) {
                                // Plugin is too small. Keep old version in case new one is
                                // incomplete/nonexistant
                                Files.copy(tempInCaseSomethingGoesWrong, pluginFile);
                            } else {
                                // Plugin is valid, and we can delete the temp
                                Files.deleteIfExists(tempInCaseSomethingGoesWrong);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLaterAsynchronously(main, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
