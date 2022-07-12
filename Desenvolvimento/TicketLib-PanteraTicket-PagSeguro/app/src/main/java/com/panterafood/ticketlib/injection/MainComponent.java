package com.panterafood.ticketlib.injection;

import br.com.uol.pagseguro.plugpagservice.wrapper.PlugPag;
import dagger.Component;
import com.panterafood.ticketlib.MainActivity;
import com.panterafood.ticketlib.utils.FragmentFlowManager;

@Component(modules = {WrapperModule.class, ScreenFlowModule.class})
public interface MainComponent {

    void inject(MainActivity activity);

    PlugPag plugPag();

    FragmentFlowManager flowManager();
}
