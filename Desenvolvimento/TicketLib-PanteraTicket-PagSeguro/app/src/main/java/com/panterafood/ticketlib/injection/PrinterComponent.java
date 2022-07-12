package com.panterafood.ticketlib.injection;

import com.panterafood.ticketlib.printer.PrinterFragment;
import com.panterafood.ticketlib.printer.PrinterPresenter;
import dagger.Component;

@Component(dependencies = {MainComponent.class}, modules = {UseCaseModule.class})
public interface PrinterComponent {

    PrinterPresenter presenter();

    void inject(PrinterFragment fragment);
}
