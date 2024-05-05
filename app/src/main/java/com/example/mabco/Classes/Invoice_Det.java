package com.example.mabco.Classes;

import com.google.gson.annotations.SerializedName;

public class Invoice_Det {@SerializedName("Qty")
private Long qty;

    @SerializedName("Price")
    private String price;


    @SerializedName("Stk_code")
    private String stk_code;

    @SerializedName("Cat_code")
    private String cat_code;

    @SerializedName("Mobile_slno")
    private String mobile_slno;

    @SerializedName("stk_desc")
    private String stk_desc;

    public String getWarranty() {
        return warranty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    @SerializedName("warranty")
    private String warranty;


    public Invoice_Det(String price, String stk_code, String cat_code, String mobile_slno, String stk_desc , String warranty ) {
        this.price = price;
        this.stk_code = stk_code;
        this.cat_code = cat_code;
        this.mobile_slno = mobile_slno;
        this.stk_desc = stk_desc;
        this.warranty = warranty;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }



    public String getStk_desc() {
        return stk_desc;
    }

    public void setStk_desc(String stk_desc) {
        this.stk_desc = stk_desc;
    }

    public String getStk_code() {
        return stk_code;
    }

    public void setStk_code(String stk_code) {
        this.stk_code = stk_code;
    }

    public String getCat_code() {
        return cat_code;
    }

    public void setCat_code(String cat_code) {
        this.cat_code = cat_code;
    }

    public String getMobile_slno() {
        return mobile_slno;
    }

    public void setMobile_slno(String mobile_slno) {
        this.mobile_slno = mobile_slno;
    }
}
