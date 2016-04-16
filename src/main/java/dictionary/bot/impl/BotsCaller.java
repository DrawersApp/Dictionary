package dictionary.bot.impl;

import dictionary.bot.DrawersBotStringHelp;
import dictionary.bot.MessageSubscriber;
import org.drawers.bot.DrawersClient;
import org.drawers.bot.dto.DrawersMessage;

/**
 * Initializing the bot.
 */
public class BotsCaller extends DrawersClient implements MessageSubscriber {

    public BotsCaller(String clientId, String password) {
        super(clientId, password);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // Load all the classes which contains string.
        Class.forName(MeaningOperations.class.getName());
        System.out.println(DrawersBotStringHelp.getDrawersBotStringHelp().toJsonString());

        if(args.length != 2) {
            System.out.println("Usage: java DictionaryBot <clientId> <password>");
        } else {
            String clientId = args[0];
            String password = args[1];
            BotsCaller client = new BotsCaller(clientId, password);
            client.startBot();
        }
    }

    @Override
    public DrawersMessage processMessageAndReply(DrawersMessage message) {
        return generateReply(message);
    }

    @Override
    public DrawersMessage getErrorDefaultMessage(DrawersMessage message) {
        return new DrawersMessage(message.getSender(), "Something went wrong");
    }
}
