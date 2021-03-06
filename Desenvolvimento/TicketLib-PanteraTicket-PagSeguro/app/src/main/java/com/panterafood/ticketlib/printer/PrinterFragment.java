package com.panterafood.ticketlib.printer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hannesdorfmann.mosby.mvp.MvpFragment;

import com.panterafood.ticketlib.HomeFragment;
import com.panterafood.ticketlib.MainActivity;
import com.panterafood.ticketlib.R;
import com.panterafood.ticketlib.injection.DaggerPrinterComponent;
import com.panterafood.ticketlib.injection.PrinterComponent;
import com.panterafood.ticketlib.injection.UseCaseModule;
import com.panterafood.ticketlib.utils.UIFeedback;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrinterFragment extends MvpFragment<PrinterContract, PrinterPresenter> implements PrinterContract, HomeFragment {

    PrinterComponent mInjector;

    public static PrinterFragment getInstance() {
        return new PrinterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mInjector = DaggerPrinterComponent
                .builder()
                .mainComponent(((MainActivity) getContext()).getMainComponent())
                .useCaseModule(new UseCaseModule())
                .build();
        mInjector.inject(this);
        View rootview = inflater.inflate(R.layout.fragment_printer, container, false);
        ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public PrinterPresenter createPresenter() {
        return mInjector.presenter();
    }

    @OnClick(R.id.btn_print)
    public void onPrintFileClicked() {
        getPresenter().printFile();
    }

    @Override
    public void showSucess() {
        Snackbar.make(getView(), R.string.printer_print_success, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
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
