package com.panterafood.ticketlib;

import com.hannesdorfmann.mosby.mvp.MvpView;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;


public interface NFCContract extends MvpView {
    void showSuccess(PlugPagNFCResult result);

    void showError(String message);
}
