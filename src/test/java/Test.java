import org.ap.search.SearchRequest;
import org.ap.search.SearchService;
import org.ap.search.es.ElasticSearchService;
import org.ap.search.es.ElasticSearchServiceConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class Test {
    private Client client = null;
    private ElasticSearchServiceConfig config = null;
    private SearchRequest request = null;

    @org.junit.Test
    public void testTransportClient() throws Exception {
        InetSocketTransportAddress address = new InetSocketTransportAddress("localhost", 9300);
        this.client = new TransportClient().addTransportAddress(address);

        SearchService svc = new ElasticSearchService(this.client, this.config);
        String test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);

        this.request.setQuery("bush");
        test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);

        this.request.setQuery("putin");
        test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);
    }

    @org.junit.Test
    public void testNodeClient() throws Exception {
        this.client = nodeBuilder()
                .client(true)
                .node()
                .client();

        SearchService svc = new ElasticSearchService(this.client, this.config);
        String test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);

        this.request.setQuery("bush");
        test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);

        this.request.setQuery("putin");
        test = svc.search(this.request);
        assertNotNull(test);

        test = svc.search(this.request);
        assertNotNull(test);
    }

    @After
    public void tearDown() throws Exception {
        if (this.client != null) {
            this.client.close();
        }
    }

    @Before
    public void setUp() throws Exception {
        Map<String, Float> fields = new HashMap<String, Float>();
        fields.put("headline", 5f);
        fields.put("title", 1f);

        ElasticSearchServiceConfig config = new ElasticSearchServiceConfig()
                .setIndex("appl")
                .setType("doc")
                .setFields(fields);

        this.config = config;

        SearchRequest request = new SearchRequest()
                .setQuery("obama")
                .setFields(new String[]{"itemid", "type", "arrivaldatetime", "headline"});

        this.request = request;
    }
}
