package com.panterafood.ticketlib;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import javax.inject.Inject;

import com.panterafood.ticketlib.injection.DaggerMainComponent;
import com.panterafood.ticketlib.printer.PrinterFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.panterafood.ticketlib.auth.AuthFragment;
import com.panterafood.ticketlib.injection.MainComponent;
import com.panterafood.ticketlib.injection.ScreenFlowModule;
import com.panterafood.ticketlib.injection.WrapperModule;
import com.panterafood.ticketlib.permissions.PermissionsFragment;
import com.panterafood.ticketlib.transactions.TransactionsFragment;
import com.panterafood.ticketlib.utils.FragmentFlowManager;
import com.panterafood.ticketlib.nfc.NFCFragment;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    FragmentFlowManager mFlowManager;

    MainComponent mInjector;

    BottomNavigationView.OnNavigationItemSelectedListener bottonMenuListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            int menuId = item.getItemId();
            Fragment fragment = PermissionsFragment.getInstance();

            switch (menuId) {
                case R.id.menu_permissions:
                    fragment = PermissionsFragment.getInstance();
                    break;
                case R.id.menu_auth:
                    fragment = AuthFragment.getInstance();
                    break;
                case R.id.menu_transactions:
                    fragment = TransactionsFragment.getInstance();
                    break;
                case R.id.menu_nfc:
                    fragment = NFCFragment.getInstance();
                    break;
                case R.id.menu_printer:
                    fragment = PrinterFragment.getInstance();
                    break;
            }

            mFlowManager.showFragment(fragment, MainActivity.this);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mInjector = DaggerMainComponent.builder()
                .screenFlowModule(new ScreenFlowModule())
                .wrapperModule(new WrapperModule(getApplicationContext()))
                .build();
        mInjector.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_content) instanceof HomeFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void initView() {
        mFlowManager.showFragment(PermissionsFragment.getInstance(), this);
        mBottomNavigationView.setOnNavigationItemSelectedListener(bottonMenuListener);
    }

    public MainComponent getMainComponent() {
        return mInjector;
    }
}
