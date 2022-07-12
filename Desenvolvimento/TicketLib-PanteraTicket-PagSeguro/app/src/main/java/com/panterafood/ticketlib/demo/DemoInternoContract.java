package com.panterafood.ticketlib.demo;

import com.hannesdorfmann.mosby.mvp.MvpView;

import org.json.JSONException;

import java.io.IOException;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;

interface DemoInternoContract extends MvpView{

    void showTransactionSuccess();

    void showError(String message);

    void showMessage(String message);

    void showLoading(boolean show);

    void writeToFile(String transactionCode, String transactionId);

    void showAbortedSuccessfully();

    void disposeDialog();

    void showActivationDialog();

    void showAuthProgress(String message);

    void showSucess();

    void showSuccessWrite(PlugPagNFCResult result);
    void showSuccessRead(PlugPagNFCResult result) throws JSONException, IOException;

}
