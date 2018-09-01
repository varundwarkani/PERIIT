package peri.periit;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherDetails extends AppCompatActivity {

    RecyclerView rvteacher;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<String> rollno = new ArrayList<>();
    ArrayList<String> uid = new ArrayList<>();
    TeacherAdapter teacherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);
        rvteacher = findViewById(R.id.rvteacher);
        rvteacher.setHasFixedSize(true);
        rvteacher.setLayoutManager(new LinearLayoutManager(TeacherDetails.this));
        teacherAdapter = new TeacherAdapter(rollno,uid);
        rvteacher.setAdapter(teacherAdapter);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //set adapter
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("student");
                    usersRef.orderByChild("rollno").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            rollno.clear();
                            uid.clear();
                            teacherAdapter = new TeacherAdapter(rollno,uid);
                            rvteacher.setAdapter(teacherAdapter);


                            for (final DataSnapshot postSnapshots: dataSnapshot.getChildren()) {

                                String rollno1 = postSnapshots.child("name").getValue().toString();
                                String uid1 = postSnapshots.child("uid").getValue().toString();
                                rollno.add(rollno1);
                                uid.add(uid1);
                                teacherAdapter = new TeacherAdapter(rollno,uid);
                                rvteacher.setAdapter(teacherAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
