package me.zombie_striker.qg.handlers;

import me.zombie_striker.qg.QAMain;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class HotbarMessager {

    // This is the server version. This is how we know the server version.
    // These are the Class instances. Needed to get fields or methods for classes.
    private static Class<?> CRAFTPLAYERCLASS, PACKET_PLAYER_CHAT_CLASS, ICHATCOMP, CHATMESSAGE, PACKET_CLASS,
            CHAT_MESSAGE_TYPE_CLASS;
    private static Field PLAYERCONNECTION;
    private static Method GETHANDLE, SENDPACKET;
    // These are the constructors for those classes. Need to create new objects.
    private static Constructor<?> PACKET_PLAYER_CHAT_CONSTRUCTOR, CHATMESSAGE_CONSTRUCTOR;
    // Used in 1.12+. Bytes are replaced with this enum
    private static Object CHAT_MESSAGE_TYPE_ENUM_OBJECT;
    private static Class<?> CHAT_MESSAGE_TYPE;
    private static int PacketConstructorType = 0;

    static {
        if (!ReflectionsUtil.isVersionHigherThan(1, 13)) {
            try {
                // This here sets the class fields.

                CRAFTPLAYERCLASS = ReflectionsUtil.getCraftBukkitClass("entity.CraftPlayer");
                PACKET_PLAYER_CHAT_CLASS = ReflectionsUtil.getPacketClass("PacketPlayOutChat");
                PACKET_CLASS = ReflectionsUtil.getPacketClass("Packet");
                ICHATCOMP = ReflectionsUtil.getMinecraftClass("IChatBaseComponent");
                GETHANDLE = CRAFTPLAYERCLASS.getMethod("getHandle");
                PLAYERCONNECTION = GETHANDLE.getReturnType().getField("playerConnection");
                SENDPACKET = PLAYERCONNECTION.getType().getMethod("sendPacket", PACKET_CLASS);
                try {

                    CHAT_MESSAGE_TYPE_CLASS = ReflectionsUtil.getMinecraftClass("ChatMessageType");
                    CHAT_MESSAGE_TYPE_ENUM_OBJECT = CHAT_MESSAGE_TYPE_CLASS.getEnumConstants()[2];
                    try {
                        PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
                                CHAT_MESSAGE_TYPE_CLASS, UUID.class);
                        PacketConstructorType = 2;
                    } catch (NoSuchMethodException notFound) {
                        PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP,
                                CHAT_MESSAGE_TYPE_CLASS);
                        PacketConstructorType = 1;
                    }
                } catch (IllegalArgumentException | NoSuchMethodException e) {
                    PACKET_PLAYER_CHAT_CONSTRUCTOR = PACKET_PLAYER_CHAT_CLASS.getConstructor(ICHATCOMP, byte.class);
                    PacketConstructorType = 0;
                }
                CHATMESSAGE = ReflectionsUtil.getMinecraftClass("ChatMessage");
                CHATMESSAGE_CONSTRUCTOR = CHATMESSAGE.getConstructor(String.class, Object[].class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the hotbar message 'message' to the player 'player'
     */
    public static void sendHotBarMessage(Player player, String message) throws Exception {
        if (ReflectionsUtil.isVersionHigherThan(1, 13)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }
        try {
            // This creates the IChatComponentBase instance
            Object icb = CHATMESSAGE_CONSTRUCTOR.newInstance(message, new Object[0]);
            // This creates the packet
            Object packet = null;
            if (PacketConstructorType == 0)
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, (byte) 2);
            else if (PacketConstructorType == 1)
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT);
            else if (PacketConstructorType == 2)
                packet = PACKET_PLAYER_CHAT_CONSTRUCTOR.newInstance(icb, CHAT_MESSAGE_TYPE_ENUM_OBJECT, UUID.randomUUID());
            // This casts the player to a craftplayer
            Object craftplayerInst = CRAFTPLAYERCLASS.cast(player);
            // This invokes the method above.
            Object methodhHandle = GETHANDLE.invoke(craftplayerInst);
            // This gets the player's connection
            Object playerConnection = PLAYERCONNECTION.get(methodhHandle);
            // This sends the packet.
            SENDPACKET.invoke(playerConnection, packet);

        } catch (Exception e) {
            failsafe("sendHotBarMessage = " + e.getMessage());
            throw e;
        }
    }

    private static void failsafe(String message) {
        QAMain.getInstance().getLogger().log(Level.WARNING,
                "HotBarMessager disabled! Something went wrong with: " + message);
        QAMain.getInstance().getLogger().log(Level.WARNING, "Report this to Zombie_Striker");
        QAMain.getInstance().getLogger().log(Level.WARNING, "Needed Information: " + Bukkit.getName() + ", "
                + Bukkit.getVersion() + ", " + Bukkit.getBukkitVersion());
        QAMain.getInstance().getLogger().log(Level.WARNING,
                "https://github.com/ZombieStriker/PluginConstructorAPI");
    }
}
