package com.example.haball.Shipment.ui.main.Models;

import androidx.lifecycle.ViewModel;

public class Distributor_ShipmentModel {
    private String DeliveryNumber;
    private String DeliveryDate;

    private String goodsreceivenotesReceiveQty;
    private String deliveryNoteStatus;

    public Distributor_ShipmentModel() {
    }

    public Distributor_ShipmentModel(String deliveryNumber, String deliveryDate,  String goodsreceivenotesReceiveQty, String deliveryNoteStatus) {
        DeliveryNumber = deliveryNumber;
        DeliveryDate = deliveryDate;
        this.goodsreceivenotesReceiveQty = goodsreceivenotesReceiveQty;
        this.deliveryNoteStatus = deliveryNoteStatus;
    }

    public String getDeliveryNumber() {
        return DeliveryNumber;
    }

    public void setDeliveryNumber(String deliveryNumber) {
        DeliveryNumber = deliveryNumber;
    }

    public String getDeliveryDate() {
        return DeliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        DeliveryDate = deliveryDate;
    }



    public String getGoodsreceivenotesReceiveQty() {
        return goodsreceivenotesReceiveQty;
    }

    public void setGoodsreceivenotesReceiveQty(String goodsreceivenotesReceiveQty) {
        this.goodsreceivenotesReceiveQty = goodsreceivenotesReceiveQty;
    }

    public String getDeliveryNoteStatus() {
        return deliveryNoteStatus;
    }

    public void setDeliveryNoteStatus(String deliveryNoteStatus) {
        this.deliveryNoteStatus = deliveryNoteStatus;
    }
}
