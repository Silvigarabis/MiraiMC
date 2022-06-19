package me.dreamvoid.miraimc.nukkit.event.friend;

import net.mamoe.mirai.event.events.FriendInputStatusChangedEvent;
import me.dreamvoid.miraimc.nukkit.NukkitPlugin;

/**
 * (Nukkit) Mirai 核心事件 - 好友 - 好友输入状态改变
 */
public class MiraiFriendInputStatusChangedEvent extends AbstractFriendEvent {
    public MiraiFriendInputStatusChangedEvent(FriendInputStatusChangedEvent event) {
        super(event);
        this.event = event;


    }

    private final FriendInputStatusChangedEvent event;

    /**
     * 判断好友是否正在输入
     * @return 是否正在输入
     */
    public boolean isInputting(){
        return event.getInputting();
    }
}
