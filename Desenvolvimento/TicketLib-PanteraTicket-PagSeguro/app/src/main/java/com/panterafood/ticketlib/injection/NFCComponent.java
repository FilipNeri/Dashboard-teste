package com.panterafood.ticketlib.injection;

import dagger.Component;
import com.panterafood.ticketlib.nfc.NFCFragment;
import com.panterafood.ticketlib.nfc.NFCPresenter;

@Component(dependencies = {MainComponent.class}, modules = {UseCaseModule.class})
public interface NFCComponent {

    void inject(NFCFragment fragment);

    NFCPresenter presenter();
}
