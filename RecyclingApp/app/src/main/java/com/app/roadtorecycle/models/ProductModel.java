package com.app.roadtorecycle.models;

public class ProductModel {

   private String productId;
   private String productName;
   private String productImg;

    public ProductModel(String productId, String productName, String productImg) {
        this.productId = productId;
        this.productName = productName;
        this.productImg = productImg;
    }

    public ProductModel() {
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

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }
}
