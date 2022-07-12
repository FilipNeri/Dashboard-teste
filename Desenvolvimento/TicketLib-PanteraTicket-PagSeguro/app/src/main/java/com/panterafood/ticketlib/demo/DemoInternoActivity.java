package com.panterafood.ticketlib.demo;

import com.google.gson.Gson;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import android.Manifest;

import javax.inject.Inject;

import com.panterafood.ticketlib.NFC;
import com.panterafood.ticketlib.PagSeguro;
import com.panterafood.ticketlib.R;
import com.panterafood.ticketlib.injection.DaggerDemoInternoComponent;
import com.panterafood.ticketlib.injection.DemoInternoComponent;
import com.panterafood.ticketlib.injection.UseCaseModule;
import com.panterafood.ticketlib.injection.WrapperModule;
import com.panterafood.ticketlib.utils.FileHelper;
import com.panterafood.ticketlib.utils.UIFeedback;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.uol.pagseguro.plugpagservice.wrapper.IPlugPagWrapper;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagAppIdentification;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData;
import butterknife.ButterKnife;

// PRIMEIRA CLASSE
public class DemoInternoActivity extends MvpActivity<DemoInternoContract, DemoInternoPresenter> implements DemoInternoContract {
    private static Context context;

    private static final double COFFEE_VALUE = 1.50;

    private int coffeeAmount = 1;

    CustomDialog dialog;

    @Inject
    DemoInternoComponent mInjector;

    //@BindView(R.id.txtCoffeeAmount)
    //TextView mCoffeeAmountTextview;

    //@BindView(R.id.txtTotalValue)
    //TextView mTotalValue;

    private boolean shouldShowDialog;

    private boolean mCanClick = true;

    private boolean isEstorno = false;

    // Primeira instancia
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DemoInternoActivity.context = getApplicationContext();

        mInjector = DaggerDemoInternoComponent.builder()
                .useCaseModule(new UseCaseModule())
                .wrapperModule(new WrapperModule(getApplicationContext()))
                .build();
        mInjector.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_selection);
        ButterKnife.bind(this);
        dialog = new CustomDialog(this);
        dialog.setOnCancelListener(cancelListener);
        //permição de arquivo
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
        }

        //askForPermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, 4);

        //finallyFunction();
    }

    public static Context getAppContext() {
        return DemoInternoActivity.context;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission with request code 1 granted
                    //Toast.makeText(this, "Permission Granted" , Toast.LENGTH_LONG).show();
                    finallyFunction();
                } else {
                    //permission with request code 1 was not granted
                    Toast.makeText(this, "Aceite as permissões!", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        finishAffinity();
                    }
                    //System.exit(0);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // pedindo as permições
    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        } else {
            finallyFunction();
        }
    }

    @NonNull
    @Override
    public DemoInternoPresenter createPresenter() {
        return mInjector.presenter();
    }

    String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    String getStringFromFile(String filePath) {
        File fl = new File(filePath);
        FileInputStream fin = null;
        String error = "";
        try {
            fin = new FileInputStream(fl);

            String ret = convertStreamToString(fin);

            //Toast.makeText(getApplicationContext(), "Arquivo Encontrado...", Toast.LENGTH_LONG).show();

            //Make sure you close all streams.
            fin.close();

            return ret;
        } catch (FileNotFoundException e) {
            //Toast.makeText(getApplicationContext(), "FileNotFoundException", Toast.LENGTH_LONG).show();
            error = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            error = e.getMessage();
        } catch (Exception e) {
            error = e.getMessage();
        }

        //showMessageError(error);

        return "";
    }

    public static void salvaContador(int contagem, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putInt(key, contagem);
        Log.d("CONTADOR", "salvaContador: " + contagem);

        // editor.apply();
        editor.commit();
    }

    public static int loadContador(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int ret = preferences.getInt(key, -1);

        Log.d("CONTADOR", "loadContador: " + ret);
        return ret;
    }

    // qualquer um que existir ele vai rodar
    void finallyFunction() {
        String error = "";

        try {
            String f = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                f = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/CallPagar.json");
            }
            String imp = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                imp = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/ImpTickets.json");
            }

            String impDireto = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                impDireto = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/ImpTicketsDireto.json");
            }

            String relatorio = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                relatorio = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/ImpRelatorio.json");
            }

            String estorno = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                estorno = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/Estorno.json");
            }

            String nfcRead = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                nfcRead = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/NfcRead.json");
            }
            String nfcWrite = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.FROYO) {
                nfcWrite = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/NfcWrite.json");
            }

            //if(true){
            if (!TextUtils.isEmpty(estorno)) { //ESTORNO
                try {
                    isEstorno = true;
                    onRefund();
                } catch (Exception e) {
                    Log.d("REFUND", e.getMessage());
                }
            } else {
                if (!TextUtils.isEmpty(relatorio)) { //Imprimir relatorio
                    PrintFile();
                } else {
                    if (!TextUtils.isEmpty(nfcRead)) {
                        NFCRead();
                    } else {
                        if (!TextUtils.isEmpty(nfcWrite)) {
                            Gson gson = new Gson();

                            // 1. JSON file to Java object
                            NFC nfcJson = gson.fromJson(nfcWrite, NFC.class);
                            if(nfcJson.getId().length() != 0){
                                int lacunasVazias = 16-nfcJson.getId().length();
                                String lacuna="";
                                for(int i =0; i<lacunasVazias;i++){
                                    lacuna = lacuna +"_";
                                }
                                nfcJson.setId(lacuna+nfcJson.getId());
                            }
                            if(nfcJson.getNum().length() != 0){
                                int lacunasVazias = 16-nfcJson.getNum().length();
                                String lacuna="";
                                for(int i =0; i<lacunasVazias;i++){
                                    lacuna = lacuna +"_";
                                }
                                nfcJson.setNum(lacuna+nfcJson.getNum());
                            }
                            NFCWrite(nfcJson);
                        } else {
                            int teste = loadContador("contador");
                            Log.d("CONTADOR", "Teste Inicio: " + teste);

                            //if(loadContador("contador") != -1){
                            //    PrintFile();
                            //}

                            if (!TextUtils.isEmpty(imp) || !TextUtils.isEmpty(impDireto)) { //Imprimir ticket
                                PrintFile();
                            } else if (!TextUtils.isEmpty(f)) { //Pagar
                                // informações do pagamento
                                Gson gson = new Gson();
                                PagSeguro pagamento = gson.fromJson(f, PagSeguro.class);

                                double d = pagamento.getValorTotal();
                                int total = (int) (d * 100);

                                if (pagamento.getFormaPagto().equals("credito")) {
                                    mCanClick = true;
                                    PagamentoCredito(total);
                                    //Toast.makeText(getApplicationContext(),"Iniciando pagamento crédito",
                                    //        Toast.LENGTH_LONG).show();
                                }
                                if (pagamento.getFormaPagto().equals("debito")) {
                                    mCanClick = true;
                                    PagamentoDebito(total);
                                    //Toast.makeText(getApplicationContext(),"Iniciando pagamento débito",
                                    //        Toast.LENGTH_LONG).show();
                                }
                                if (pagamento.getFormaPagto().equals("pix")) {

                                    mCanClick = true;
                                    PagamentoPix(total);
                                    //Toast.makeText(getApplicationContext(),"Iniciando pagamento débito",
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Arquivo de pagameto/impressão não encontrado",
                                        Toast.LENGTH_SHORT).show();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    finishAffinity();
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            error = e.getMessage();
            //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //showMessageError(error);
    }

    // vai para o presenter
    public void PrintFile() {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().printFile();
    }

    public void PagamentoCredito(int vl) {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().creditPayment(vl);
    }

    public void NFCRead() {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().readNFCCard();
    }

    public void NFCWrite(NFC nfcWrite) {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().writeNFCCard(nfcWrite);
    }

    public void PagamentoDebito(int vl) {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().doDebitPayment(vl);
    }

    public void PagamentoPix(int vl) {
        if (!mCanClick) {
            return;
        }
        mCanClick = false;
        shouldShowDialog = true;
        getPresenter().doPixPayment(vl);
    }

    public void onRefund() {
        if (!mCanClick) {
            return;
        }

        shouldShowDialog = true;
        mCanClick = false;
        getPresenter().getLastTransaction();
    }

    @Override
    public void showTransactionSuccess() {
        if (isEstorno) {
            mCanClick = true;
            showMessage(getString(R.string.transactions_successful));
            try {
                File fl = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    fl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Estorno.json");
                }
                fl.delete();

                File root = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                }
                File f = new File(root, "RespostaEstorno.json");
                f.createNewFile();

                //System.exit(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mCanClick = true;
            showMessage(getString(R.string.transactions_successful));
            try {
                File fl = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    fl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CallPagar.json");
                }
                fl.delete();
                //Toast.makeText(getApplicationContext(),"CallPagar Deletado", Toast.LENGTH_SHORT).show();

                File root = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                }
                File f = new File(root, "RespostaTicket.json");
                f.createNewFile();

                //System.exit(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void writeToFile(String transactionCode, String transactionId) {
        FileHelper.writeToFile(transactionCode, transactionId, this);
    }

    @Override
    public void disposeDialog() {
        mCanClick = true;
        shouldShowDialog = false;
    }

    @Override
    public void showSucess() {
        Snackbar.make(findViewById(android.R.id.content), R.string.printer_print_success, Snackbar.LENGTH_LONG).show();
    }
    @Override
    public void showSuccessWrite(PlugPagNFCResult result) {
        String id = new String(result.getSlots()[30].get("data"), StandardCharsets.UTF_8);
        String num = new String(result.getSlots()[32].get("data"), StandardCharsets.UTF_8);
        System.out.println(num);
        System.out.println(id);
        System.exit(0);
    }
    @Override
    public void showSuccessRead(PlugPagNFCResult result) {
        try {
            String id = new String(result.getSlots()[30].get("data"), StandardCharsets.UTF_8);
            String num = new String(result.getSlots()[32].get("data"), StandardCharsets.UTF_8);
            num = num.replaceAll("_","");
            id = id.replaceAll("_","");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("num",num);
            jsonObject.put("id",id);

            String caminho = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/NFC.json";
            //File nfcRead = new File(caminho);
            FileWriter fileWriter = new FileWriter(caminho);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();
            System.exit(0);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAuthProgress(String message) {
        UIFeedback.showDialog(this, message);
    }

    @Override
    public void showMessage(String message) {
        if (shouldShowDialog && !dialog.isShowing()) {
            dialog.show();
        }
        dialog.setMessage(message);

        if (message.contains("CANCELADA")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
            //System.exit(0);
        }
    }

    @Override
    public void showError(String message) {
        try {
            if(message != null){
                Toast.makeText(this, message , Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(this, "Ocorreu um erro" , Toast.LENGTH_LONG).show();
            }

            String erroGenerico = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroGenerico.json";
            File erro = new File(erroGenerico);
            erro.createNewFile();
            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void showAbortedSuccessfully() {
        UIFeedback.showDialog(this, R.string.transactions_successful_abort, true);
    }

    @Override
    public void showLoading(boolean show) {
        if (show) {
            UIFeedback.showProgress(this);
        } else {
            UIFeedback.dismissProgress();
        }
    }

    @Override
    public void showActivationDialog() {
        ActivationDialog dialog = new ActivationDialog();
        dialog.setOnDismissListener(new DismissListener() {
            @Override
            public void onDismiss(String activationCode) {
                getPresenter().activate(activationCode);
            }
        });
        dialog.show(getSupportFragmentManager(), "dialog");
    }

    DialogInterface.OnCancelListener cancelListener = dialogInterface -> {
        dialogInterface.dismiss();
        if (shouldShowDialog) {
            getPresenter().abortTransaction();
        }
    };

    @Override
    public void onDestroy() {
        UIFeedback.releaseVariables();
        super.onDestroy();
    }
}
