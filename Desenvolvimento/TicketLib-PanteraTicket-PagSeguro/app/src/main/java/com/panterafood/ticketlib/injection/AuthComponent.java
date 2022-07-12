package com.panterafood.ticketlib.injection;

import dagger.Component;
import com.panterafood.ticketlib.auth.AuthFragment;
import com.panterafood.ticketlib.auth.AuthPresenter;

@Component(dependencies = {MainComponent.class}, modules = {UseCaseModule.class})
public interface AuthComponent extends MainComponent {

    AuthPresenter presenter();

    void inject(AuthFragment fragment);
}
