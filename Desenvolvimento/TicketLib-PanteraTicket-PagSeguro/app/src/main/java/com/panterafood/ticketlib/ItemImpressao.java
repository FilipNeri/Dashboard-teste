package com.panterafood.ticketlib;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemImpressao {
    @SerializedName("cd")
    @Expose
    private String cd;

    @SerializedName("subtot")
    @Expose
    private String subtot;

    @SerializedName("qtd")
    @Expose
    private String qtd;

    @SerializedName("vl_unit")
    @Expose
    private Double vl_unit;

    @SerializedName("nm_prod")
    @Expose
    private String nm_prod;

    @SerializedName("cd_ficha")
    @Expose
    private String cd_ficha;

    @SerializedName("dt")
    @Expose
    private String dt;

    @SerializedName("forma_pgto")
    @Expose
    private String forma_pgto;

    @SerializedName("evento")
    @Expose
    private String evento;

    @SerializedName("url_evento")
    @Expose
    private String url_evento;

    @SerializedName("msg1")
    @Expose
    private String msg1;

    @SerializedName("msg2")
    @Expose
    private String msg2;

    @SerializedName("isSegundaVia")
    @Expose
    private boolean isSegundaVia;

    @SerializedName("senha")
    @Expose
    private String senha;

    @SerializedName("serial")
    @Expose
    private String serial;

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isSegundaVia() {
        return isSegundaVia;
    }

    public void setSegundaVia(boolean segundaVia) {
        isSegundaVia = segundaVia;
    }

    public String getMsg1() {
        return msg1;
    }

    public void setMsg1(String msg1) {
        this.msg1 = msg1;
    }

    public String getMsg2() {
        return msg2;
    }

    public void setMsg2(String msg2) {
        this.msg2 = msg2;
    }

    public String getUrl_evento() {
        return url_evento;
    }

    public void setUrl_evento(String url_evento) {
        this.url_evento = url_evento;
    }

    public String getForma_pgto() {
        return forma_pgto;
    }

    public void setForma_pgto(String forma_pgto) {
        this.forma_pgto = forma_pgto;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getCd_ficha() {
        return cd_ficha;
    }

    public void setCd_ficha(String cd_ficha) {
        this.cd_ficha = cd_ficha;
    }

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getSubtot() {
        return subtot;
    }

    public void setSubtot(String subtot) {
        this.subtot = subtot;
    }

    public String getQtd() {
        return qtd;
    }

    public void setQtd(String qtd) {
        this.qtd = qtd;
    }

    public Double getVl_unit() {
        return vl_unit;
    }

    public void setVl_unit(Double vl_unit) {
        this.vl_unit = vl_unit;
    }

    public String getNm_prod() {
        return nm_prod;
    }

    public void setNm_prod(String nm_prod) {
        this.nm_prod = nm_prod;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return "ItemImpressao{" +
                "cd='" + cd + '\'' +
                ", subtot='" + subtot + '\'' +
                ", qtd='" + qtd + '\'' +
                ", vl_unit=" + vl_unit +
                ", nm_prod='" + nm_prod + '\'' +
                ", serial='" + serial + '\'' +
                '}';
    }
}
