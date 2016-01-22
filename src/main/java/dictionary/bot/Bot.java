package dictionary.bot;

import rocks.xmpp.addr.Jid;
import rocks.xmpp.core.XmppException;
import rocks.xmpp.core.sasl.AuthenticationException;
import rocks.xmpp.core.session.TcpConnectionConfiguration;
import rocks.xmpp.core.session.XmppClient;
import rocks.xmpp.core.stanza.model.Message;
import rocks.xmpp.core.stream.model.StreamElement;
import rocks.xmpp.extensions.httpbind.BoshConnectionConfiguration;
import rocks.xmpp.extensions.json.model.Json;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;

/**
 * Created by harshit on 20/1/16.
 */
public class Bot {

    private static Bot bot;

    private Bot() {
        initializeConnection();
    }

    public static synchronized Bot getBot() {
        if (bot == null) {
            bot = new Bot();
        }
        return bot;
    }

    public void initializeConnection() {
        TcpConnectionConfiguration tcpConfiguration = TcpConnectionConfiguration.builder()
                .hostname("ejabberd.sandwitch.in")
                .port(5222)
                .proxy(Proxy.NO_PROXY)        // Proxy for the TCP connection
                .keepAliveInterval(20)        // Whitespace keep-alive interval
                .secure(false)
                .build();
        BoshConnectionConfiguration boshConfiguration = BoshConnectionConfiguration.builder()
                .hostname("ejabberd.sandwitch.in")
                .port(5280)
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("hostname", 3128)))
                .file("/http-bind/")
                .wait(60)  // BOSH connection manager should wait maximal 60 seconds before responding to a request.
                .build();


        XmppClient xmppClient = new XmppClient("ejabberd.sandwitch.in", tcpConfiguration, boshConfiguration);

        // Listen for messages
        xmppClient.addInboundMessageListener(e -> {
            Message message = e.getMessage();
            // Handle inbound message.
            // TODO - Add check if message is text type.
            Observable.just(message)
                    .subscribeOn(Schedulers.io())
                    .map(msg -> msg.getBody())
                    .filter(body -> body != null && body.length() > 0)
                    .map(word -> RetrofitAdapter.getRetrofitAdapter().getDictionaryInterface().getMeaning(word))
                    .map(meaning -> RetrofitAdapter.generateText(meaning))
                    .filter(serializedMeaning -> serializedMeaning.length() > 0)
                    .subscribe(output -> xmppClient.send(generateMessage(message.getFrom(), Message.Type.CHAT, output)) ,
                            error -> error.printStackTrace());
        });

        try {
            xmppClient.connect();
        } catch (XmppException e) {
            e.printStackTrace();
        }

        /*
          Third parameter is resource. Used to support chat across different entities like Laptop/Mobile.
          Hard code resource to the client/libray - help us identify the client.
         */

        try {
            xmppClient.login("username", "password", "babbler");
        } catch (AuthenticationException e) {
            // Login failed, because the server returned a SASL failure, most likely due to wrong credentials.
        } catch (XmppException e) {
            // Other causes, e.g. no response, failure during resource binding, etc.
        }
    }

    private StreamElement generateMessage(Jid from, Message.Type chat, String s) {
        Message message = new Message(from, chat, s);
        message.setId(UUID.randomUUID().toString());
        message.addExtension(generateJsonContainer(ChatType.TEXT));
        return message;
    }



    public Json generateJsonContainer(ChatType chatType) {
        ChatMetaData  chatMetaData = new ChatMetaData(chatType.toString(), System.currentTimeMillis());
        Json jsonPacketExtension = new Json(chatMetaData.toJsonString());
        return jsonPacketExtension;
    }

}
