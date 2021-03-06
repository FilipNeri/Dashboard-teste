package com.panterafood.ticketlib.auth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import javax.inject.Inject;

import com.panterafood.ticketlib.HomeFragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.panterafood.ticketlib.MainActivity;
import com.panterafood.ticketlib.R;
import com.panterafood.ticketlib.injection.AuthComponent;
import com.panterafood.ticketlib.injection.DaggerAuthComponent;
import com.panterafood.ticketlib.utils.UIFeedback;

public class AuthFragment extends MvpFragment<AuthContract, AuthPresenter> implements AuthContract, HomeFragment {

    @Inject
    AuthComponent mInjector;

    public static AuthFragment getInstance() {
        return new AuthFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInjector = DaggerAuthComponent.builder()
                .mainComponent(((MainActivity) getContext()).getMainComponent())
                .build();
        mInjector.inject(this);
        View rootView = inflater.inflate(R.layout.fragment_auth, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @OnClick(R.id.btn_authentication_check)
    public void onCheckAuthClicked() {
        getPresenter().checkIsAuthenticated();
    }

    @OnClick(R.id.btn_authentication_request)
    public void onRequestAuthClicked() {
        getPresenter().requestAuth();
    }

    @OnClick(R.id.btn_authentication_invalidate)
    public void deactivate() {
        getPresenter().deactivate();
    }

    @Override
    public AuthPresenter createPresenter() {
        return mInjector.presenter();
    }

    @Override
    public void showIsAuthenticated(Boolean isAuthenticated) {
        UIFeedback.showDialog(getContext(), isAuthenticated ?
                R.string.auth_is_authenticated : R.string.auth_isnt_authenticated);
    }

    @Override
    public void showError(String message) {
        UIFeedback.showDialog(getContext(), message);
    }

    @Override
    public void showActivatedSuccessfully() {
        UIFeedback.showDialog(getContext(), R.string.auth_activated_successfully);
    }

    @Override
    public void showDeactivatedSuccessfully() {
        UIFeedback.showDialog(getContext(), R.string.auth_deactivated_successfully);
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            UIFeedback.showProgress(getContext());
        } else {
            UIFeedback.dismissProgress();
        }
    }
}
