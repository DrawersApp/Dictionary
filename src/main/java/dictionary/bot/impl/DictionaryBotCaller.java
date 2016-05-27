package dictionary.bot.impl;

import com.drawers.dao.packets.MqttProviderManager;
import org.drawers.bot.lib.DrawersBotStringHelp;
import org.drawers.bot.listener.DrawersMessageListener;
import org.drawers.bot.mqtt.DrawersBot;
import org.drawers.bot.util.SendMail;

/**
 * Initializing the bot.
 */
public class DictionaryBotCaller implements DrawersMessageListener {

    private static DrawersBot bot;
    private static DictionaryBotCaller client;
    private MqttProviderManager mqttProviderManager;
    private String clientId;

    public DictionaryBotCaller(String clientId, String password) {
        bot = new DrawersBot(clientId, password, this);
        mqttProviderManager = MqttProviderManager.getInstanceFor(bot);
        mqttProviderManager.setClientIdAndName(clientId, "Dictionary");
    }



    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {
        // Load all the classes which contains string.
        Class.forName(MeaningOperations.class.getName());
        System.out.println(DrawersBotStringHelp.getDrawersBotStringHelp().toJsonString());

        if(args.length != 3) {
            System.out.println("Usage: java DictionaryBot <clientId> <password> ");
        } else {
            String clientId = args[0];
            String password = args[1];
            String adminEmail = args[2];
            SendMail.getInstance().setAdminEmail(adminEmail);
            SendMail.getInstance().sendMail("Welcome to Drawers Bot", "Your bot is up and running now.");
            DictionaryBotCaller client = new DictionaryBotCaller(clientId, password);
            client.clientId = clientId;
            client.startBot();
        }
    }

    protected void startBot() throws InterruptedException {
        DictionaryMessageListener dictionaryMessageListener = new DictionaryMessageListener(bot, clientId);
        mqttProviderManager.addMessageListener(dictionaryMessageListener);
        mqttProviderManager.addGroupMessageListener(dictionaryMessageListener);
        bot.start();
        while (true) {
            Thread.sleep(10000000000000000L);
        }

    }

    @Override
    public void onConnected() {
    }
}
