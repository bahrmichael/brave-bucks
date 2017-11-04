package com.bravebucks.eve.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Appraisal {

    @Field("raw")
    private String raw;
    @Field("link")
    private String link;
    @Field("additionalRaw")
    private String additionalRaw;
    @Field("totalBuy")
    private Double totalBuy;
    @Field("items")
    private List<ItemWithQuantity> items;

    public Appraisal() {
    }

    public Appraisal(final String raw) {
        this.raw = raw;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(final String raw) {
        this.raw = raw;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public void updateRaw() {
        if (null != additionalRaw) {
            if (raw == null) {
                raw = additionalRaw;
            } else {
                raw += "\n" + additionalRaw;
            }
            additionalRaw = null;
        }
    }

    public void setAdditionalRaw(final String additionalRaw) {
        this.additionalRaw = additionalRaw;
    }

    public String getAdditionalRaw() {
        return additionalRaw;
    }

    public Double getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(final Double totalBuy) {
        this.totalBuy = totalBuy;
    }

    public List<ItemWithQuantity> getItems() {
        return items;
    }

    public void setItems(final List<ItemWithQuantity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Appraisal{" +
               "raw='" + raw + '\'' +
               ", link='" + link + '\'' +
               ", additionalRaw='" + additionalRaw + '\'' +
               ", totalBuy='" + totalBuy + '\'' +
               '}';
    }
}
