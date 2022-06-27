package me.dreamvoid.miraimc.internal.classloader;

import me.dreamvoid.miraimc.internal.Config;
import me.dreamvoid.miraimc.internal.Utils;
import me.dreamvoid.miraimc.webapi.Info;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static me.dreamvoid.miraimc.internal.classloader.LibrariesLoader.*;

public class MiraiLoader {
    /**
     * 加载最新版Mirai Core
     */
    public static void loadMiraiCore() throws RuntimeException, IOException, ParserConfigurationException, SAXException {
        loadMiraiCore(getLibraryVersionMaven("net.mamoe", "mirai-core-all", Config.Gen_MavenRepoUrl.replace("http://","https://"),"release"));
    }

    public static String getStableVersion() {
        try {
            return Info.init().mirai.get("stable");
        } catch (IOException e){
            Utils.logger.warning("Fetching mirai stable version from remote failed, try to use latest. Reason: " + e);
            return "latest";
        }
    }

    /**
     * 加载指定版本的Mirai Core
     * @param version 版本
     */
    public static void loadMiraiCore(String version) throws RuntimeException, IOException, ParserConfigurationException, SAXException {
        if(version.equalsIgnoreCase("latest")){
            version = getLibraryVersionMaven("net.mamoe", "mirai-core-all", Config.Gen_MavenRepoUrl.replace("http://","https://"),"release");
        }

        // 文件夹
        File MiraiDir;
        if (Config.Gen_MiraiWorkingDir.equals("default")) {
            MiraiDir = new File(Config.PluginDir,"MiraiBot");
        } else {
            MiraiDir = new File(Config.Gen_MiraiWorkingDir);
        }
        File LibrariesDir = new File(MiraiDir,"libs");
        if(!LibrariesDir.exists() && !LibrariesDir.mkdirs()) {
            throw new RuntimeException("Failed to create " + LibrariesDir.getPath());
        }

        File writeName = new File(new File(Config.PluginDir, "cache"), "core-ver");
        if(!writeName.getParentFile().exists() && !writeName.getParentFile().mkdirs()) {
            throw new RuntimeException("Failed to create " + writeName.getParentFile().getPath());
        }

        try {
            loadLibraryClassMaven("net.mamoe", "mirai-core-all", version, "-all", Config.Gen_MavenRepoUrl.replace("http://","https://"), LibrariesDir);
            try (FileWriter writer = new FileWriter(writeName);
                 BufferedWriter out = new BufferedWriter(writer)
            ) {
                out.write(version);
                out.flush();
            }
        } catch (Exception e) {
            Utils.logger.warning("Unable to download mirai core from remote server("+e+"), try to use local core.");
            if(writeName.exists()) {
                String content = new String(Files.readAllBytes(writeName.toPath()), StandardCharsets.UTF_8);
                if(!content.equals("")){
                    String name = "mirai-core-all" + "-" + content + ".jar"; // 文件名
                    File coreFile = new File(LibrariesDir, name);
                    loadLibraryClassLocal(coreFile);
                } else {
                    Utils.logger.warning("Unable to use local core.");
                }
            } else {
                Utils.logger.warning("No local core found.");
            }
        }
    }
}
