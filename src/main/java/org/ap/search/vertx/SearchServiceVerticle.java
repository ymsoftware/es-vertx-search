package org.ap.search.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.ap.search.FieldRange;
import org.ap.search.SearchRequest;
import org.ap.search.SearchService;
import org.ap.search.es.ElasticSearchService;
import org.ap.search.es.ElasticSearchServiceConfig;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.HashMap;
import java.util.Map;

import static org.ap.search.Helpers.*;

/**
 * Created by ymetelkin on 7/16/15.
 */
public class SearchServiceVerticle extends AbstractVerticle {

    private Client client = null;
    private SearchService service = null;
    //private EventBusHandler ebHandler = null;

    // Called when verticle is deployed
    @Override
    public void start() {

        vertx.executeBlocking(future -> {
            JsonObject config = this.config().getJsonObject("es");

            String host = config.getString("host", "localhost");
            Integer port = config.getInteger("port", 9300);
            InetSocketTransportAddress address = new InetSocketTransportAddress(host, port);
            this.client = new TransportClient().addTransportAddress(address);

            ElasticSearchServiceConfig es = new ElasticSearchServiceConfig()
                    .setIndex(config.getString("index", "appl"))
                    .setType(config.getString("type", "doc"));

            JsonArray ja = config.getJsonArray("fields", null);
            if (ja != null && ja.size() > 0) {
                Map<String, Float> fields = new HashMap<String, Float>();
                ja.stream().forEach(e -> {
                    String field = (String) e;
                    String[] tokens = field.split("\\^");
                    Float weight = tokens.length == 1 ? 1f : Float.parseFloat(tokens[1]);
                    fields.put(tokens[0], weight);
                });
                es.setFields(fields);
            }

            this.service = new ElasticSearchService(this.client, es);
            //this.ebHandler = new EventBusHandler(vertx);

            Router router = Router.router(vertx);

            router.route().handler(CorsHandler.create("*")
                    .allowedMethod(HttpMethod.GET)
                    .allowedMethod(HttpMethod.POST));

            router.route().handler(BodyHandler.create());
            router.post("/search").blockingHandler(this::search, false);
            //router.route("/eventbus/*").handler(this.ebHandler.handle());

            vertx.createHttpServer().requestHandler(router::accept).listen(this.config().getInteger("port", 9900));

            future.complete(this.client);
        }, res -> {
            System.out.println("SearchServiceVerticle started: " + res.result());
        });
    }

    // Optional - called when verticle is undeployed
    @Override
    public void stop() {
        if (this.client != null) {
            this.client.close();
        }

        System.out.println("SearchServiceVerticle stopped");
    }

    private void search(RoutingContext routingContext) {
        SearchRequest request = getRequest(routingContext);

        String result = this.service.search(request);

        //vertx.eventBus().publish(Events.SEARCH, jo);

        routingContext.response().putHeader("content-type", "application/json").end(result);
    }

    private SearchRequest getRequest(RoutingContext routingContext) {
        JsonObject jo = routingContext.getBodyAsJson();

        SearchRequest request = new SearchRequest();

        String query = jo.getString("query", null);
        if (query != null) {
            request.setQuery(query);
        }

        String[] types = Helpers.toStringArray(jo.getJsonArray("media_types", null));
        if (types != null) {
            request.setMediaTypes(types);
        }

        String[] fields = Helpers.toStringArray(jo.getJsonArray("fields", null));
        if (fields != null) {
            request.setFields(fields);
        }

        Integer[] include = Helpers.toIntegerArray(jo.getJsonArray("entitlements.include", null));
        if (include != null) {
            request.setIncludeProducts(include);
        }

        Integer[] exclude = Helpers.toIntegerArray(jo.getJsonArray("entitlements.exclude", null));
        if (exclude != null) {
            request.setExcludeProducts(exclude);
        }


        Integer from = jo.getInteger("skip", 0);
        if (from > 0) {
            request.setFrom(from);
        }

        Integer size = jo.getInteger("take", 0);
        if (size > 0) {
            request.setSize(size);
        }

        FieldRange<String> range = getRangeFilter(jo.getJsonObject("date_range", null));
        if (range != null) {
            request.setDateRange(range);
        }

        return request;
    }

    private FieldRange<String> getRangeFilter(JsonObject jo) {
        if (jo != null) {
            String field = jo.getString("field", null);
            if (field != null && field.length() > 0) {
                FieldRange<String> range = new FieldRange<String>().setField(field);

                String lower = jo.getString("gte", null);
                if (!isNullOrWhiteSpace(lower)) {
                    range.gte(lower);
                } else {
                    lower = jo.getString("gt", null);
                    if (!isNullOrWhiteSpace(lower)) {
                        range.gt(lower);
                    }
                }

                String upper = jo.getString("lte", null);
                if (!isNullOrWhiteSpace(upper)) {
                    range.lte(upper);
                } else {
                    upper = jo.getString("lt", null);
                    if (!isNullOrWhiteSpace(upper)) {
                        range.lt(upper);
                    } else {
                        range.lt("now");
                    }
                }

                return range;
            }
        }

        return null;
    }
}
