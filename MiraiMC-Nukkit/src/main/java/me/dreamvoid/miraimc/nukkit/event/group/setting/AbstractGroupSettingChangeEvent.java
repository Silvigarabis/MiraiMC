package me.dreamvoid.miraimc.nukkit.event.group.setting;

import cn.nukkit.event.HandlerList;
import me.dreamvoid.miraimc.api.bot.MiraiGroup;
import net.mamoe.mirai.event.events.GroupSettingChangeEvent;
import cn.nukkit.event.Event;

/**
 * (Nukkit) Mirai 核心事件 - 群 - 群设置 - 群设置改变（抽象）
 */
abstract class AbstractGroupSettingChangeEvent extends Event {
	public AbstractGroupSettingChangeEvent(GroupSettingChangeEvent<?> event){
		this.event = event;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlers() { return handlers; }

	private final GroupSettingChangeEvent<?> event;
	/**
	 * 获取机器人账号
	 * @return 机器人账号
	 */
	public long getBotID() { return event.getBot().getId(); }

	/**
	 * 获取群号
	 * @return 群号
	 */
	public long getGroupID(){
		return event.getGroup().getId();
	}

	/**
	 * 获取哈希值
	 * @return 哈希值
	 */
	public int getHashCode(){
		return event.hashCode();
	}

	/**
	 * 获取原始事件内容<br>
	 * [!] 不推荐使用
	 * @return 原始事件内容
	 */
	@Override
	public String toString() {
		return event.toString();
	}

	/**
	 * 获取群实例
	 * @return MiraiGroup 实例
	 */
	public MiraiGroup getGroup(){
		return new MiraiGroup(event.getBot(), event.getGroup().getId());
	}
}
