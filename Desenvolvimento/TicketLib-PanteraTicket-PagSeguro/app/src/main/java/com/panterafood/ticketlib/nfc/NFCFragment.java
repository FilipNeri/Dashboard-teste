package com.panterafood.ticketlib.nfc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import java.io.UnsupportedEncodingException;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import com.panterafood.ticketlib.HomeFragment;
import com.panterafood.ticketlib.MainActivity;
import com.panterafood.ticketlib.R;
import com.panterafood.ticketlib.injection.DaggerNFCComponent;
import com.panterafood.ticketlib.utils.UIFeedback;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.panterafood.ticketlib.injection.NFCComponent;
import com.panterafood.ticketlib.injection.UseCaseModule;

public class NFCFragment extends MvpFragment<NFCContract, NFCPresenter> implements NFCContract, HomeFragment {

    NFCComponent mInjector;

    public static NFCFragment getInstance() {
        return new NFCFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInjector = DaggerNFCComponent.builder()
                .useCaseModule(new UseCaseModule())
                .mainComponent(((MainActivity) getContext()).getMainComponent())
                .build();
        mInjector.inject(this);
        View rootview = inflater.inflate(R.layout.fragment_nfc, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public NFCPresenter createPresenter() {
        return mInjector.presenter();
    }

    @OnClick(R.id.btn_nfc_read)
    public void onReadCardClicked() {
        getPresenter().readNFCCard();
    }

    @OnClick(R.id.btn_nfc_write)
    public void onWriteCardClicked() {
        getPresenter().writeNFCCard();
    }

    @OnClick(R.id.btn_nfc_abort)
    public void onAbortClicked() {
        getPresenter().abort();
    }

    @Override
    public void showSuccess(PlugPagNFCResult result) {
        try {
            UIFeedback.showDialog(getContext(), "Valor do slot: " + new String(result.getSlots()[result.getStartSlot()].get("data"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}
