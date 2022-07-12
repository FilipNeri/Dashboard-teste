package com.panterafood.ticketlib.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.panterafood.ticketlib.MainActivity;
import com.panterafood.ticketlib.R;

public class FragmentFlowManager {

    public void showFragment(Fragment fragment, Context context) {
        FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_content, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
