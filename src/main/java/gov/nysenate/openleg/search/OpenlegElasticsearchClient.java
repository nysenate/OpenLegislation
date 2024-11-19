package gov.nysenate.openleg.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import gov.nysenate.openleg.common.util.AsciiArt;
import gov.nysenate.openleg.common.util.OutputUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;

@Service
public class OpenlegElasticsearchClient extends ElasticsearchClient implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegElasticsearchClient.class);

    @Autowired
    public OpenlegElasticsearchClient(@Value("${elastic.search.host:localhost}") String elasticSearchHost,
                                      @Value("${elastic.search.port:9200}") int elasticSearchPort,
                                      @Value("${elastic.search.connection_retries:5}") int esAllowedRetries)
            throws InterruptedException {
        super(new RestClientTransport(
                RestClient.builder(new HttpHost(elasticSearchHost, elasticSearchPort, "http")).build(),
                new JacksonJsonpMapper(OutputUtils.elasticsearchJsonMapper))
        );

        for (int triesLeft = esAllowedRetries; triesLeft > 0;) {
            logger.info("Connecting to Elasticsearch...");
            try {
                if (ping().value()) {
                    logger.info("Successfully connected to Elasticsearch!");
                    return;
                }
            } catch (IOException | ElasticsearchException ignored) {}
            logger.warn("Could not connect to Elasticsearch.");
            logger.warn("{} retries remain.", --triesLeft);
            Thread.sleep(1000);
        }
        logger.error("Elasticsearch at host: {}:{} needs to be running prior to deployment!",
                elasticSearchHost, elasticSearchPort);
        logger.error(AsciiArt.START_ELASTIC_SEARCH.getText());
        throw new ElasticsearchProcessException("Elasticsearch connection retries exceeded");
    }

    @Override
    public void close() throws IOException {
        transport.close();
    }
}
