package com.example.mohamedabdelazizhamad.datesofphotographer.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.datesofphotographer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ViewActivity extends AppCompatActivity {
    private DatabaseReference datesRef;
    private EditText editTextName, editTextphone, editTextDate,
            editTextPlace, editTextTypeWork, editTextTotalprice, editTextPaid, editTextResidual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        InitializationFields();

        datesRef = FirebaseDatabase.getInstance().getReference().child("Dates");
        datesRef.keepSynced(true);


        String id = getIntent().getExtras().get("id").toString();
//        Toast.makeText(this, "id: " + id, Toast.LENGTH_SHORT).show();

        GetClient(id);
    }

    private void InitializationFields() {

        editTextName = findViewById(R.id.view_Clientname);
        editTextphone = findViewById(R.id.view_ClientPhone);
        editTextDate = findViewById(R.id.view_Date);
        editTextPlace = findViewById(R.id.view_Placeofwork);
        editTextTypeWork = findViewById(R.id.view_Typeofwork);
        editTextTotalprice = findViewById(R.id.view_Total_price);
        editTextPaid = findViewById(R.id.view_Paidup);
        editTextResidual = findViewById(R.id.view_Residual);

    }

    private void GetClient(String id) {
        datesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                     if (dataSnapshot.exists()&& dataSnapshot.hasChild("client_name") ){
                         String client_name = dataSnapshot.child("client_name").getValue().toString();
                         String client_phone = dataSnapshot.child("client_phone").getValue().toString();
                         String date = dataSnapshot.child("date").getValue().toString();
                         String place = dataSnapshot.child("place").getValue().toString();
                         String typeofwork = dataSnapshot.child("type_work").getValue().toString();
                         String total = dataSnapshot.child("total_price").getValue().toString();
                         String paid = dataSnapshot.child("paid").getValue().toString();
                         String residual = dataSnapshot.child("residual").getValue().toString();

                         editTextName.setText(client_name);
                         editTextphone.setText(client_phone);
                         editTextDate.setText(date);
                         editTextPlace.setText(place);
                         editTextTypeWork.setText(typeofwork);
                         editTextTotalprice.setText(total);
                         editTextPaid.setText(paid);
                         editTextResidual.setText(residual);
                     }
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
