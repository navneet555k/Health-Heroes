package magicfence.healthfiles;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class RepViewActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    String currentUserID,rtitle,rdate,rkey,link,desc,dr_name;
    TextView dNameTv,rDateTV,rDescTV;
    ImageView qrView;
    Button DeleteButton;
    Bitmap bitmap;
    DatabaseReference repRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rep_view);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        repRef = FirebaseDatabase.getInstance().getReference().child("Patients").child(currentUserID).child("Reports");

        currentUserID = mAuth.getCurrentUser().getUid();
        rtitle = getIntent().getStringExtra("title");
        rdate = getIntent().getStringExtra("date");
        rkey = rtitle + rdate;

        dNameTv = (TextView) findViewById(R.id.rep_dname);
        rDateTV = (TextView) findViewById(R.id.rep_pdate);
        rDescTV = (TextView) findViewById(R.id.rep_desc);
        qrView = (ImageView) findViewById(R.id.qr_view_rep);
        DeleteButton = (Button) findViewById(R.id.rep_del_btn);

        rDateTV.setText(rdate);

        repRef.child(rkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    desc = snapshot.child("desc").getValue().toString();
                    rDescTV.setText(desc);
                    final DatabaseReference drRef = FirebaseDatabase.getInstance().getReference().child("Doctors");
                    final String did = snapshot.child("doctor_id").getValue().toString();
                    drRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists())
                            {
                                dr_name = snapshot.child(did).child("fullname").getValue().toString();
                                dNameTv.setText(dr_name);
                                QRGEncoder qrgEncoder = new QRGEncoder(did, null, QRGContents.Type.TEXT, 100);
                                bitmap = qrgEncoder.getBitmap();
                                qrView.setImageBitmap(bitmap);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repRef.child(rkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RepViewActivity.this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
                            Intent hIntent = new Intent(RepViewActivity.this, ReportsActivity.class);
                            hIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(hIntent);
                        }
                        else
                        {
                            String msg = task.getException().getMessage();
                            Toast.makeText(RepViewActivity.this, "Error. " + msg, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }
}