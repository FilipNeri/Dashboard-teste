package com.panterafood.ticketlib;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemFicha {
    @SerializedName("cd")
    @Expose
    private int cd;

    @SerializedName("cd_ficha")
    @Expose
    private int cd_ficha;

    @SerializedName("cd_prod")
    @Expose
    private int cd_prod;

    @SerializedName("nm_prod")
    @Expose
    private String nm_prod;

    @SerializedName("vl_unit")
    @Expose
    private double vl_unit;

    @SerializedName("qtd")
    @Expose
    private int qtd;

    @SerializedName("sub_total")
    @Expose
    private double sub_total;

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public int getCd_ficha() {
        return cd_ficha;
    }

    public void setCd_ficha(int cd_ficha) {
        this.cd_ficha = cd_ficha;
    }

    public int getCd_prod() {
        return cd_prod;
    }

    public void setCd_prod(int cd_prod) {
        this.cd_prod = cd_prod;
    }

    public String getNm_prod() {
        return nm_prod;
    }

    public void setNm_prod(String nm_prod) {
        this.nm_prod = nm_prod;
    }

    public double getVl_unit() {
        return vl_unit;
    }

    public void setVl_unit(double vl_unit) {
        this.vl_unit = vl_unit;
    }

    public int getQtd() {
        return qtd;
    }

    public void setQtd(int qtd) {
        this.qtd = qtd;
    }

    public double getSub_total() {
        return sub_total;
    }

    public void setSub_total(double sub_total) {
        this.sub_total = sub_total;
    }
}
