package dictionary.bot.impl;

import com.drawers.dao.ChatConstant;
import com.drawers.dao.MqttChatMessage;
import com.drawers.dao.packets.MqttChat;
import com.drawers.dao.packets.group.GroupMessage;
import com.drawers.dao.packets.listeners.GroupMessageListener;
import com.drawers.dao.packets.listeners.NewMessageListener;
import dictionary.bot.OperationsManager;
import org.drawers.bot.lib.DrawersBotString;
import org.drawers.bot.lib.Response;
import org.drawers.bot.mqtt.DrawersBot;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by harshit on 27/5/16.
 */
public class DictionaryMessageListener implements NewMessageListener, GroupMessageListener {
    private final DrawersBot bot;
    private final String clientId;
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public DictionaryMessageListener(DrawersBot bot, String clientId) {
        this.bot = bot;
        this.clientId = clientId;
    }

    @Override
    public void receiveMessage(MqttChatMessage mqttChatMessage) {
        executorService.submit((Runnable) () -> {
            if (mqttChatMessage.chatType != ChatConstant.ChatType.QA) {
                return;
            }
            String message = null;
            try {
                message = URLDecoder.decode(mqttChatMessage.message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (message == null) {
                return;
            }
            DrawersBotString drawersBotString = DrawersBotString.fromString(message);
            Response response = OperationsManager.getOperationsManager().performOperations(drawersBotString);
            String responseString = null;
            try {
                responseString = URLEncoder.encode(response.toUserString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (responseString == null) {
                return;
            }
            MqttChat mqttChat = new MqttChat(mqttChatMessage.senderUid, UUID.randomUUID().toString(), responseString, false, ChatConstant.ChatType.TEXT, clientId);
            mqttChat.sendStanza(bot);
        });
    }

    @Override
    public void acknowledgeStanza(MqttChatMessage mqttChatMessage) {

    }

    @Override
    public void messageSendAck(GroupMessage.GroupMessageContainer groupMessageContainer) {

    }

    @Override
    public void receiveMessage(GroupMessage.GroupMessageContainer groupMessageContainer, String s) {
        executorService.submit((Runnable) () -> {
            if (groupMessageContainer.chatType != ChatConstant.ChatType.QA) {
                return;
            }
            String message = null;
            try {
                message = URLDecoder.decode(groupMessageContainer.message, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (message == null) {
                return;
            }
            DrawersBotString drawersBotString = DrawersBotString.fromString(message);
            Response response = OperationsManager.getOperationsManager().performOperations(drawersBotString);
            String responseString = null;
            try {
                responseString = URLEncoder.encode(response.toUserString(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (responseString == null) {
                return;
            }
            GroupMessage groupMessage = new GroupMessage(s, UUID.randomUUID().toString(), response.toUserString(), ChatConstant.ChatType.TEXT, clientId);
            groupMessage.sendStanza(bot);
        });
    }
}
