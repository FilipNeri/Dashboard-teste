package com.panterafood.ticketlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import com.panterafood.ticketlib.demo.DemoInternoActivity;
import com.panterafood.ticketlib.injection.ApplicationSelectorComponent;
import com.panterafood.ticketlib.utils.FragmentFlowManager;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class  ApplicationSelectorActivity extends Activity {

    ApplicationSelectorComponent mInjector;

    @Inject
    FragmentFlowManager mFragmentFlowManager;

    public static ApplicationSelectorActivity getInstance() {
        return new ApplicationSelectorActivity();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_selector);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.btn_demo)
    public void onDemoClicked() {
        startActivity(DemoInternoActivity.class);
    }

    private void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btn_all_features)
    public void onAllFeaturesClicked() {
        startActivity(MainActivity.class);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
