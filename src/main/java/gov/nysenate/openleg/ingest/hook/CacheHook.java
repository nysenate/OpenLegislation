package gov.nysenate.openleg.ingest.hook;

import gov.nysenate.openleg.model.Addendum;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.apache.log4j.Logger;

public class CacheHook implements Hook<List<? extends SenateObject>> {
    private static Logger logger = Logger.getLogger(CacheHook.class);

    public static final String DEFAULT_HOST = "http://127.0.0.1";
    public static final int DEFAULT_PORT = 80;

    public String host;
    public int port;

    public CacheHook() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public CacheHook(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void call(List<? extends SenateObject> list) {
        for(SenateObject so:list) {if(so instanceof Agenda) {
            Agenda agenda = (Agenda) so;

            if (agenda.getAddendums() != null) {
                for( Addendum addendum : agenda.getAddendums() ) {
                    if(addendum.getMeetings() != null) {
                        call(addendum.getMeetings());
                    }
                }
            }
        }
        else {
            purgeUri(TextFormatter.append("doc:",so.luceneOid()));
        }
        }
        purgeUri("search");
        purgeUri("views");
    }

    public void purgeUri(String uri) {
        SocketChannel channel = null;

        try {
            URL connectUrl = new URL(host);
            String host = connectUrl.getHost();

            SocketAddress remote = new InetSocketAddress(host, port);
            channel = SocketChannel.open(remote);

            logger.warn(TextFormatter.append("purging ", uri, " at ", host));

            String request = "PURGE " + uri + " HTTP/1.1\r\n" + "User-Agent: HTTPGrab\r\n" +
                    "Accept: text/*\r\nConnection: close\r\nHost: " + host + "\r\n\r\n";

            ByteBuffer header = ByteBuffer.wrap(request.getBytes("US-ASCII"));
            channel.write(header);
            channel.finishConnect();

            ByteBuffer buffer = ByteBuffer.allocate(131072);
            while(channel.read(buffer) != -1) {
                buffer.clear();
            }

            channel.close();
        }
        catch (IOException e) {
            logger.error(e);
        }
    }
}