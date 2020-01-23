package com.app.roadtorecycle.models;

public class SubProductModel {

   private String productId;
   private String productName;
   private String categoryId;

    public SubProductModel(String productId, String productName, String categoryId) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
    }

    public SubProductModel() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
