package com.example.alarm.materialhw.fragments;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alarm.materialhw.Adapters.MockAdapter;
import com.example.alarm.materialhw.R;
import com.example.alarm.materialhw.Subject;
import com.example.alarm.materialhw.Adapters.SubjectAdapter;

public class MainFragment extends Fragment {
    private static final int LAYOUT = R.layout.fragment_main;

    View view;
    private Context context;

    public static MainFragment instance = null;

    public static MainFragment getInstance(Context context){
        if (instance == null){
            instance = new MainFragment();
            instance.setContext(context);

        }

        return instance;
    }
    RecyclerView recyclerView;
    SubjectAdapter subjectAdapter;

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView.getAdapter() == null)
        {
            recyclerView.setAdapter(subjectAdapter);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
//        recyclerView.setAdapter(new SubjectAdapter(Subject.subjects, context));
        if ((Subject.subjects == null)||(Subject.subjects.size() == 0))
        {
            SharedPreferences preferences = context.getSharedPreferences("points", Context.MODE_PRIVATE);
            int amount = preferences.getInt("amount", 0);
            if (amount != 0) {
                String[] names = new String[amount];
                boolean[] exam = new boolean[amount];
                float[] points = new float[amount];
                for (int i = 0; i < amount; i++)
                {
                    names[i] = preferences.getString("name"+i, "Subject name is lost");
                    exam[i] = preferences.getBoolean("exam"+i, false);
                    points[i] = preferences.getFloat("points"+i, -1);
                }


                recyclerView.setAdapter(new MockAdapter(names, exam, points, context));
            }
        }
        return view;
    }

    public void updateCards(){
        subjectAdapter = new SubjectAdapter(Subject.subjects, context);
        recyclerView.setAdapter(subjectAdapter);
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
