package com.example.alarm.materialhw.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alarm.materialhw.R;

public class LoginFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_login;
    View view;


    public static LoginFragment instance = null;
    private Context context;

    public static LoginFragment getInstance(Context context){
        if (instance == null){
            instance = new LoginFragment();
            instance.setContext(context);
        }

        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        return view;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
