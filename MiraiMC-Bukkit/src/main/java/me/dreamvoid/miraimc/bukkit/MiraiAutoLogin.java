package me.dreamvoid.miraimc.bukkit;

import me.dreamvoid.miraimc.api.MiraiBot;
import me.dreamvoid.miraimc.httpapi.MiraiHttpAPI;
import me.dreamvoid.miraimc.httpapi.exception.AbnormalStatusException;
import me.dreamvoid.miraimc.internal.Config;
import me.dreamvoid.miraimc.internal.Utils;
import net.mamoe.mirai.utils.BotConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class MiraiAutoLogin {

    public MiraiAutoLogin(BukkitPlugin plugin) {
        this.plugin = plugin;
        this.Logger = Utils.logger;
        Instance = this;
    }

    private final BukkitPlugin plugin;
    private final Logger Logger;
    private static File AutoLoginFile;
    public static MiraiAutoLogin Instance;

    public void loadFile() {
        // 建立文件夹
        File ConsoleDir = new File(Utils.getMiraiDir(), "config/Console");
        if(!ConsoleDir.exists() &&!ConsoleDir.mkdirs()) throw new RuntimeException("Failed to create folder " + ConsoleDir.getPath());

        // 建立自动登录文件
        AutoLoginFile = new File(ConsoleDir, "AutoLogin.yml");
        if(!AutoLoginFile.exists()) {
            try {
                if(!AutoLoginFile.createNewFile()){ throw new RuntimeException("Failed to create folder " + AutoLoginFile.getPath()); }
                String defaultText = "accounts: "+System.getProperty("line.separator");
                File writeName = AutoLoginFile;
                try (FileWriter writer = new FileWriter(writeName);
                     BufferedWriter out = new BufferedWriter(writer)
                ) {
                    out.write(defaultText);
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Map<?, ?>> loadAutoLoginList() {
        FileConfiguration data = YamlConfiguration.loadConfiguration(AutoLoginFile);
        return data.getMapList("accounts");
    }

    public void doStartUpAutoLogin() {
        Runnable thread = () -> {
            Logger.info("[AutoLogin] Starting auto login task.");
            for(Map<?,?> map : loadAutoLoginList()){
                Map<?,?> password = (Map<?, ?>) map.get("password");
                Map<?,?> configuration = (Map<?, ?>) map.get("configuration");
                long Account = Long.parseLong(String.valueOf(map.get("account")));
                if(Account != 123456){
                    String Password = password.get("value").toString();
                    BotConfiguration.MiraiProtocol Protocol = null;
                    boolean ShouldUseHttpApi = false;
                    String ProtocolName = configuration.get("protocol").toString().toUpperCase();
                    if (ProtocolName.equals("HTTPAPI")){
                        ShouldUseHttpApi = true;
                    } else {
                        try {
                            Protocol = BotConfiguration.MiraiProtocol.valueOf(ProtocolName);
                        } catch (IllegalArgumentException ignored) {
                            Logger.warning("[AutoLogin] Unknown protocol "+ProtocolName+", using ANDROID_PHONE instead.");
                            Protocol = BotConfiguration.MiraiProtocol.ANDROID_PHONE;
                        }
                    }
                    if(ShouldUseHttpApi){
                        if(Config.General.EnableHttpApi) {
                            try {
                                MiraiHttpAPI httpAPI = new MiraiHttpAPI(Config.HttpApi.Url);
                                httpAPI.bind(httpAPI.verify(Password).session, Account);
                                Logger.info("[AutoLogin] Auto login bot account: " + Account + " Protocol: HTTPAPI");
                            } catch (AbnormalStatusException e){
                                Logger.warning("[AutoLogin] 使用HTTPAPI登录机器人时出现异常，状态码："+e.getCode()+"，原因: " + e.getMessage());
                            } catch(IOException ex) {
                                ex.printStackTrace();
                                Logger.severe("[AutoLogin] Failed for auto login account "+ Account +" using protocol HTTPAPI due unknown error");
                            }
                        } else {
                            Logger.severe("[AutoLogin] Failed for auto loging account: "+ Account +" due http-api is disabled");
                        }
                    } else{
                        Logger.info("[AutoLogin] Auto login bot account: " + Account + " Protocol: " + Protocol.name());
                        MiraiBot.doBotLogin(Account, Password, Protocol);
                    }
                }
            }
        };
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, thread);
    }

    public boolean addAutoLoginBot(long Account, String Password, String Protocol){
        // 获取现有的机器人列表
        FileConfiguration data = YamlConfiguration.loadConfiguration(AutoLoginFile);
        List<Map<?, ?>> list = data.getMapList("accounts");

        // 新建用于添加进去的Map
        Map<Object, Object> account = new HashMap<>();

        // account 节点
        account.put("account", Account);

        // password 节点
        Map<Object, Object> password = new HashMap<>();
        password.put("kind", "PLAIN");
        password.put("value", Password);
        account.put("password", password);

        // configuration 节点
        Map<Object, Object> configuration = new HashMap<>();
        configuration.put("protocol", Protocol.toUpperCase());
        configuration.put("device", "device.json");
        account.put("configuration", configuration);

        // 添加
        list.add(account);
        data.set("accounts", list);
        try {
            data.save(AutoLoginFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean delAutoLoginBot(long Account){
        FileConfiguration data = YamlConfiguration.loadConfiguration(AutoLoginFile);
        List<Map<?, ?>> list = data.getMapList("accounts");

        for (Map<?, ?> bots : list) {
            if (Long.parseLong(String.valueOf(bots.get("account"))) == Account) {
                list.remove(bots);
                break;
            }
        }

        data.set("accounts", list);

        try {
            data.save(AutoLoginFile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
