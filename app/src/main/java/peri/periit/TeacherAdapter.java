package peri.periit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static peri.periit.MainActivity.CATPREF;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    ArrayList<String> rollno = new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();

    public TeacherAdapter(ArrayList<String> rollno,ArrayList<String> uid){
        this.rollno = rollno;
        this.uid = uid;
    }

    @Override
    public TeacherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_teacher, parent, false);
        return new TeacherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TeacherAdapter.ViewHolder holder, final int position) {

        holder.cvteacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(),uid.get(position), Toast.LENGTH_SHORT).show();
                SharedPreferences categoriesPref = holder.itemView.getContext().getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = categoriesPref.edit();
                editor.putString("studentuid",uid.get(position));
                editor.commit();

                Intent intent = new Intent (holder.itemView.getContext(), StudentView.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.tvteacherrollno.setText(rollno.get(position));
    }

    @Override
    public int getItemCount() {
        return rollno.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvteacherrollno;
        public CardView cvteacher;

        public ViewHolder(View itemView) {
            super(itemView);
            tvteacherrollno = itemView.findViewById(R.id.tvteacherrollno);
            cvteacher = itemView.findViewById(R.id.cvteacher);
        }
    }

}

