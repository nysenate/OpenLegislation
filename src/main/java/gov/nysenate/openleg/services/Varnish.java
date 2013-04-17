package gov.nysenate.openleg.services;

import gov.nysenate.openleg.util.Storage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map.Entry;

public class Varnish extends ServiceBase {
    protected final InetSocketAddress remoteSocket;

    protected static final String LOG_TEMPLATE = "purging %s at %s";

    protected static final String PURGE_TEMPLATE = "PURGE %s HTTP/1.1\r\n" +
                                                   "User-Agent: HTTPGrab\r\n" +
                                                   "Accept: text/*\r\n" +
                                                   "Connection: close\r\n" +
                                                   "Host: %s\r\n\r\n";

    public Varnish(String host, int port) {
        super();
        remoteSocket = new InetSocketAddress(host, port);
    }

    @Override
    public boolean process(HashMap<String, Storage.Status> changeLog, Storage storage) throws IOException {
        for(Entry<String, Storage.Status> entry : changeLog.entrySet()) {
            // Key format is YEAR/OTYPE/OID
            String key = entry.getKey();
            String otype = key.split("/")[1];
            String oid = key.split("/")[2];

            if(!otype.equals("agenda")) {
                purgeUri("doc:"+oid);
            }
        }
        purgeUri("search");
        purgeUri("views");
        return true;
    }

    public void purgeUri(String uri) throws IOException {
        // This works, but maybe we can do it without manually opening sockets?
        // TODO: Do we need to re-open the channel for each request?
        logger.warn(String.format(LOG_TEMPLATE, uri, remoteSocket.getHostName()));
        SocketChannel channel = SocketChannel.open(remoteSocket);
        String request = String.format(PURGE_TEMPLATE, uri, remoteSocket.getHostName());

        ByteBuffer header = ByteBuffer.wrap(request.getBytes("US-ASCII"));
        channel.write(header);
        channel.finishConnect();

        // Can't we just close it if we are throwing the response away?
        ByteBuffer buffer = ByteBuffer.allocate(131072);
        while(channel.read(buffer) != -1) {
            buffer.clear();
        }

        channel.close();
    }

}

