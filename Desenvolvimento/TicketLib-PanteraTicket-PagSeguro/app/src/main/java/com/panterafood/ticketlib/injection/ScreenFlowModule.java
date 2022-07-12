package com.panterafood.ticketlib.injection;

import dagger.Module;
import dagger.Provides;
import com.panterafood.ticketlib.utils.FragmentFlowManager;

@Module
public class ScreenFlowModule {

    @Provides
    FragmentFlowManager providesFragmentFlowManager() {
        return new FragmentFlowManager();
    }

}
