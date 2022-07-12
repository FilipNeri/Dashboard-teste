package com.panterafood.ticketlib.injection;

import com.panterafood.ticketlib.demo.DemoInternoActivity;
import com.panterafood.ticketlib.demo.DemoInternoPresenter;
import dagger.Component;

@Component(modules = {UseCaseModule.class, WrapperModule.class})
public interface DemoInternoComponent {

    void inject(DemoInternoActivity activity);

    DemoInternoPresenter presenter();
}
