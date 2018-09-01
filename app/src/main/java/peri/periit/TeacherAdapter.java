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
    ArrayList<String> status = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();

    public TeacherAdapter(ArrayList<String> rollno,ArrayList<String> uid, ArrayList<String> status, ArrayList<String> name){
        this.rollno = rollno;
        this.uid = uid;
        this.status = status;
        this.name = name;
    }

    @Override
    public TeacherAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_teacher, parent, false);
        return new TeacherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TeacherAdapter.ViewHolder holder, final int position) {


        if (status.get(position).equals("0"))
        {
            holder.cvteacher.setCardBackgroundColor(Color.parseColor("#FF0000"));
        }else if (status.get(position).equals("1")){
            holder.cvteacher.setCardBackgroundColor(Color.parseColor("#32CD32"));
        }else if (status.get(position).equals("2")) {
            holder.cvteacher.setCardBackgroundColor(Color.parseColor("#FF8000"));
        }
        holder.cvteacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences categoriesPref = holder.itemView.getContext().getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = categoriesPref.edit();
                editor.putString("studentuid",uid.get(position));
                editor.commit();

                Intent intent = new Intent (holder.itemView.getContext(), StudentView.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.tvteacherrollno.setText("Roll No. " + rollno.get(position));
        holder.tvteachername.setText("Name: "+name.get(position));
    }

    @Override
    public int getItemCount() {
        return rollno.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvteacherrollno,tvteachername;
        public CardView cvteacher;

        public ViewHolder(View itemView) {
            super(itemView);
            tvteachername = itemView.findViewById(R.id.tvteachername);
            tvteacherrollno = itemView.findViewById(R.id.tvteacherrollno);
            cvteacher = itemView.findViewById(R.id.cvteacher);
        }
    }

}

