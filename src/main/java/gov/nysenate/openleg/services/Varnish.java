package gov.nysenate.openleg.services;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.util.Storage;
import gov.nysenate.openleg.util.TextFormatter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map.Entry;

public class Varnish extends ServiceBase {
    private final String host;
    private final int port;

    public Varnish(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean process(HashMap<String, Storage.Status> changeLog, Storage storage) throws IOException {

        for(Entry<String, Storage.Status> entry : changeLog.entrySet()) {
            String key = entry.getKey();
            String otype = key.split("/")[1];
            String oid = key.split("/")[2];
            Class<? extends ILuceneObject> objType = classMap.get(otype);

            if(entry.getValue() == Storage.Status.DELETED) {
                if(otype.equals("agenda")) {
                    // TODO: Need a way to handle meetings here.. very important
                    // Probably need a whole new approach to this one
                } else {
                    purgeUri("doc:"+oid);
                }
            } else {
                ILuceneObject obj = (ILuceneObject) storage.get(key, objType);
                purgeUri("doc:"+obj.luceneOid());
            }
        }
        purgeUri("search");
        purgeUri("views");
        return true;
    }


    public void purgeUri(String uri) throws IOException {
        SocketChannel channel = null;

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

}

