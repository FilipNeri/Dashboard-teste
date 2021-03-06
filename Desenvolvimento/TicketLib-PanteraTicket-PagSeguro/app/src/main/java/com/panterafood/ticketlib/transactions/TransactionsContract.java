package com.panterafood.ticketlib.transactions;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface TransactionsContract extends MvpView {

    void showTransactionSuccess();

    void showTransactionSuccess(String message);

    void showError(String message);

    void showMessage(String message);

    void showLoading(boolean show);

    void writeToFile(String transactionCode, String transactionId);

    void showAbortedSuccessfully();

    void showPrintError(String message);

    void showLastTransaction(String transactionCode);
}
