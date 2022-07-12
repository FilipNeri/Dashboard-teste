package com.panterafood.ticketlib.auth;

import com.hannesdorfmann.mosby.mvp.MvpView;

public interface AuthContract extends MvpView {

    void showIsAuthenticated(Boolean isAuthenticated);

    void showError(String message);

    void showActivatedSuccessfully();

    void showDeactivatedSuccessfully();

    void showLoading(boolean show);
}
