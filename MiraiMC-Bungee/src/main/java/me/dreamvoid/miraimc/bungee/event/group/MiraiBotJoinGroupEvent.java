package me.dreamvoid.miraimc.bungee.event.group;

import net.mamoe.mirai.event.events.BotJoinGroupEvent;
import net.md_5.bungee.api.ProxyServer;

/**
 * (BungeeCord) Mirai 核心事件 - 群 - 机器人成功加入了一个新群
 */
public class MiraiBotJoinGroupEvent extends AbstractGroupEvent {
    public MiraiBotJoinGroupEvent(BotJoinGroupEvent event) {
        super(event);
        this.event = event;


    }

    private final BotJoinGroupEvent event;

    /**
     * 返回加入群的群号
     * @return 群号
     */
    @Override
    public long getGroupID() { return event.getGroupId(); }
}
