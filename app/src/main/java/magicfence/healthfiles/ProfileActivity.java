package magicfence.healthfiles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {
    userProfileDetails user;
    private EditText weightEdit, heightEdit, genderEdit, ageEdit;
    private TextView weightT, heightT,genderT,ageT;
    private Button SaveDetails;
    private Button editDetails;
    private int i =0;
    private RelativeLayout saveCardView,editCardView;
    private FirebaseAuth mAuth;
    String currentUserID = FirebaseAuth.getInstance().getUid();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Patients/"+currentUserID+"/ProfileDetails");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retreiveUserDetails(mDatabase);
        setContentView(R.layout.activity_profile);
        heightT = (TextView)findViewById(R.id.users_height);
        weightT = (TextView)findViewById(R.id.users_weight);
        ageT = (TextView)findViewById(R.id.users_age);
        genderT = (TextView)findViewById(R.id.users_gender);
        weightEdit = (EditText) findViewById(R.id.user_weight);
        heightEdit = (EditText) findViewById(R.id.user_height);
        genderEdit = (EditText) findViewById(R.id.user_gender);
        ageEdit = (EditText) findViewById(R.id.user_age);
        SaveDetails = (Button) findViewById(R.id.saveDetails_button);
        editDetails = (Button) findViewById(R.id.editDetail_button);
        saveCardView = (RelativeLayout) findViewById(R.id.save_cardView);
        editCardView = (RelativeLayout) findViewById(R.id.edit_cardView);

        SaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String weight,height,gender,age;
                weight = weightEdit.getText().toString();
                height = heightEdit.getText().toString();
                gender = genderEdit.getText().toString();
                age = ageEdit.getText().toString();
                user = new userProfileDetails(weight,height,gender,age);
                mDatabase.setValue(user);
                genderT.setText(gender);
                weightT.setText(weight);
                heightT.setText(height);
                ageT.setText(age);
                saveCardView.setVisibility(View.GONE);
                editCardView.setVisibility(View.VISIBLE);

            }
        });
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editCardView.setVisibility(View.GONE);
                saveCardView.setVisibility(View.VISIBLE);

            }
        });
    }
    public class userProfileDetails{
        public String weight,height,gender,age;
        public userProfileDetails() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }
        public userProfileDetails(String weight, String height,String gender,String age) {
            this.weight = weight;
            this.height = height;
            this.age = age;
            this.gender=gender;
        }


    }
    public void retreiveUserDetails(DatabaseReference mDatabase){
        i=0;
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshots : snapshot.getChildren()){
                    if(i==0){
                        ageT.setText(snapshots.getValue(String.class));
                    }
                    else if(i==1){
                        genderT.setText(snapshots.getValue(String.class));

                    }
                    else if(i==2){
                        heightT.setText(snapshots.getValue(String.class));

                    }
                    else if(i==3){
                        weightT.setText(snapshots.getValue(String.class));
                    }
                    i++;
                    Log.d("ProfileActivity",snapshots.getValue(String.class));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}