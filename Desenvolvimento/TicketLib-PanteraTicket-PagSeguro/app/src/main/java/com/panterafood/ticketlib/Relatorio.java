package com.panterafood.ticketlib;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public class Relatorio {
    @SerializedName("tipo")
    @Expose
    private String tipo;

    @SerializedName("vl")
    @Expose
    private String vl;

    @SerializedName("caixa")
    @Expose
    private String caixa;

    @SerializedName("url_evento")
    @Expose
    private String url_evento;

    @SerializedName("evento")
    @Expose
    private String evento;

    @SerializedName("dt_abert")
    @Expose
    private String dt_abert;

    @SerializedName("vl_abert")
    @Expose
    private String vl_abert;

    @SerializedName("dt_fecha")
    @Expose
    private String dt_fecha;

    @SerializedName("cd_usu")
    @Expose
    private int cd_usu;

    @SerializedName("usu")
    @Expose
    private String usu;

    @SerializedName("vl_din")
    @Expose
    private String vl_din;

    @SerializedName("vl_che")
    @Expose
    private String vl_che;

    @SerializedName("vl_fia")
    @Expose
    private String vl_fia;

    @SerializedName("vl_car")
    @Expose
    private String vl_car;

    @SerializedName("vl_cor")
    @Expose
    private String vl_cor;

    @SerializedName("vl_outra")
    @Expose
    private String vl_outra;

    @SerializedName("vl_pix")
    @Expose
    private String vl_pix;

    @SerializedName("vl_computado_din")
    @Expose
    private String vl_computado_din;

    @SerializedName("vl_computado_car")
    @Expose
    private String vl_computado_car;

    @SerializedName("vl_computado_cortesia")
    @Expose
    private String vl_computado_cortesia;

    @SerializedName("vl_computado_outra")
    @Expose
    private String vl_computado_outra;

    @SerializedName("vl_computado_pix")
    @Expose
    private String vl_computado_pix;

    @SerializedName("vl_computado_sangria")
    @Expose
    private String vl_computado_sangria;

    @SerializedName("vl_computado_suprimento")
    @Expose
    private String vl_computado_suprimento;

    @SerializedName("vl_computado_estornos")
    @Expose
    private String vl_computado_estornos;

    @SerializedName("serial")
    @Expose
    private String serial;

    @SerializedName("itens")
    @Expose
    private ItemFicha[] itens;

    @SerializedName("itensCortesia")
    @Expose
    private ItemFicha[] itensCortesia;

    public ItemFicha[] getItensCortesia() {
        return itensCortesia;
    }

    public void setItensCortesia(ItemFicha[] itensCortesia) {
        this.itensCortesia = itensCortesia;
    }

    @SerializedName("produtos")
    @Expose
    private Produto[] produtos;

    public String getUrl_evento() {
        return url_evento;
    }

    public void setUrl_evento(String url_evento) {
        this.url_evento = url_evento;
    }

    public String getVl_computado_estornos() {
        return vl_computado_estornos;
    }

    public void setVl_computado_estornos(String vl_computado_estornos) {
        this.vl_computado_estornos = vl_computado_estornos;
    }

    public Produto[] getProdutos() {
        return produtos;
    }

    public void setProdutos(Produto[] produtos) {
        this.produtos = produtos;
    }


    public String getVl_cor() {
        return vl_cor;
    }

    public void setVl_cor(String vl_cor) {
        this.vl_cor = vl_cor;
    }

    public String getVl_computado_sangria() {
        return vl_computado_sangria;
    }

    public void setVl_computado_sangria(String vl_computado_sangria) {
        this.vl_computado_sangria = vl_computado_sangria;
    }

    public String getVl_computado_suprimento() {
        return vl_computado_suprimento;
    }

    public void setVl_computado_suprimento(String vl_computado_suprimento) {
        this.vl_computado_suprimento = vl_computado_suprimento;
    }

    public String getVl_outra() {
        return vl_outra;
    }

    public void setVl_outra(String vl_outra) {
        this.vl_outra = vl_outra;
    }

    public String getVl_computado_outra() {
        return vl_computado_outra;
    }

    public void setVl_computado_outra(String vl_computado_outra) {
        this.vl_computado_outra = vl_computado_outra;
    }
    public String getVl_pix() {
        return vl_pix;
    }

    public void setVl_pix(String vl_pix) {
        this.vl_pix = vl_pix;
    }

    public String getVl_computado_pix() {
        return vl_computado_pix;
    }

    public void setVl_computado_pix(String vl_computado_pix) {
        this.vl_computado_pix = vl_computado_pix;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getVl() {
        return vl;
    }

    public void setVl(String vl) { this.vl = vl; }

    public String getCaixa() {
        return caixa;
    }

    public void setCaixa(String caixa) {
        this.caixa = caixa;
    }

    public String getDt_abert() {
        return dt_abert;
    }

    public void setDt_abert(String dt_abert) {
        this.dt_abert = dt_abert;
    }

    public String getVl_abert() {
        return vl_abert;
    }

    public void setVl_abert(String vl_abert) {
        this.vl_abert = vl_abert;
    }

    public String getDt_fecha() {
        return dt_fecha;
    }

    public void setDt_fecha(String dt_fecha) {
        this.dt_fecha = dt_fecha;
    }

    public int getCd_usu() {
        return cd_usu;
    }

    public void setCd_usu(int cd_usu) {
        this.cd_usu = cd_usu;
    }

    public String getUsu() {
        return usu;
    }

    public void setUsu(String usu) {
        this.usu = usu;
    }

    public String getVl_din() {
        return vl_din;
    }

    public void setVl_din(String vl_din) {
        this.vl_din = vl_din;
    }

    public String getVl_che() {
        return vl_che;
    }

    public void setVl_che(String vl_che) {
        this.vl_che = vl_che;
    }

    public String getVl_fia() {
        return vl_fia;
    }

    public void setVl_fia(String vl_fia) {
        this.vl_fia = vl_fia;
    }

    public String getVl_car() {
        return vl_car;
    }

    public void setVl_car(String vl_car) {
        this.vl_car = vl_car;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getVl_computado_din() {
        return vl_computado_din;
    }

    public void setVl_computado_din(String vl_computado_din) {
        this.vl_computado_din = vl_computado_din;
    }

    public String getVl_computado_car() {
        return vl_computado_car;
    }

    public void setVl_computado_car(String vl_computado_car) {
        this.vl_computado_car = vl_computado_car;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public ItemFicha[] getItens() {
        return itens;
    }

    public void setItens(ItemFicha[] itens) {
        this.itens = itens;
    }

    public String getVl_computado_cortesia() {
        return vl_computado_cortesia;
    }

    public void setVl_computado_cortesia(String vl_computado_cortesia) {
        this.vl_computado_cortesia = vl_computado_cortesia;
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "tipo='" + tipo + '\'' +
                ", vl='" + vl + '\'' +
                ", caixa='" + caixa + '\'' +
                ", evento='" + evento + '\'' +
                ", dt_abert='" + dt_abert + '\'' +
                ", vl_abert='" + vl_abert + '\'' +
                ", dt_fecha='" + dt_fecha + '\'' +
                ", cd_usu=" + cd_usu +
                ", usu='" + usu + '\'' +
                ", vl_din='" + vl_din + '\'' +
                ", vl_che='" + vl_che + '\'' +
                ", vl_fia='" + vl_fia + '\'' +
                ", vl_car='" + vl_car + '\'' +
                ", vl_computado_din='" + vl_computado_din + '\'' +
                ", vl_computado_car='" + vl_computado_car + '\'' +
                ", vl_computado_cortesia='" + vl_computado_cortesia + '\'' +
                ", serial='" + serial + '\'' +
                ", itens=" + Arrays.toString(itens) +
                '}';
    }
}
