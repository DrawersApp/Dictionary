package dictionary.bot;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.TLSUtils;
import org.jivesoftware.smackx.json.packet.JsonPacketExtension;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main bot class contains the logic for incoming and reply message.
 * @author harshit
 * @version 1.0.0
 */
public class Bot implements PingFailedListener {

    private static Bot bot;
    private XMPPTCPConnection xmppConnection;
    private MessageSubscriber messageSubscriber;
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Bot(MessageSubscriber messageSubscriber) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        this.messageSubscriber = messageSubscriber;
        initializeConnection();
    }

    public static synchronized Bot getBot(MessageSubscriber messageSubscriber) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        if (bot == null) {
            bot = new Bot(messageSubscriber);
        }
        return bot;
    }

    /**
     * Building the connection with stream resumption.
     * Stream resumption allows for quick reconnect for flaky networks.
     * Listening for incoming message and dispatching to message subscriber using thread pool of size 5.
     * @see  <a href="http://xmpp.org/extensions/xep-0198.html">http://xmpp.org/extensions/xep-0198.html</a>
     * @throws XmppStringprepException
     * @throws SmackException.NotConnectedException
     * @throws InterruptedException
     */
    public void initializeConnection() throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        SmackConfiguration.setDefaultPacketReplyTimeout(30 * 1000);
        PingManager.setDefaultPingInterval(5 * 60);
        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
        config.setUsernameAndPassword("42382d53-272e-4674-8079-453dc22ee412", "dictionary");
        config.setResource("smack");
        config.setXmppDomain(JidCreate.domainBareFrom("ejabberd.sandwitch.in"));
        config.setDebuggerEnabled(true);
        config.setCompressionEnabled(true);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible);
        config.setKeystorePath("src/main/java/res/ser.cert");
        try {
            TLSUtils.acceptAllCertificates(config);
            TLSUtils.disableHostnameVerificationForTlsCertificicates(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        xmppConnection = new XMPPTCPConnection(config.build());
        xmppConnection.setUseStreamManagement(true);
        xmppConnection.setUseStreamManagementResumption(true);
        // Listener for incoming messages
        StanzaFilter chatFilter = MessageTypeFilter.CHAT;
        xmppConnection.addAsyncStanzaListener(new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
                Message message = (Message) packet;
                executorService.submit(() -> replyMessage(message));
            }
        }, chatFilter);
        PingManager.getInstanceFor(xmppConnection).registerPingFailedListener(this);
        PingManager.getInstanceFor(xmppConnection).setPingInterval(5 * 60);
        ReconnectionManager.getInstanceFor(xmppConnection).enableAutomaticReconnection();
        try {
            xmppConnection.connect();
        } catch (InterruptedException | XMPPException | SmackException | IOException e) {
            e.printStackTrace();
        }
        try {
            xmppConnection.login();
        } catch (InterruptedException | IOException | SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param message
     * Generates the message from subscriber and propogate to {@link #sendMessage(Jid, String)}.
     * Also check for error conditions.
     */
    private void replyMessage(Message message) {
        if (message.getBody() == null) {
            return;
        }
        String replyMessage = messageSubscriber.getErrorDefaultText();
        try {
            replyMessage = messageSubscriber.generateReply(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendMessage(message.getFrom(), replyMessage);
    }

    /**
     *
     * @param jid
     * @param reply
     * Sends the actual stanza.
     */
    private void sendMessage(Jid jid, String reply) {
        try {
            xmppConnection.sendStanza(generateMessage(jid, Message.Type.chat, reply));
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param from - Jid to which we need to reply.
     * @param chat - message type. hardcoded.
     * @param s  - reply.
     * @return Message
     * This method generates the entire message with extensions.
     */
    private Message generateMessage(Jid from, Message.Type chat, String s) {
        Message message = new Message();
        message.setStanzaId(UUID.randomUUID().toString());
        message.setType(chat);
        message.setTo(from);
        message.setBody(s);
        message.addExtension(generateJsonContainer(ChatType.TEXT));
        return message;
    }


    /**
     *
     * @param chatType
     * @return JsonPacketExtension
     * Adds metadata such as time and chattype to packet to help the client arrange and render the message.
     */
    public JsonPacketExtension generateJsonContainer(ChatType chatType) {
        ChatMetaData  chatMetaData = new ChatMetaData(chatType.toString(), System.currentTimeMillis());
        JsonPacketExtension jsonPacketExtension = new JsonPacketExtension(chatMetaData.toJsonString());
        return jsonPacketExtension;
    }

    @Override
    public void pingFailed() {
        xmppConnection.instantShutdown();
        try {
            xmppConnection.connect();
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
