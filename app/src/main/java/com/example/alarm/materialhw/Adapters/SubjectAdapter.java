package com.example.alarm.materialhw.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alarm.materialhw.MainActivity;
import com.example.alarm.materialhw.R;
import com.example.alarm.materialhw.Subject;
import com.example.alarm.materialhw.fragments.PointsViewFragment;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectHolder> {

    private List<Subject> data = new ArrayList<>();
    private Context context;

    public SubjectAdapter(List<Subject> data, Context context) {
        SharedPreferences.Editor editor = context
                .getSharedPreferences("points", Context.MODE_PRIVATE)
                .edit();
        int i = 0;
        for (Subject s :
                data) {
            if (s.getSemester() == Subject.CURRENT_SEMESTER) {
                this.data.add(s);
                //save to device
                editor.putString("name"+i, s.getName());
                editor.putBoolean("exam"+i, (s.getType().equals("Экзамен")));
                editor.putFloat("points"+i++, s.getTotalPoints().floatValue());
            }
            editor.putInt("amount", i);
            editor.apply();
        }

        this.context = context;
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //определяем содержимое RecycleView как subject_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);

        return new SubjectHolder(view);
    }


    @Override
    public void onBindViewHolder(SubjectHolder holder, int position) {
        //Заполнение данных cardView
        Subject item = data.get(position);
        holder.title.setText(item.getName());
        String s = item.getTotalPoints() + "";
        holder.points.setText(s);
        if (item.isClosed()){
//                holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.closedSubject, null));
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.closedSubject));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.cardView.setCardBackgroundColor(context.getColor(R.color.closedSubject));
            }
        }
        holder.cardView.setOnClickListener(new onCardClickListener(item));
        holder.type.setText(item.getType());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SubjectHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView title;
        TextView type;
        TextView points;

        public SubjectHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.title);
            type = (TextView) itemView.findViewById(R.id.subjectType);
            points = (TextView) itemView.findViewById(R.id.points);
        }
    }

    private class onCardClickListener implements  CardView.OnClickListener{
        Subject subject;
        public onCardClickListener(Subject item){
            subject = item;
        }
        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity) v.getContext();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, PointsViewFragment.getInstance(context, subject))
                    .addToBackStack(null)
                    .commit();
        }
    }
}
