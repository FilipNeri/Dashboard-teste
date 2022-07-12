package com.panterafood.ticketlib.injection;

import com.panterafood.ticketlib.ApplicationSelectorActivity;
import dagger.Component;

@Component(dependencies = {MainComponent.class}, modules = {UseCaseModule.class})
public interface ApplicationSelectorComponent {

    void inject(ApplicationSelectorActivity fragment);

}
