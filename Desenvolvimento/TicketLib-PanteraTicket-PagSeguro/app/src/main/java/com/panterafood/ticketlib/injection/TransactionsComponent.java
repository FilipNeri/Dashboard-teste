package com.panterafood.ticketlib.injection;

import dagger.Component;
import com.panterafood.ticketlib.transactions.TransactionsFragment;
import com.panterafood.ticketlib.transactions.TransactionsPresenter;

@Component(dependencies = {MainComponent.class}, modules = {UseCaseModule.class})
public interface TransactionsComponent extends MainComponent {

    TransactionsPresenter presenter();

    void inject(TransactionsFragment fragment);
}
