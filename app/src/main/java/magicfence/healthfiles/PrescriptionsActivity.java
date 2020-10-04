package magicfence.healthfiles;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrescriptionsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference prescRef;
    FirebaseAuth mAuth;
    String currentUserID,dt,dnamee;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    TextView msgTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescriptions);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        msgTV = (TextView) findViewById(R.id.pmesg1);

        prescRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID)
                .child("Prescriptions");

        prescRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                   recyclerView.setVisibility(View.GONE);
                   msgTV.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.prescriptions_recycler_view);

        FirebaseRecyclerOptions<Prescriptions> options =
                new FirebaseRecyclerOptions.Builder<Prescriptions>()
                        .setQuery(prescRef, Prescriptions.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Prescriptions,PrescriptionsViewHolder>(options)
        {

            @NonNull
            @Override
            public PrescriptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.prescription_view_layout,parent,false);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent presIntent = new Intent(PrescriptionsActivity.this, PresViewActivity.class);
                        presIntent.putExtra("key",dt);
                        presIntent.putExtra("dname",dnamee);
                        startActivity(presIntent);
                    }
                });

                return new PrescriptionsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PrescriptionsViewHolder holder, int position, @NonNull Prescriptions model) {
                dnamee = model.getDr_name();
                holder.setDr_name(dnamee);
                dt = getRef(position).getKey().toString();
                holder.setDate(dt);

            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseRecyclerAdapter != null)
            firebaseRecyclerAdapter.startListening();
    }
}

class PrescriptionsViewHolder extends RecyclerView.ViewHolder
{
    View mView;
    public PrescriptionsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }
    public void setDr_name(String dr_name) {

        TextView tview = mView.findViewById(R.id.presc_view_dname);
        tview.setText(dr_name);
    }
    public void setDate(String date)
    {
        TextView tview = mView.findViewById(R.id.presc_view_date);
        tview.setText(date);
    }
}
