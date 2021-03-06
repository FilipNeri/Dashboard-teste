package com.panterafood.ticketlib.demo;

import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import com.hannesdorfmann.mosby.mvp.MvpNullObjectBasePresenter;

import javax.inject.Inject;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagEventData;
import com.panterafood.ticketlib.ActionResult;
import com.panterafood.ticketlib.NFC;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DemoInternoPresenter extends MvpNullObjectBasePresenter<DemoInternoContract> {

    private DemoInternoUseCase mUseCase;
    private Disposable mSubscribe;
    private Boolean hasAborted = false;
    private int countPassword = 0;

    @Inject
    public DemoInternoPresenter(DemoInternoUseCase useCase) {
        mUseCase = useCase;
    }

    public void creditPayment(int value) {
        doAction(mUseCase.doCreditPayment(value), value);
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

    public void doDebitPayment(int value) {
        doAction(mUseCase.doDebitPayment(value), value);
    }
    public void doPixPayment(int value) {
        doAction(mUseCase.doPixPayment(value), value);
    }
    public void doVoucherPayment(int value) {
        doAction(mUseCase.doVoucherPayment(value), value);
    }

    public void readNFCCard() {
        mSubscribe = mUseCase.readNFCCard()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getView().showSuccessRead(result),
                        throwable -> getView().showError(throwable.getMessage()));
    }

    public void writeNFCCard(NFC nfcWrite) {
        mSubscribe = mUseCase.writeNFCCard(nfcWrite)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> getView().showSuccessWrite(result),
                        throwable -> getView().showError(throwable.getMessage()));
    }

    public void doRefund(ActionResult actionResult) {
        if (actionResult.getMessage() != null) {
            getView().showError(actionResult.getMessage());
            getView().disposeDialog();

            if (actionResult.getMessage().contains("ser estornada")){ //erro
                try {
                    File fl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Estorno.json");
                    fl.delete();

                    //System.exit(0);
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            doAction(mUseCase.doRefund(actionResult), 0);
        }
    }

    private void doAction(Observable<ActionResult> action, int value) {
        mSubscribe = mUseCase.isAuthenticated()
                .filter(aBoolean -> {
                    if (!aBoolean) {
                        getView().showActivationDialog();
                        mSubscribe.dispose();
                    }
                    return aBoolean;
                })
                .flatMap((Function<Boolean, ObservableSource<ActionResult>>) aBoolean -> action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> getView().showTransactionSuccess())
                .doOnDispose(() -> getView().disposeDialog())
                .subscribe((ActionResult result) -> {
                            writeToFile(result);

                            if (result.getEventCode() == PlugPagEventData.EVENT_CODE_NO_PASSWORD ||
                                    result.getEventCode() == PlugPagEventData.EVENT_CODE_DIGIT_PASSWORD) {
                                getView().showMessage(checkMessagePassword(result.getEventCode(), value));
                            } else {
                                getView().showMessage(checkMessage(result.getMessage()));
                            }
                        },
                        throwable -> {
                            getView().showMessage(throwable.getMessage());
                            getView().disposeDialog();
                        });
    }

    private String checkMessagePassword(int eventCode, int value) {
        StringBuilder strPassword = new StringBuilder();

        if (eventCode == PlugPagEventData.EVENT_CODE_DIGIT_PASSWORD) {
            countPassword++;
        }
        if (eventCode == PlugPagEventData.EVENT_CODE_NO_PASSWORD) {
            countPassword = 0;
        }

        for (int count = countPassword; count > 0; count--) {
            strPassword.append("*");
        }

        return String.format("VALOR: %.2f\nSENHA: %s", (value / 100.0), strPassword.toString());
    }

    private String checkMessage(String message) {
        if (message != null && !message.isEmpty() && message.contains("SENHA")) {
            String[] strings = message.split("SENHA");
            return strings[0].trim();
        }

        return message;
    }

    private void writeToFile(ActionResult result) {
        if (result.getTransactionCode() != null && result.getTransactionId() != null) {
            try {

                getView().writeToFile(result.getTransactionCode(), result.getTransactionId());

                File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File arquivo = new File(root, "Transacao.json");
                JSONObject dados = new JSONObject("{\"transactionCode\":" + result.getTransactionCode() + ",\"transactionId\":\"" + result.getTransactionId() + "\"}");

                if (!arquivo.exists()) {
                    //cria um arquivo (vazio)
                    arquivo.createNewFile();
                }
                //caso seja um diret??rio, ?? poss??vel listar seus arquivos e diret??rios
                File[] arquivos = arquivo.listFiles();
                //escreve no arquivo
                FileWriter fw = new FileWriter(arquivo, true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write("Texto a ser escrito no txt");
                bw.newLine();
                bw.close();
                fw.close();

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void abortTransaction() {
        mSubscribe = mUseCase.abort()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void detachView(boolean retainInstance) {
        if (mSubscribe != null) {
            mSubscribe.dispose();
        }
        super.detachView(retainInstance);
    }

    public void activate(String activationCode) {
        mSubscribe = mUseCase.initializeAndActivatePinpad(activationCode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(disposable -> getView().showLoading(true))
                .doOnComplete(() -> {
                    getView().showLoading(false);
                    getView().disposeDialog();
                })
                .doOnDispose(() -> getView().disposeDialog())
                .subscribe(actionResult -> getView().showAuthProgress(actionResult.getMessage()),
                        throwable -> {
                            getView().showLoading(false);
                            getView().showError(throwable.getMessage());
                        });
    }

    public void getLastTransaction() {
       mSubscribe = mUseCase.getLastTransaction()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(actionResult -> getView().showMessage(actionResult.getTransactionCode()),
                        throwable -> getView().showError(throwable.getMessage()));
    }
}
