package com.example.mohamedabdelazizhamad.datesofphotographer.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.mohamedabdelazizhamad.datesofphotographer.R;
import com.example.mohamedabdelazizhamad.datesofphotographer.models.ModelDate;
import com.example.mohamedabdelazizhamad.datesofphotographer.models.WorksModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UpdateActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private EditText editTextName, editTextphone, editTextDate,
            editTextPlace, editTextTypeWork, editTextTotalprice, editTextPaid, editTextResidual, editTextTime;
    private ImageButton saveImageButton;
    private Calendar mCalendar;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String mDate, mTime;
    private ModelDate date;
    private DatabaseReference remaindersRef, work_typeRef;
    private DatabaseReference datesRef;
    private List<String> names, prices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        InitializationFields();
        if (!isNetworkAvailable()) {
            SendUserToMainActivity();
            Toast.makeText(this, getResources().getString(R.string.ThereisnoInternetConnection), Toast.LENGTH_SHORT).show();
        }
        datesRef = FirebaseDatabase.getInstance().getReference().child("Dates");
        datesRef.keepSynced(true);
        work_typeRef = FirebaseDatabase.getInstance().getReference().child("work_type");


        mCalendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        FirebaseApp.initializeApp(this);
        String id = getIntent().getExtras().get("id").toString();
        GetClient(id);

        remaindersRef = FirebaseDatabase.getInstance().getReference();
        remaindersRef.child("Dates").keepSynced(true);
        mYear = now.get(Calendar.YEAR);
        mMonth = now.get(Calendar.MONTH);
        mDay = now.get(Calendar.DAY_OF_MONTH) + 1;
        mHour = now.get(Calendar.HOUR_OF_DAY);
        mMinute = now.get(Calendar.MINUTE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;
        final String s = id;

        names = new ArrayList<>();
        prices = new ArrayList<>();
        GetWork();
        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    saveReminderFirebase(s);
                } else {
                    SendUserToMainActivity();
                    Toast.makeText(UpdateActivity.this, getResources().getString(R.string.ThereisnoInternetConnection), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void InitializationFields() {
        myToolbar = findViewById(R.id.toolbarUpdate);
        setSupportActionBar(myToolbar);
        myToolbar.setTitle(getResources().getString(R.string.UpdateClient));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.update_bar_layout, null);
        actionBar.setCustomView(actionBarView);

        saveImageButton = findViewById(R.id.updateImageButton);

        editTextDate = findViewById(R.id.update_date);
        editTextName = findViewById(R.id.update_name);
        editTextphone = findViewById(R.id.update_phone);
        editTextPlace = findViewById(R.id.update_Placeofwork);
        editTextTypeWork = findViewById(R.id.update_type_work);
        editTextTotalprice = findViewById(R.id.update_total_price);
        editTextPaid = findViewById(R.id.update_Paid_up);
        editTextResidual = findViewById(R.id.update_Residual);

        editTextDate.setFocusable(false);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogOfDate();
            }
        });
        editTextDate.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogOfDate();
                return true;
            }
        });

        editTextTypeWork.setFocusable(false);
        editTextResidual.setFocusable(false);
        editTextTypeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWorkDialog();
            }
        });
        editTextTypeWork.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showWorkDialog();
                return true;
            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void GetClient(String id) {
        datesRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("client_name")) {
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
    //----------------------------------------------------------------------------------------------

    private void saveReminderFirebase(String id) {
        String name = editTextName.getText().toString();
        String phone = editTextphone.getText().toString();
        String date = editTextDate.getText().toString();
        String place = editTextPlace.getText().toString();
        String work = editTextTypeWork.getText().toString();
        String total = editTextTotalprice.getText().toString();
        String paid = editTextPaid.getText().toString();

//        String time = editTextTime.getText().toString();
        if (name.equals("") || name == null) {
            editTextName.setError(getString(R.string.enterClientName));
        } else if (name.length() < 3) {
            editTextName.setError(getString(R.string.InvalidClientName));
        } else if (phone.equals("") || phone == null) {
            editTextName.setError(null);
            editTextphone.setError(getString(R.string.enterPhoneNumber));
        } else if (date.equals("") || date == null) {
            editTextphone.setError(null);
            editTextDate.setError(getString(R.string.selectDate));
        } else if (place.equals("") || place == null) {
            editTextDate.setError(null);
            editTextPlace.setError(getString(R.string.enterPlaceofwork));
        } else if (work.equals("") || work == null) {
            editTextPlace.setError(null);
            editTextTypeWork.setError(getString(R.string.selectTypeOfwork));
        } else if (paid.equals("") || paid == null) {
            editTextTypeWork.setError(null);
            editTextPaid.setError(getString(R.string.enterPaidmoney));
        }
//        else if (state == "true") {
//            if (time.equals("") || time == null) {
//                editTextPaid.setError(null);
//                editTextTime.setError(getString(R.string.selectTime));
//            }
//        }
        else {
            int Totalint = Integer.valueOf(total);
            int paidint = Integer.valueOf(paid);
            String residual = null;
            editTextPaid.setError(null);

            if (paidint > Totalint) {
                editTextPaid.setError(getResources().getString(R.string.Theamountpaidishigherthanthetotalprice));
            } else {


                int residualint = Totalint - paidint;
                editTextResidual.setText(String.valueOf(residualint));
                residual = editTextResidual.getText().toString();

//            progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle(getResources().getString(R.string.CreatingNewDate));
//            progressDialog.setCanceledOnTouchOutside(false);
//            progressDialog.show();

                HashMap<String, Object> profiledate = new HashMap<>();
                profiledate.put("client_name", name);
                profiledate.put("client_phone", phone);
                profiledate.put("date", date);
                profiledate.put("place", place);
                profiledate.put("total_price", total);
                profiledate.put("type_work", work);
                profiledate.put("paid", paid);
                profiledate.put("residual", residual);
                remaindersRef.child("Dates").child(id).updateChildren(profiledate).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();
                            Toast.makeText(UpdateActivity.this, getResources().getString(R.string.clientupdatedsuccessfully), Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
                        } else {
                            ShowSnack(getResources().getString(R.string.Failedtosave));
//                        progressDialog.dismiss();
                        }
                    }
                });
            }
        }

    }

    private void SendUserToMainActivity() {

        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();

    }

    //----------------------------------------------------------------------------------------------
    private void DialogOfDate() {
        int year = mCalendar.get(Calendar.YEAR);
        final int month = mCalendar.get(Calendar.MONTH);
        final int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        date = new ModelDate();

        DatePickerDialog dialog = new DatePickerDialog(UpdateActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        monthOfYear++;

                        mDay = dayOfMonth;
                        mMonth = monthOfYear;
                        mYear = year;
                        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;

                        date.setmDay(dayOfMonth);
                        date.setmYear(year);
                        date.setmMonth(monthOfYear);

                        editTextDate.setText(mDate);


                    }

                }, year, month, day);

        dialog.show();
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(UpdateActivity.this);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @SuppressLint("NewApi")
    private void ShowSnack(String message) {
        Snackbar mSnackBar = Snackbar.make(findViewById(R.id.UpdateActivity), message, Snackbar.LENGTH_LONG);
        View view = mSnackBar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        view.setBackgroundColor(getColor(R.color.colorNameApp));
        TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
        mainTextView.setTextColor(getColor(R.color.colorPrimary));
        mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mSnackBar.show();
    }

    private void showWorkDialog() {
        final String[] name_strings = GetStringArray(names);
        final String[] price_strings = GetStringArray(prices);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.selectTypeOfwork))
                .setIcon(R.drawable.type)
                .setSingleChoiceItems(name_strings, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String n = name_strings[which];
                        editTextTypeWork.setText(n);
                        editTextTotalprice.setText(price_strings[which]);

                    }
                }).setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();

    }

    private void GetWork() {

        work_typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String s1 = null;
                    for (DataSnapshot workSnapshot : dataSnapshot.getChildren()) {
                        WorksModel model = workSnapshot.getValue(WorksModel.class);
                        String work_name = model.getWork_name();
                        s1 = model.getWork_name();
                        String work_price = model.getWork_price();
                        Log.e("name", work_name);
                        names.add(work_name);
                        prices.add(work_price);
                    }
                    Log.e("name", s1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public String[] GetStringArray(List<String> arr) {

        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {

            // Assign each value to String array
            str[j] = arr.get(j);
        }

        return str;
    }
}
