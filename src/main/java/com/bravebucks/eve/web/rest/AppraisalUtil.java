package com.bravebucks.eve.web.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.bravebucks.eve.domain.ItemWithQuantity;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AppraisalUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AppraisalUtil.class);

    private AppraisalUtil() {
    }

    static String getLinkFromRaw(String raw) throws UnirestException {
        HttpResponse<String> stringHttpResponse = Unirest.post("http://evepraisal.com/appraisal")
                                                         .field("raw_textarea", raw)
                                                         .field("market", "jita")
                                                         .asString();

        String body = stringHttpResponse.getBody();
        String listingId = body.split("Evepraisal - Appraisal Result ")[1].split(" ")[0];
        return "http://evepraisal.com/a/" + listingId;
    }

    static double getBuy(final String appraisalLink) throws UnirestException {
        String url = appraisalLink + ".json";
        HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
        double sell = 0;
        try {
            sell = jsonResponse.getBody().getObject().getJSONObject("totals").getDouble("buy");
        } catch (JSONException e) {
            LOG.warn("an exception occurred", e);
        }
        return sell;
    }

    static List<ItemWithQuantity> getItems(final String appraisalLink) throws UnirestException {
        List<ItemWithQuantity> result = new ArrayList<>();
        HttpResponse<JsonNode> jsonResponse = Unirest.get(appraisalLink + ".json").asJson();
        JSONObject body = jsonResponse.getBody().getObject();
        JSONArray items = body.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject jsonObject = items.getJSONObject(i);
            int typeID = jsonObject.getInt("typeID");
            String typeName = jsonObject.getString("typeName");
            int quantity = jsonObject.getInt("quantity");
            result.add(new ItemWithQuantity(typeName, typeID, quantity));
        }
        return result.stream()
            .sorted(Comparator.comparing(ItemWithQuantity::getTypeName))
            .collect(Collectors.toList());
    }
}
