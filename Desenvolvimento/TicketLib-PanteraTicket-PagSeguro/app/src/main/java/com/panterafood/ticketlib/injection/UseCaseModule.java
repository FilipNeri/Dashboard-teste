package com.panterafood.ticketlib.injection;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import com.panterafood.ticketlib.demo.DemoInternoUseCase;
import com.panterafood.ticketlib.printer.PrinterUseCase;
import dagger.Module;
import dagger.Provides;
import com.panterafood.ticketlib.auth.AuthUseCase;
import com.panterafood.ticketlib.transactions.TransactionsUseCase;
import com.panterafood.ticketlib.nfc.NFCUseCase;

@Module
public class UseCaseModule {

    @Provides
    AuthUseCase providesAuthUseCase(PlugPag plugPag) {
        return new AuthUseCase(plugPag);
    }

    @Provides
    TransactionsUseCase providesTransactionsUseCase(PlugPag plugPag) {
        return new TransactionsUseCase(plugPag);
    }

    @Provides
    NFCUseCase providesNFCUseCase(PlugPag plugPag) {
        return new NFCUseCase(plugPag);
    }

    @Provides
    PrinterUseCase providesPrinterUseCase(PlugPag plugPag) {
        return new PrinterUseCase(plugPag);
    }

    @Provides
    DemoInternoUseCase providesDemoInternoUseCase(PlugPag plugPag) {
        return new DemoInternoUseCase(plugPag);
    }
}
