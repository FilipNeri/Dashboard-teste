package com.panterafood.ticketlib.printer;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PrinterPresenter extends MvpNullObjectBasePresenter<PrinterContract> {

    private final PrinterUseCase mUseCase;

    private Disposable mSubscribe;

    @Inject
    public PrinterPresenter(PrinterUseCase useCase) {
        mUseCase = useCase;
    }

    public void printFile() {
        mSubscribe = mUseCase.printFile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> getView().showLoading(true))
                .doOnComplete(() -> getView().showLoading(false))
                .subscribe(o -> getView().showSucess(),
                        throwable -> getView().showError(throwable.getMessage()));
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
    }
}
