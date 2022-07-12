package com.panterafood.ticketlib.demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.github.danielfelgar.drawreceiptlib.ReceiptBuilder;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagActivationData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagInitializationResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNFCResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagNearFieldCardData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPaymentData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrintResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrinterData;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagPrinterListener;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagTransactionResult;
import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPagVoidData;
import br.com.uol.pagseguro.plugpagservice.wrapper.exception.PlugPagException;
import com.panterafood.ticketlib.ActionResult;
import com.panterafood.ticketlib.ItemFicha;
import com.panterafood.ticketlib.ItemImpressao;
import com.panterafood.ticketlib.NFC;
import com.panterafood.ticketlib.Produto;
import com.panterafood.ticketlib.R;
import com.panterafood.ticketlib.Relatorio;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class DemoInternoUseCase {

    public static final String USER_REFERENCE = "APPDEMO";
    private final PlugPag mPlugPag;

    private final int TYPE_CREDITO = 1;
    private final int TYPE_DEBITO = 2;
    private final int TYPE_VOUCHER = 3;
    private final int TYPE_PIX = 5;

    private final int INSTALLMENT_TYPE_A_VISTA = 1;
    private final int INSTALLMENT_TYPE_PARC_VENDEDOR = 2;
    private final int INSTALLMENT_TYPE_PARC_COMPRADOR = 3;
    //String barcode_data = "7891000315507";
    private String barcode_data;

    public DemoInternoUseCase(PlugPag plugPag) {
        mPlugPag = plugPag;
    }

    public Observable<ActionResult> printFile() {
        return Observable.create((ObservableEmitter<ActionResult> emitter) -> {
            String relatorio = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                    "/ImpRelatorio.json");
            if(!TextUtils.isEmpty(relatorio)){ //Imprimir relatorio
                ActionResult actionResult = new ActionResult();
                setPrintListener(emitter, actionResult);

                Gson gson = new Gson();
                // informações do relatorio
                Relatorio impRelatorio = gson.fromJson(relatorio, Relatorio.class);

                if(impRelatorio != null){
                    if(impRelatorio.getTipo().equalsIgnoreCase("LISTA DE PRODUTOS")){
                        listaProdutos(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }
                    else if(impRelatorio.getTipo().equalsIgnoreCase("SANGRIA") ||
                       impRelatorio.getTipo().equalsIgnoreCase("SUPRIMENTO"))
                    {
                        sangriaSuprimento(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }
                    else if(impRelatorio.getTipo().equalsIgnoreCase("ESTORNO")){
                        estorno(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }
                    else if (impRelatorio.getTipo().equalsIgnoreCase("ABERTURA DE CAIXA")){
                        aberturaDeCaixa(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }
                    else if (impRelatorio.getTipo().equalsIgnoreCase("SALDO DE CAIXA")) {
                        saldoCaixa(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }

                    else if (impRelatorio.getTipo().equalsIgnoreCase("RELATÓRIO DE PRODUTOS")) {
                        relatorioProdutos(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }                    else if (impRelatorio.getTipo().equalsIgnoreCase("FECHAMENTO DE CAIXA")) {
                        //String imp = fechamentoDeCaixa(impRelatorio);
                        fechamentoDeCaixa(impRelatorio);

                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }
                        else{ //sucesso
                            impressaoFinalizada();
                            emitter.onComplete();
                        }
                    }
                    else {
                        String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                        File errof = new File(erro);
                        errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                        System.exit(0);
                    }
                }
            }
            else { //imprime ticket
                ActionResult actionResult = new ActionResult();
                setPrintListener(emitter, actionResult);

                String imp = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/ImpTickets.json");
                String impDireto = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                        "/ImpTicketsDireto.json");

                if(!TextUtils.isEmpty(impDireto)){
                    String strPrint = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            "/ImpTicketsDireto.json");

                    Gson gson = new Gson();
                    // informações do pagamento
                    // ItemImpressao impressao = gson.fromJson(strPrint, ItemImpressao.class);
                    ItemImpressao[] impressao = gson.fromJson(strPrint, ItemImpressao[].class);

                    if(impressao != null){
                        boolean existeSegundaVia = false;
                        ArrayList<ItemImpressao> segundaViaItems = new ArrayList<>();
                        for (int i = 0; impressao.length > i; i++) {
                            if(impressao[i].isSegundaVia()){
                                existeSegundaVia = true;
                                segundaViaItems.add(impressao[i]);
                            }
                        }
                        montaTicketRapido(impressao);
                        PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                4,
                                10 * 12));

                        if (result.getResult() != PlugPag.RET_OK) { // erro
                            String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                            File errof = new File(erro);
                            errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                            actionResult.setResult(result.getResult());

                            System.exit(0);
                        }

                        if (existeSegundaVia){
                            montaSegundaVia(segundaViaItems);
                            PlugPagPrintResult result2 = mPlugPag.printFromFile(new PlugPagPrinterData(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                    4,
                                    10 * 12));

                            if (result2.getResult() != PlugPag.RET_OK) { // erro
                                String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                                File errof = new File(erro);
                                errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                                actionResult.setResult(result2.getResult());

                                System.exit(0);
                            }
                        }
                        impressaoFinalizada();
                    }
                }
                else if (!TextUtils.isEmpty(imp)) {
                    String strPrint = getStringFromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                            "/ImpTickets.json");

                    Gson gson = new Gson();
                    // informações do pagamento
                    // ItemImpressao impressao = gson.fromJson(strPrint, ItemImpressao.class);
                    ItemImpressao[] impressao = gson.fromJson(strPrint, ItemImpressao[].class);

                    if (impressao != null) {
                        boolean existeSegundaVia = false;
                        ArrayList<ItemImpressao> segundaViaItems = new ArrayList<>();
                        int contagemInicio = DemoInternoActivity.loadContador("contador");
                        //Toast.makeText(DemoInternoActivity.getAppContext(), "contagemInicio 312: " + contagemInicio,
                        //       Toast.LENGTH_SHORT).show();
                        Log.d("CONTADOR", "ContagemInicio: " + contagemInicio);

                        if (impressao.length <= contagemInicio + 1) {
                            DemoInternoActivity.salvaContador(-1, "contador");
                            contagemInicio = DemoInternoActivity.loadContador("contador");
                            Log.d("CONTADOR", "(impressao.length <= contagemInicio+1) | Contagem inicio " + contagemInicio);
                        }
                        if (impressao.length > contagemInicio + 1) {
                            //Percorre todos os itens do pagamento
                            for (int i = contagemInicio + 1; impressao.length > i; i++) {
                                barcode_data = impressao[i].getCd();
                                barcode_data = montaCdBarras(barcode_data,impressao[i].getForma_pgto());

                                if (impressao[i].isSegundaVia()) {
                                    existeSegundaVia = true;
                                    segundaViaItems.add(impressao[i]);
                                }
                                montaTicket(impressao[i], barcode_data);

                                PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                        4,
                                        10 * 12));

                                if (result.getResult() != PlugPag.RET_OK) { // erro
                                    String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                                    File errof = new File(erro);
                                    errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                                    actionResult.setResult(result.getResult());

                                    System.exit(0);
                                } else { //sucesso
                                    DemoInternoActivity.salvaContador(i, "contador");
                                    //Toast.makeText(DemoInternoActivity.getAppContext(), "contados 375: " + i,
                                    //        Toast.LENGTH_SHORT).show();
                                    Log.d("CONTADOR", "Sucesso ticket -> Contador:" + i);
                                }

                            }

                            if (existeSegundaVia) {
                                montaSegundaVia(segundaViaItems);
                                PlugPagPrintResult result = mPlugPag.printFromFile(new PlugPagPrinterData(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png",
                                        4,
                                        10 * 12));

                                if (result.getResult() != PlugPag.RET_OK) { // erro
                                    String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                                    File errof = new File(erro);
                                    errof.createNewFile(); //Cria o arquivo que diz que teve um erro na impressão

                                    actionResult.setResult(result.getResult());

                                    System.exit(0);
                                } else { //sucesso
                                    Log.d("CONTADOR", "Sucesso 2via");
                                }
                            }
                            impressaoFinalizada();
                        }
                    }
                }
                emitter.onComplete();
            }

        });
    }
    private void relatorioProdutos(Relatorio impRelatorio){

        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText("Data inicial:")
                .addText(impRelatorio.getDt_abert())
                .addText("")
                .addText("Data final:")
                .addText(impRelatorio.getDt_fecha())
                .addText("")
                .addText("PRODUTOS VENDIDOS:");

        Locale ptBr = new Locale("pt", "BR");
        for (ItemFicha i : impRelatorio.getItens()) {
            receipt.addText("")
                    .addText(i.getNm_prod())
                    .addText("Qtd: " + i.getQtd() + " | " + NumberFormat.getCurrencyInstance(ptBr).format(i.getSub_total()));
        }

        receipt.addText("")
                .addText("Usuário: " + impRelatorio.getUsu())
                .addText("Cód. usuário: " + impRelatorio.getCd_usu())
                .addText("POS: " + impRelatorio.getSerial())
        ;


        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saldoCaixa(Relatorio impRelatorio){

        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText("Data abertura:")
                .addText(impRelatorio.getDt_abert())
                .addText("Valor: " + impRelatorio.getVl_abert())
                .addText("")
                .addText("Evento:")
                .addText(impRelatorio.getEvento())
                .addText("")
                .addText("COMPUTADO (VENDAS):")
                .addText("Dinheiro: "+ impRelatorio.getVl_computado_din())
                .addText("Cartão: "+ impRelatorio.getVl_computado_car())
                .addText("Cortesia: "+ impRelatorio.getVl_computado_cortesia())
                .addText("Outros: "+ impRelatorio.getVl_computado_outra())
                .addText("")
                .addText("Sangria: " + impRelatorio.getVl_computado_sangria())
                .addText("Suprimento: " + impRelatorio.getVl_computado_suprimento())
                .addText("")
                .addText("Estornos: " + impRelatorio.getVl_computado_estornos())
                .addText("")
                .addText("TOTAL: " + impRelatorio.getVl())
                .addText("")
                .addText("PRODUTOS VENDIDOS:");

        Locale ptBr = new Locale("pt", "BR");
        for (ItemFicha i : impRelatorio.getItens()) {
            receipt.addText("")
                    .addText(i.getNm_prod())
                    .addText("Qtd: " + i.getQtd() + " | " + NumberFormat.getCurrencyInstance(ptBr).format(i.getSub_total()));
        }

        receipt.addText("")
                .addText("Usuário: " + impRelatorio.getUsu())
                .addText("Cód. usuário: " + impRelatorio.getCd_usu())
                .addText("POS: " + impRelatorio.getSerial())
        ;

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void estorno(Relatorio impRelatorio){
        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText(impRelatorio.getDt_abert())
                .addText("")
                .addText("Cód. da venda: " + impRelatorio.getCd_usu())
                .addText("Valor: " + impRelatorio.getVl())
                .addText("POS: " + impRelatorio.getSerial())
        ;

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listaProdutos(Relatorio impRelatorio){
        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        Locale ptBr = new Locale("pt", "BR");

        receipt.setAlign(Paint.Align.LEFT);

        for (Produto i : impRelatorio.getProdutos()) {
            receipt.addText("")
                    .addText(i.getCd() + " - " + i.getNm())
                    .addText(NumberFormat.getCurrencyInstance(ptBr).format(i.getVl()));
        }

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sangriaSuprimento(Relatorio impRelatorio) {
        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText("Valor: " + impRelatorio.getVl())
                .addText("Caixa: " + impRelatorio.getCaixa())
                .addText("Usuário: " + impRelatorio.getUsu())
                .addText("Cód. usuário: " + impRelatorio.getCd_usu())
                .addText("POS: " + impRelatorio.getSerial())
                .addText("")
                .addText(impRelatorio.getDt_abert());

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void aberturaDeCaixa(Relatorio impRelatorio) {

        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }
        else {
            Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                    R.drawable.panteraticket);
            if(foto_evento != null)
                receipt.addImage(foto_evento)
                        .addText("");
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText(impRelatorio.getDt_abert())
                .addText("")
                .addText("Evento: " + impRelatorio.getEvento())
                .addText("Valor: " + impRelatorio.getVl())
                .addText("Usuário: " + impRelatorio.getUsu())
                .addText("Cód. usuário: " + impRelatorio.getCd_usu())
                .addText("POS: " + impRelatorio.getSerial());

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void fechamentoDeCaixa(Relatorio impRelatorio) {
        System.out.println("chegou no fechamento de caixa");
        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20)
                .setAlign(Paint.Align.CENTER)
                .setColor(Color.BLACK)
                .setTextSize(80);

        //Imagem evento
        if(impRelatorio.getUrl_evento().length() != 0){
            if(impRelatorio.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
            else {
                Bitmap foto_evento = getBitmapFromURL(impRelatorio.getUrl_evento());
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }
        }

        receipt.addText(impRelatorio.getTipo())
                .addText("");

        receipt.setAlign(Paint.Align.LEFT)
                .addText("Data abertura:")
                .addText(impRelatorio.getDt_abert())
                .addText("Valor:")
                .addText(impRelatorio.getVl_abert())
                .addText("")
                .addText("Data fechamento:")
                .addText(impRelatorio.getDt_fecha())
                .addText("")
                .addText("Evento:")
                .addText(impRelatorio.getEvento())
                .addText("-----------------------------------------------")
                .addText("PRODUTOS VENDIDOS");

        Locale ptBr = new Locale("pt", "BR");
        double abertura = Double.parseDouble(impRelatorio.getVl_abert().replace("R$ ", "").replace(".", "").replace(",", "."));

        double vendas = 0;
        for (ItemFicha i : impRelatorio.getItens()) {
            vendas += i.getSub_total();
            receipt.addText("")
                    .addText(i.getNm_prod())
                    .addText("Qtd: " + i.getQtd() + " | " + NumberFormat.getCurrencyInstance(ptBr).format(i.getSub_total()))
            ;
        }

        double doubleSangria= Double.parseDouble(impRelatorio.getVl_computado_sangria().replace("R$ ", "").replace(".", "").replace(",", "."));
        double doubleSuprimento= Double.parseDouble(impRelatorio.getVl_computado_suprimento().replace("R$ ", "").replace(".", "").replace(",", "."));
        double total = vendas + doubleSangria + doubleSuprimento + abertura;

        double doubleDinComp= Double.parseDouble((impRelatorio.getVl_computado_din()+ doubleSuprimento + abertura).replace("R$ ", "").replace(".", "").replace(",", "."));
        double doubleDinDig= Double.parseDouble(impRelatorio.getVl_din().replace("R$ ", "").replace(".", "").replace(",", "."));
        double diferencaDin = doubleDinDig - doubleDinComp;

        double doubleCarComp= Double.parseDouble(impRelatorio.getVl_computado_car().replace("R$ ", "").replace(".", "").replace(",", "."));
        double doubleCarDig= Double.parseDouble(impRelatorio.getVl_car().replace("R$ ", "").replace(".", "").replace(",", "."));
        double diferencaCar = doubleCarDig - doubleCarComp;

        double doubleCortComp= Double.parseDouble(impRelatorio.getVl_computado_cortesia().replace("R$ ", "").replace(".", "").replace(",", "."));
        double doubleCortDig= Double.parseDouble(impRelatorio.getVl_cor().replace("R$ ", "").replace(".", "").replace(",", "."));
        double diferencaCort = doubleCortDig - doubleCortComp;

        double doublePixComp= Double.parseDouble(impRelatorio.getVl_computado_pix().replace("R$ ", "").replace(".", "").replace(",", "."));
        double doublePixDig= Double.parseDouble(impRelatorio.getVl_pix().replace("R$ ", "").replace(".", "").replace(",", "."));
        double diferencaPix = doublePixDig - doublePixComp;

        receipt.addText("")
                .addText("-----------------------------------------------")
                .addText("Estornos:")
                .addText(impRelatorio.getVl_computado_estornos())
                .addText("-----------------------------------------------")
                .addText("Vendas:")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(vendas))
                .addText("")
                .addText("Sangrias:")
                .addText(impRelatorio.getVl_computado_sangria())
                .addText("")
                .addText("Suprimentos:")
                .addText(impRelatorio.getVl_computado_suprimento())
                .addText("")
                .addText("TOTAL: ")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(total))
                .addText("")
                .addText("-----------------------------------------------")
                .addText("Dinheiro computado:")
                .addText(impRelatorio.getVl_computado_din())
                .addText("")
                .addText("Dinheiro digitado:")
                .addText(impRelatorio.getVl_din())
                .addText("")
                .addText("Diferança Dinheiro:")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(diferencaDin))
                .addText("")
                .addText("-----------------------------------------------")
                .addText("Cartão computado:")
                .addText(impRelatorio.getVl_computado_car())
                .addText("")
                .addText("Cartão digitado:")
                .addText(impRelatorio.getVl_car())
                .addText("")
                .addText("Diferança Cartão:")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(diferencaCar))
                .addText("")
                /*
                .addText("-----------------------------------------------")
                .addText("Cortesia computado:")
                .addText(impRelatorio.getVl_computado_cortesia())
                .addText("")
                .addText("Cortesia digitado:")
                .addText(impRelatorio.getVl_cor())
                .addText("")
                .addText("Diferança Cortesia:")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(diferencaCort))
                .addText("")*/
                .addText("-----------------------------------------------")
                .addText("Pix computado:")
                .addText(impRelatorio.getVl_computado_pix())
                .addText("")
                .addText("Pix digitado:")
                .addText(impRelatorio.getVl_pix())
                .addText("")
                .addText("Diferança Pix:")
                .addText(NumberFormat.getCurrencyInstance(ptBr).format(diferencaPix))
                .addText("")
                .addText("-----------------------------------------------")
        ;

        receipt.addText("")
                .addText("Usuário: " + impRelatorio.getUsu())
                .addText("Cód. usuário:" + impRelatorio.getCd_usu())
        ;

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        String imp = impRelatorio.getTipo();
        imp += "\n\nData abertura:\n" + impRelatorio.getDt_abert();
        imp += "\nValor: " + impRelatorio.getVl_abert();
        imp += "\n\nData fechamento:\n" + impRelatorio.getDt_fecha();
        imp += "\n\nEvento: " + impRelatorio.getEvento();
        imp += "\n\nDIGITADO:";
        imp += "\nDinheiro: " + impRelatorio.getVl_din();
        imp += "\nCartão: " + impRelatorio.getVl_car();
        imp += "\nCortesia: " + impRelatorio.getVl_cor();
        imp += "\n\nCOMPUTADO (VENDAS):";
        imp += "\nDinheiro: " + impRelatorio.getVl_computado_din();
        imp += "\nCartão: " + impRelatorio.getVl_computado_car();
        imp += "\nCortesia: " + impRelatorio.getVl_computado_cortesia();
        imp += "\n\nSangria: " + impRelatorio.getVl_computado_sangria();
        imp += "\nSuprimento: " + impRelatorio.getVl_computado_suprimento();
        imp += "\n\nEstornos: " + impRelatorio.getVl_computado_estornos();
        imp += "\n\nPRODUTOS VENDIDOS:";
        Locale ptBr = new Locale("pt", "BR");
        for (ItemFicha i : impRelatorio.getItens()) {
            imp += "\n\n" + i.getNm_prod();
            imp += "\nQtd: " + i.getQtd() + " | " + NumberFormat.getCurrencyInstance(ptBr).format(i.getSub_total());;
                            //imp += "\n\nCód: " + i.getCd_prod();
                            //imp += "\nQtd: " + i.getQtd();
                            //imp += "\nTotal: " + NumberFormat.getCurrencyInstance(ptBr).format(i.getSub_total());
        }
        imp += "\n\nUsuário: " + impRelatorio.getUsu();
        imp += "\nCód. usuário: " + impRelatorio.getCd_usu();
        return imp;
        */
    }

    private void montaTicket(ItemImpressao i, String barcode_data) {
        // barcode image
        Bitmap barcode = null;
        try {
            barcode = encodeAsBitmap(barcode_data, BarcodeFormat.EAN_13, 900, 350);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (barcode != null){
            ReceiptBuilder receipt = new ReceiptBuilder(1000);

            receipt.setMargin(30, 20).
                    setAlign(Paint.Align.CENTER).
                    setColor(Color.BLACK).
                    setTextSize(85);

            //Imagem evento
            if(i.getUrl_evento().length() != 0){
                if(i.getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                    //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                    Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                            R.drawable.panteraticket);
                    if(foto_evento != null)
                        receipt.addImage(foto_evento)
                                .addText("");
                }
                else {
                    Bitmap foto_evento = getBitmapFromURL(i.getUrl_evento());
                    if(foto_evento != null)
                        receipt.addImage(foto_evento)
                                .addText("");
                }
            }
            else {
                Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                        R.drawable.panteraticket);
                if(foto_evento != null)
                    receipt.addImage(foto_evento)
                            .addText("");
            }

            //Nome do evento
            if(i.getEvento().length() > 15) {
                receipt.setAlign(Paint.Align.CENTER).
                        addText(i.getEvento().substring(0, 15)).
                        addText(i.getEvento().substring(15));
            }
            else{
                receipt.setAlign(Paint.Align.CENTER).
                        addText(i.getEvento());
            }

            receipt.addText("");

            //receipt.setAlign(Paint.Align.CENTER).
            //        addText(i.getEvento())
            //        .addText("");

            //Nome do produto
            if(i.getNm_prod().length() > 14) {
                String primeira = i.getNm_prod().substring(0, 14);
                String segunda = i.getNm_prod().substring(14);

                receipt.addText(primeira).
                        addText(segunda);
            }
            else{
                receipt.addText(i.getNm_prod());
            }

            receipt.addBlankSpace(20).
                    addText("R$"+i.getSubtot());

            //se for cortesia
            if(i.getForma_pgto() != "") {
                receipt.addText(i.getForma_pgto());
            }

            if(!i.isSegundaVia()){
                receipt.addText("");
                receipt.addImage(barcode)
                        .addBlankSpace(50);
            }

            receipt.setTextSize(65).
                    setAlign(Paint.Align.CENTER);

            if(i.getMsg1() != ""){
                receipt.addText(i.getMsg1());
            }
            if(i.getMsg2() != ""){
                receipt.addText(i.getMsg2());
            }

            if(i.isSegundaVia()) {
                receipt.setAlign(Paint.Align.CENTER).
                        addText("SENHA:" + i.getSenha());
            }
            receipt.addText(barcode_data);
            receipt.addText("");
            receipt.setAlign(Paint.Align.LEFT).
                    addText("Venda:" + i.getCd_ficha()).
                    addText(i.getDt());

            receipt.setAlign(Paint.Align.LEFT).
                    addText("POS:" + i.getSerial());

            String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

            Bitmap cs = receipt.build();

            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
                FileOutputStream fos = new FileOutputStream(new File(path));
                cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
                cs.recycle();
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void montaTicketRapido(ItemImpressao[] impressao) {
        ReceiptBuilder receipt = new ReceiptBuilder(1000);
        for (int i = 0; impressao.length > i; i++) {
            barcode_data = impressao[i].getCd();
            barcode_data = montaCdBarras(barcode_data,impressao[i].getForma_pgto());

            // barcode image
            Bitmap barcode = null;
            try {
                barcode = encodeAsBitmap(barcode_data, BarcodeFormat.EAN_13, 900, 350);
            } catch (WriterException e) {
                e.printStackTrace();
            }

            if (barcode != null){
                receipt.setMargin(30, 20).
                        setAlign(Paint.Align.CENTER).
                        setColor(Color.BLACK).
                        setTextSize(85);


                //Imagem evento
                if(impressao[i].getUrl_evento().length() != 0){
                    if(impressao[i].getUrl_evento().equalsIgnoreCase("http://apppanterafood.kinghost.net/PanteraTicket.png")){
                        //ContextCompat.getDrawable(DemoInternoActivity.getAppContext(), R.drawable.meuticket);
                        Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                                R.drawable.panteraticket);
                        if(foto_evento != null)
                            receipt.addImage(foto_evento)
                                    .addText("");
                    }
                    else {
                        Bitmap foto_evento = getBitmapFromURL(impressao[i].getUrl_evento());
                        if(foto_evento != null)
                            receipt.addImage(foto_evento)
                                    .addText("");
                    }
                }
                else {
                    Bitmap foto_evento = BitmapFactory.decodeResource(DemoInternoActivity.getAppContext().getResources(),
                            R.drawable.panteraticket);
                    if(foto_evento != null)
                        receipt.addImage(foto_evento)
                                .addText("");
                }

                //Nome do evento
                if(impressao[i].getEvento().length() > 15) {
                    receipt.setAlign(Paint.Align.CENTER).
                            addText(impressao[i].getEvento().substring(0, 15)).
                            addText(impressao[i].getEvento().substring(15));
                }
                else{
                    receipt.setAlign(Paint.Align.CENTER).
                            addText(impressao[i].getEvento());
                }

                receipt.addText("");

                //receipt.setAlign(Paint.Align.CENTER).
                //        addText(i.getEvento())
                //        .addText("");

                //Nome do produto
                if(impressao[i].getNm_prod().length() > 14) {
                    String primeira = impressao[i].getNm_prod().substring(0, 14);
                    String segunda = impressao[i].getNm_prod().substring(14);

                    receipt.addText(primeira).
                            addText(segunda);
                }
                else{
                    receipt.addText(impressao[i].getNm_prod());
                }

                receipt.addBlankSpace(20).
                        addText("R$"+impressao[i].getSubtot());

                //se for cortesia
                if(impressao[i].getForma_pgto() != "") {
                    receipt.addText(impressao[i].getForma_pgto());
                }

                if(!impressao[i].isSegundaVia()){
                    receipt.addText("");
                    receipt.addImage(barcode)
                            .addBlankSpace(50);
                }

                receipt.setTextSize(65).
                        setAlign(Paint.Align.CENTER);

                if(impressao[i].getMsg1() != ""){
                    receipt.addText(impressao[i].getMsg1());
                }
                if(impressao[i].getMsg2() != ""){
                    receipt.addText(impressao[i].getMsg2());
                }

                if(impressao[i].isSegundaVia()) {
                    receipt.setAlign(Paint.Align.CENTER).
                            addText("SENHA:" + impressao[i].getSenha());
                }
                receipt.addText(barcode_data);
                receipt.addText("");
                receipt.setAlign(Paint.Align.LEFT).
                        addText("Venda:" + impressao[i].getCd_ficha()).
                        addText(impressao[i].getDt()).
                        addText("POS: " + impressao[i].getSerial())
                ;

                receipt.addText("");
                receipt.addText("");
                receipt.addText("");
            }
        }

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void montaSegundaVia(ArrayList<ItemImpressao> segundaViaItems){
        ReceiptBuilder receipt = new ReceiptBuilder(1000);

        receipt.setMargin(30, 20).
                setAlign(Paint.Align.CENTER).
                setColor(Color.BLACK).
                setTextSize(80);

        receipt.setAlign(Paint.Align.CENTER)
                .addText("CÓPIA PARA PRODUÇÃO");

        double total = 0;
        for (ItemImpressao i: segundaViaItems) {
            if(i.getNm_prod().length() > 14) {
                String primeira = i.getNm_prod().substring(0, 14);
                String segunda = i.getNm_prod().substring(14);

                receipt.addText("- " +primeira).
                        addText(segunda);
            }
            else{
                receipt.addText("- " +i.getNm_prod());
            }

            total += Double.parseDouble(i.getSubtot().replace(".", "").replace(",", "."));
        }

        Locale ptBr = new Locale("pt", "BR");

        receipt.addBlankSpace(20).
                setTextSize(60).
                addText(NumberFormat.getCurrencyInstance(ptBr).format(total)).
                addText("SENHA:" + segundaViaItems.get(0).getSenha()).
                addText("Venda:" + segundaViaItems.get(0).getCd_ficha()).
                addText(segundaViaItems.get(0).getDt());

        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        Bitmap cs = receipt.build();

        try {
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
            FileOutputStream fos = new FileOutputStream(new File(path));
            cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
            cs.recycle();
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void montaTicketSegundaVia(ItemImpressao i, String barcode_data) {
            ReceiptBuilder receipt = new ReceiptBuilder(1000);

            receipt.setMargin(30, 20).
                    setAlign(Paint.Align.CENTER).
                    setColor(Color.BLACK).
                    setTextSize(80);

            //Nome do evento
            receipt.setAlign(Paint.Align.CENTER)
                    .addText("CÓPIA PARA PRODUÇÃO");

            //Nome do produto
            if(i.getNm_prod().length() > 14) {
                String primeira = i.getNm_prod().substring(0, 14);
                String segunda = i.getNm_prod().substring(14);

                receipt.addText(primeira).
                        addText(segunda);
            }
            else{
                receipt.addText(i.getNm_prod());
            }
            receipt.addText(barcode_data);
            receipt.addText("");
            receipt.addBlankSpace(20).
                    setTextSize(60).
                    addText("R$"+i.getSubtot()).
                    addText("SENHA:" + i.getSenha()).
                    addText("Venda:" + i.getCd_ficha()).
                    addText(i.getDt());

            String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

            Bitmap cs = receipt.build();

            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
                FileOutputStream fos = new FileOutputStream(new File(path));
                cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
                cs.recycle();
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    private String montaCdBarras(String barcode, String cashless){
        String retorno;
        // 5 00000000123 ?
        if(cashless.equalsIgnoreCase("cashless")){
            retorno = "6"; // primeiro digito: 5
        }else {
            retorno = "5"; // primeiro digito: 5
        }

        for(int i = 0; i < 11-barcode.length(); i++){ // 11 digitos: 000... + cd
            retorno = retorno + "0";
        }

        retorno = retorno + barcode;
        retorno = retorno + checkSum(retorno); //checkSum: digito verificador

        return retorno;
    }

    public int checkSum(String code){
        int val=0;
        for(int i=0;i<code.length();i++){
            val+=((int)Integer.parseInt(code.charAt(i)+"")) * ((i%2==0) ? 1:3);
        }

        int checksum_digit = 10 - (val % 10);
        if (checksum_digit == 10) checksum_digit = 0;

        return checksum_digit;
    }

    private void impressaoFinalizada() {
        //Deleta o arquivo ImpTicket

        DemoInternoActivity.salvaContador(-1, "contador");
        //Toast.makeText(DemoInternoActivity.getAppContext(), "finaliza 609",
        //        Toast.LENGTH_SHORT).show();
        int contador = DemoInternoActivity.loadContador("contador");

        Log.d("CONTADOR", "impressaoFinalizada: " + contador);
        String caminhoDireto = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImpTicketsDireto.json";
        String caminho = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImpTickets.json";
        String caminho2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
        String relatorio = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImpRelatorio.json";

        try {
            String caminho3 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Finalizado.txt";
            File finalizado = new File(caminho3);
            finalizado.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File deletarDireto = new File(caminhoDireto);
        File deletar = new File(caminho);
        File deletar2 = new File(caminho2);
        File deletarRelatorio = new File(relatorio);

        if (deletarDireto.exists()) {
            deletarDireto.delete(); //COMENTAR SÓ PARA TESTE
            //Toast.makeText(DemoInternoActivity.getAppContext(),"ImpTicket deletado",
            //        Toast.LENGTH_LONG).show();
        }
        if (deletar.exists()) {
           deletar.delete(); //COMENTAR SÓ PARA TESTE
           //Toast.makeText(DemoInternoActivity.getAppContext(),"ImpTicket deletado",
           //        Toast.LENGTH_LONG).show();
        }
        if (deletar2.exists()) {
            deletar2.delete();
            //Toast.makeText(DemoInternoActivity.getAppContext(),"impressao.png deletado",
            //        Toast.LENGTH_LONG).show();
        }
        if (deletarRelatorio.exists()) {
            deletarRelatorio.delete();
            //Toast.makeText(DemoInternoActivity.getAppContext(),"impressao.png deletado",
            //        Toast.LENGTH_LONG).show();
        }
        String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
        File errof = new File(erro);
        if (errof.exists())
           errof.delete(); // deleta o arquivo de erro, se existir

        System.exit(0);
    }
/*
    void getImage(String strPrint) throws IOException {
        String text = strPrint + "\n";

        final Rect bounds = new Rect();
        TextPaint textPaint = new TextPaint() {
            {
                setColor(Color.BLACK);
                setTextAlign(Paint.Align.LEFT);
                setTextSize(32f);
                setAntiAlias(true);
            }
        };
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                bounds.width(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int maxWidth = -1;
        for (int i = 0; i < mTextLayout.getLineCount(); i++) {
            if (maxWidth < mTextLayout.getLineWidth(i)) {
                maxWidth = (int) mTextLayout.getLineWidth(i);
            }
        }
        final Bitmap bmp = Bitmap.createBitmap(maxWidth , mTextLayout.getHeight(),
                Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.WHITE);// just adding black background
        final Canvas canvas = new Canvas(bmp);
        mTextLayout.draw(canvas);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
        FileOutputStream fos = new FileOutputStream(new File(path));
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        //bmp.recycle();
        //fos.flush();
        //fos.close();

        // barcode image
        Bitmap barcode = null;
        try {
            barcode = encodeAsBitmap(barcode_data, BarcodeFormat.EAN_13, 300, 150);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        if (barcode != null){
            combineImages(bmp, barcode);
        }

    }

    public Bitmap combineImages(Bitmap c, Bitmap s) throws IOException { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        cs.eraseColor(Color.WHITE);// just adding black background

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        // this is an extra bit I added, just incase you want to save the new image somewhere and then return the location
        String tmpImg = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";
        FileOutputStream fos = new FileOutputStream(new File(path));
        cs.compress(Bitmap.CompressFormat.PNG, 100, fos);
        cs.recycle();
        fos.flush();
        fos.close();

        return cs;
    }
*/

    String getStringFromFile (String filePath)  {
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

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void setPrintListener(ObservableEmitter<ActionResult> emitter, ActionResult result) {
        mPlugPag.setPrinterListener(new PlugPagPrinterListener() {
            @Override
            public void onError(PlugPagPrintResult printResult) {
                emitter.onError(new PlugPagException(String.format("Error %s %s", printResult.getErrorCode(), printResult.getMessage())));
            }

            @Override
            public void onSuccess(PlugPagPrintResult printResult) {
                //emitter.onError(new PlugPagException(String.format(Locale.getDefault(), "Print OK: steps [%d]", printResult.getSteps())));

                //actionResult.setResult(result.getResult());

                //Deleta o arquivo ImpTicket
                /*
                String caminho = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImpTickets.json";
                String caminho2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/impressao.png";

                File deletar = new File(caminho);
                File deletar2 = new File(caminho2);

                if (deletar.exists()) {
                    deletar.delete(); //COMENTAR SÓ PARA TESTE
                    //Toast.makeText(DemoInternoActivity.getAppContext(),"ImpTicket deletado",
                    //        Toast.LENGTH_LONG).show();
                }
                if (deletar2.exists()) {
                    deletar2.delete();
                    //Toast.makeText(DemoInternoActivity.getAppContext(),"impressao.png deletado",
                    //        Toast.LENGTH_LONG).show();
                }

                String erro = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ErroImpressao.json";
                File errof = new File(erro);
                if (errof.exists())
                    errof.delete(); // deleta o arquivo de erro, se existir

                emitter.onComplete();

                System.exit(0);
                */
            }
        });
    }

    public Observable<ActionResult> doCreditPayment(int value) {
        return doPayment(new PlugPagPaymentData(
                TYPE_CREDITO,
                value,
                INSTALLMENT_TYPE_A_VISTA,
                1,
                USER_REFERENCE,
                true
        ));
    }

    public Observable<ActionResult> doDebitPayment(int value) {
        return doPayment(new PlugPagPaymentData(
                TYPE_DEBITO,
                value,
                INSTALLMENT_TYPE_A_VISTA,
                1,
                USER_REFERENCE,
                true));
    }
    public Observable<ActionResult> doPixPayment(int value) {
        return doPayment(new PlugPagPaymentData(
                TYPE_PIX,
                value,
                INSTALLMENT_TYPE_A_VISTA,
                1,
                USER_REFERENCE,
                true));
    }

    public Observable<ActionResult> doVoucherPayment(int value) {
        return doPayment(new PlugPagPaymentData(
                TYPE_VOUCHER,
                value,
                INSTALLMENT_TYPE_A_VISTA,
                1,
                USER_REFERENCE,
                true));
    }

    private Observable<ActionResult> doPayment(final PlugPagPaymentData paymentData) {
        return Observable.create(emitter -> {
            ActionResult result = new ActionResult();
            setListener(emitter, result);
            PlugPagTransactionResult plugPagTransactionResult = mPlugPag.doPayment(paymentData);
            System.out.println(plugPagTransactionResult);
            sendResponse(emitter, plugPagTransactionResult, result);
        });
    }

    private void sendResponse(ObservableEmitter<ActionResult> emitter, PlugPagTransactionResult plugPagTransactionResult,
                              ActionResult result) {
        if (plugPagTransactionResult.getResult() != 0) {
            emitter.onError(new PlugPagException(plugPagTransactionResult.getMessage(), plugPagTransactionResult.getErrorCode()));
        } else {
            result.setTransactionCode(plugPagTransactionResult.getTransactionCode());
            result.setTransactionId(plugPagTransactionResult.getTransactionId());
            emitter.onNext(result);
        }
        emitter.onComplete();
    }

    private void setListener(ObservableEmitter<ActionResult> emitter, ActionResult result) {
        mPlugPag.setEventListener(plugPagEventData -> {
            result.setEventCode(plugPagEventData.getEventCode());
            result.setMessage(plugPagEventData.getCustomMessage());
            emitter.onNext(result);
        });
    }

    public Completable abort() {
        return Completable.create(emitter -> mPlugPag.abort());
    }

    public Observable<Boolean> isAuthenticated() {
        return Observable.create(emitter -> {
            emitter.onNext(mPlugPag.isAuthenticated());
            emitter.onComplete();
        });
    }

    public Observable<ActionResult> initializeAndActivatePinpad(String activationCode) {
        return Observable.create(emitter -> {
            ActionResult actionResult = new ActionResult();
            mPlugPag.setEventListener(plugPagEventData -> {
                actionResult.setEventCode(plugPagEventData.getEventCode());
                actionResult.setMessage(plugPagEventData.getCustomMessage());
                emitter.onNext(actionResult);
            });

            PlugPagInitializationResult result = mPlugPag.initializeAndActivatePinpad(new PlugPagActivationData(activationCode));
            if (result.getResult() == PlugPag.RET_OK) {
                emitter.onNext(new ActionResult());
            } else {
                emitter.onError(new RuntimeException(result.getErrorMessage()));
            }
            emitter.onComplete();
        });
    }

    public Observable<ActionResult> doRefund(ActionResult transaction) {
        return Observable.create(emitter -> {
            ActionResult actionResult = new ActionResult();
            setListener(emitter, actionResult);
            PlugPagTransactionResult result = mPlugPag.voidPayment(new PlugPagVoidData(transaction.getTransactionCode(),
                    transaction.getTransactionId(),
                    true));
            result.getNsu();
            sendResponse(emitter, result, actionResult);
        });
    }

    public Observable<ActionResult> getLastTransaction() {
        return Observable.create(emitter -> {
            ActionResult actionResult = new ActionResult();
            PlugPagTransactionResult result = mPlugPag.getLastApprovedTransaction();
            sendResponse(emitter, result, actionResult);
        });
    }

    public Observable<PlugPagNFCResult> readNFCCard() {
        return Observable.create(emitter -> {
            PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
            cardData.setStartSlot(30);
            cardData.setEndSlot(32);
            Log.d("READ","LENDO");

            PlugPagNFCResult result = mPlugPag.readFromNFCCard(cardData);

            if (result.getResult() == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }

    public Observable<PlugPagNFCResult> writeNFCCard(NFC data) {
        return Observable.create(emitter -> {
            PlugPagNearFieldCardData cardData = new PlugPagNearFieldCardData();
            cardData.setStartSlot(30);
            cardData.setEndSlot(32);

            String id = data.getId();
            byte[] idBytes = id.getBytes();
            String num = data.getNum();
            byte[] numBytes = num.getBytes();

            cardData.getSlots()[30].put("data", idBytes);
            cardData.getTimeOutRead();
            cardData.getSlots()[32].put("data", numBytes);

            PlugPagNFCResult result = mPlugPag.writeToNFCCard(cardData);

            if (result.getResult() == 1) {
                emitter.onNext(result);
            } else {
                emitter.onError(new PlugPagException());
            }

            emitter.onComplete();
        });
    }
}
