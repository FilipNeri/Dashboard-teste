package com.panterafood.ticketlib;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PagSeguro {
    @SerializedName("formaPagto")
    @Expose
    private String formaPagto;

    @SerializedName("parcelas")
    @Expose
    private int parcelas;

    @SerializedName("valorTotal")
    @Expose
    private Double valorTotal;

    @SerializedName("referencia")
    @Expose
    private String referencia;

    public String getFormaPagto() {
        return formaPagto;
    }

    public void setFormaPagto(String formaPagto) {
        this.formaPagto = formaPagto;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    @Override
    public String toString() {
        return "PagSeguro{" +
                "formaPagto='" + formaPagto + '\'' +
                ", parcelas=" + parcelas +
                ", valorTotal=" + valorTotal +
                ", referencia='" + referencia + '\'' +
                '}';
    }
}
