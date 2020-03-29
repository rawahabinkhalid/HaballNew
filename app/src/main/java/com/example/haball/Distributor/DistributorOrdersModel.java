package com.example.haball.Distributor;

public class DistributorOrdersModel {
    private String ID;
    private String OrderNumber;
    private String CompanyName;
    private String TotalPrice;
    private String NetPrice;
    private String OrderStatusValue;
    private String CreatedDate;
    private String Status;

    public DistributorOrdersModel(String ID, String orderNumber, String companyName, String totalPrice, String netPrice, String orderStatusValue, String createdDate, String status) {
        this.ID = ID;
        OrderNumber = orderNumber;
        CompanyName = companyName;
        TotalPrice = totalPrice;
        NetPrice = netPrice;
        OrderStatusValue = orderStatusValue;
        CreatedDate = createdDate;
        Status = status;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getCompanyName() {
        return CompanyName;
    }

    public void setCompanyName(String companyName) {
        CompanyName = companyName;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getNetPrice() {
        return NetPrice;
    }

    public void setNetPrice(String netPrice) {
        NetPrice = netPrice;
    }

    public String getOrderStatusValue() {
        return OrderStatusValue;
    }

    public void setOrderStatusValue(String orderStatusValue) {
        OrderStatusValue = orderStatusValue;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
