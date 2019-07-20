package com.ljh.fleamarket.bo;

public class CollectBO {
    private String userId;
    private int goodsId;
    private int OpType;
    private int flag;
    private String message;
    private String token;    //token
    private int pageNumber;
    private int pageSize;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public int getOpType() {
        return OpType;
    }

    public void setOpType(int opType) {
        OpType = opType;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CollectBO{" +
                "userId='" + userId + '\'' +
                ", goodsId=" + goodsId +
                ", OpType=" + OpType +
                ", flag=" + flag +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                '}';
    }
}
