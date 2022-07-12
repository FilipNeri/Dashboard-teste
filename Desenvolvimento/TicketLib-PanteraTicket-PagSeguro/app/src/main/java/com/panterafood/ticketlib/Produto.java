package com.panterafood.ticketlib;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Produto {
    @SerializedName("cd")
    @Expose
    private int cd;

    @SerializedName("nm")
    @Expose
    private String nm;

    @SerializedName("vl")
    @Expose
    private double vl;

    @SerializedName("preco_custo")
    @Expose
    private double preco_custo;

    @SerializedName("classe")
    @Expose
    private String classe;

    @SerializedName("estoque_min")
    @Expose
    private double estoque_min;

    @SerializedName("unid")
    @Expose
    private String unid;

    public int getCd() {
        return cd;
    }

    public void setCd(int cd) {
        this.cd = cd;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public double getVl() {
        return vl;
    }

    public void setVl(double vl) {
        this.vl = vl;
    }

    public double getPreco_custo() {
        return preco_custo;
    }

    public void setPreco_custo(double preco_custo) {
        this.preco_custo = preco_custo;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public double getEstoque_min() {
        return estoque_min;
    }

    public void setEstoque_min(double estoque_min) {
        this.estoque_min = estoque_min;
    }

    public String getUnid() {
        return unid;
    }

    public void setUnid(String unid) {
        this.unid = unid;
    }
}
