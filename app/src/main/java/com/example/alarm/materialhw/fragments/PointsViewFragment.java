package com.example.alarm.materialhw.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alarm.materialhw.Adapters.PointsAdapter;
import com.example.alarm.materialhw.R;
import com.example.alarm.materialhw.Subject;

public class PointsViewFragment extends Fragment {

    private static final int LAYOUT = R.layout.fragment_points;
    View view;
    Context context;
    RecyclerView recyclerView;
    Subject subject;

    private static PointsViewFragment instance = null;

    public static PointsViewFragment getInstance(Context context, Subject item){
        if (instance == null) {
            instance = new PointsViewFragment();
            instance.context = context;
            instance.subject = item;
        }
        if (!instance.subject.equals(item)){
            instance.subject = item;
        }
        return instance;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new PointsAdapter(subject));
        return view;
    }

    public PointsViewFragment() {
        super();
    }
}
