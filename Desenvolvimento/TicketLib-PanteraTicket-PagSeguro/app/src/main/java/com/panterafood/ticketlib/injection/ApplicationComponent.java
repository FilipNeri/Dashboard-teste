package com.panterafood.ticketlib.injection;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import dagger.Component;

@Component(modules = {WrapperModule.class})
public interface ApplicationComponent {

    PlugPag plugPag();
}
