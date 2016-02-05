package dictionary.bot.impl;

import dictionary.bot.Bot;
import dictionary.bot.DrawersBotStringHelp;
import dictionary.bot.MessageSubscriber;
import org.jivesoftware.smack.SmackException;
import org.jxmpp.stringprep.XmppStringprepException;

/**
 * Initializing the bot.
 */
public class BotsCaller implements MessageSubscriber {

    public static void main(String[] args) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException, ClassNotFoundException {
        // Load all the classes which contains string.
        Class.forName(MeaningOperations.class.getName());
        System.out.print(DrawersBotStringHelp.getDrawersBotStringHelp().toJsonString());
        BotsCaller botsCaller = new BotsCaller();
        Bot.getBot(botsCaller, "42382d53-272e-4674-8079-453dc22ee412", "dictionary");
        while (true) {
            Thread.sleep(1000000000000L);
        }
    }

    private String errorDefaultText = "Something went wrong";

    @Override
    public String getErrorDefaultText() {
        return errorDefaultText;
    }
}
