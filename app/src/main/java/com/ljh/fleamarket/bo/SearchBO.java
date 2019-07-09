package com.ljh.fleamarket.bo;

/**
 * Created by ASUS on 2019/3/29.
 */

public class SearchBO {
    private int opType;     //操作类型（发布，维护等）
    private String token;    //token
    private int pageNumber;
    private int pageSize;
    private String goodsType;
    private String getMoreFlag;
    private String goodsKeyWord;

    public int getOpType() {
        return opType;
    }

    public void setOpType(int opType) {
        this.opType = opType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGetMoreFlag() {
        return getMoreFlag;
    }

    public void setGetMoreFlag(String getMoreFlag) {
        this.getMoreFlag = getMoreFlag;
    }

    public String getGoodsKeyWord() {
        return goodsKeyWord;
    }

    public void setGoodsKeyWord(String goodsKeyWord) {
        this.goodsKeyWord = goodsKeyWord;
    }
}
