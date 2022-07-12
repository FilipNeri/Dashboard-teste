package com.panterafood.ticketlib.printer;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface PrinterContract extends MvpView{

    void showSucess();

    void showError(String message);

    void showLoading(boolean show);
}
