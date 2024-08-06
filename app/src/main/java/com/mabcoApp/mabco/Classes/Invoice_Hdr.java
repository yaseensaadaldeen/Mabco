package com.mabcoApp.mabco.Classes;

import java.util.ArrayList;

public class Invoice_Hdr {
    private String invoice_no , offerNo = "-1" , location , customerName , date,totalPrice , totalFinalPrice , discount;
    private ArrayList<Invoice_Det> invoice_det ;

    public Invoice_Hdr(String invoice_no, String totalPrice, String totalFinalPrice, String discount,
                       String offerNo, String location, String customerName, String date) {
        this.invoice_no = invoice_no;
        this.totalPrice = totalPrice;
        this.totalFinalPrice = totalFinalPrice;
        this.discount = discount;
        this.offerNo = offerNo;
        this.location = location;
        this.customerName = customerName;
        this.date = date;
    }



    @Override
    public String toString() {
        return this.invoice_no;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalFinalPrice() {
        return totalFinalPrice;
    }

    public void setTotalFinalPrice(String totalFinalPrice) {
        this.totalFinalPrice = totalFinalPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOfferNo() {
        return offerNo;
    }

    public void setOfferNo(String offerNo) {
        this.offerNo = offerNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public ArrayList<Invoice_Det> getInvoice_det() {
        return invoice_det;
    }

    public void setInvoice_det(ArrayList<Invoice_Det> invoice_det) {
        this.invoice_det = invoice_det;
    }

    public String getDate() {
        return date.substring(0,16);
    }

    public void setDate(String date) {
        this.date = date;
    }
}
