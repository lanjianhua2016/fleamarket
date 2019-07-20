package com.ljh.fleamarket.bo;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by ASUS on 2019/3/27.
 */
// goodsname can not be null,unit can not be null,quality should bigger than 0,price can not be null and should bigger than 0)"}
public class Goods implements Serializable {
    private byte[] goodsImg;    //商品图片
    private String goodsName;   //商品名称
    private float price;        //价格
    private String unit;         //单位
    private float quality;      //数量
    private String goodsType;   //商品所属类
    private String userid;       //发布人ID
    private String contact;      //联系方式
    private String description; //商品描述
    private String goodsTypeName; //所属类名（可选字段
    private int goodsID;          //商品编号

    private String token;    //token
    private int opType;     //操作类型（发布，维护等）

    public int getGoodsID() {
        return goodsID;
    }

    public void setGoodsID(int goodsID) {
        this.goodsID = goodsID;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getQuality() {
        return quality;
    }

    public void setQuality(float quality) {
        this.quality = quality;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public byte[] getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(byte[] goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "goodsImg=" + Arrays.toString(goodsImg) +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", quality=" + quality +
                ", goodsType='" + goodsType + '\'' +
                ", userid='" + userid + '\'' +
                ", contact='" + contact + '\'' +
                ", description='" + description + '\'' +
                ", goodsTypeName='" + goodsTypeName + '\'' +
                ", goodsID=" + goodsID +
                ", token='" + token + '\'' +
                ", opType=" + opType +
                '}';
    }
}
