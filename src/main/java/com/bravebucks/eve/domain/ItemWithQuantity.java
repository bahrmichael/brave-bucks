package com.bravebucks.eve.domain;

public class ItemWithQuantity {
    private String typeName;
    private Integer typeID;
    private Integer quantity;

    public ItemWithQuantity() {
    }

    public ItemWithQuantity(final String typeName, final Integer typeID, final Integer quantity) {
        this.typeName = typeName;
        this.typeID = typeID;
        this.quantity = quantity;
    }

    public Integer getTypeID() {
        return typeID;
    }

    public void setTypeID(final Integer typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(final Integer quantity) {
        this.quantity = quantity;
    }
}
