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

public class ReportsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView msgTv;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    FirebaseAuth mAuth;
    String currentUserID,rptitle,rpdate;
    DatabaseReference repRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        recyclerView = (RecyclerView) findViewById(R.id.reports_recycler_view);
        msgTv = (TextView) findViewById(R.id.rmesg1);
        repRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID).child("Reports");

        repRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists())
                {
                    recyclerView.setVisibility(View.GONE);
                    msgTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Reports> options =
                new FirebaseRecyclerOptions.Builder<Reports>()
                        .setQuery(repRef, Reports.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Reports,ReportsViewHolder>(options)
                {
                    @NonNull
                    @Override
                    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.reports_view_layout, parent,false);

                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                               Intent repIntent = new Intent(ReportsActivity.this,RepViewActivity.class);
                               repIntent.putExtra("title",rptitle);
                               repIntent.putExtra("date",rpdate);
                               startActivity(repIntent);
                            }
                        });

                        return new ReportsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ReportsViewHolder holder, int position, @NonNull Reports model) {
                        rptitle = model.getTitle();
                        rpdate = model.getDate();
                        holder.setTitle(rptitle);
                        holder.setDate(rpdate);
                    }
                };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseRecyclerAdapter.startListening();
    }
}
class ReportsViewHolder extends RecyclerView.ViewHolder
{
    View mView;
    public ReportsViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setTitle(String title) {
        TextView textView = mView.findViewById(R.id.rep_view_title);
        textView.setText(title);
    }
    public void setDate(String date) {
        TextView textView = mView.findViewById(R.id.rep_view_date);
        textView.setText(date);
    }

}