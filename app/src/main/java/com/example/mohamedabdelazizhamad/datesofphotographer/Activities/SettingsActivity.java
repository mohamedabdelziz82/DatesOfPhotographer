package com.example.mohamedabdelazizhamad.datesofphotographer.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.mohamedabdelazizhamad.datesofphotographer.R;
import com.example.mohamedabdelazizhamad.datesofphotographer.models.WorksModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class SettingsActivity extends AppCompatActivity {
    private Button NewWork;
    private Dialog addDialog;
    private EditText addtype, addprice;
    private Button addOk_btn, addCancel_btn;
    private DatabaseReference work_typeRef;
    private TextView noType;
    private RecyclerView worksRecyclerView;
    private static WorksModel m;
    private static ID mid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//------------------ InitializationFields------------
        noType = findViewById(R.id.noTypes);
        worksRecyclerView = findViewById(R.id.settingRecyclerView);
        worksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        m = new WorksModel();
        mid = new ID();
        work_typeRef = FirebaseDatabase.getInstance().getReference().child("work_type");
        work_typeRef.keepSynced(true);
        NewWork = findViewById(R.id.NewWorkbtn);
        NewWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.ThereisnoInternetConnection), Toast.LENGTH_SHORT).show();
                } else {
                    ShowAddWorkDialog();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        work_typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GetAllWorks();
                    noType.setVisibility(View.GONE);
                } else {
                    noType.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
     private void GetAllWorks() {
        FirebaseRecyclerOptions<WorksModel>  options = new FirebaseRecyclerOptions.Builder<WorksModel>()
                .setQuery(work_typeRef, WorksModel.class)
                .build();
        FirebaseRecyclerAdapter<WorksModel, WorksHolder> adapter
                = new FirebaseRecyclerAdapter<WorksModel, WorksHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull WorksHolder holder, int position, @NonNull final WorksModel model) {
                holder.workName.setText(model.getWork_name());
                holder.workPrice.setText(model.getWork_price());
                final String key = getRef(position).getKey();
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String[] items = {getResources().getString(R.string.Edit), getResources().getString(R.string.Delete)};
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this)
                                .setTitle(model.getWork_name())
                                .setIcon(R.drawable.type)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                if (isNetworkAvailable()) {
                                                    GetWork(key);
                                                    mid.setId(key);
                                                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                                                    bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
                                                } else {
                                                    Toast.makeText(SettingsActivity.this, getResources().getString(R.string.ThereisnoInternetConnection), Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                            case 1:
                                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(SettingsActivity.this)
                                                        .setTitle(getResources().getString(R.string.Attention))
                                                        .setMessage(getResources().getString(R.string.Areyousuretodelete) + " " + model.getWork_name() + getResources().getString(R.string.s))
                                                        .setIcon(android.R.drawable.ic_menu_delete)
                                                        .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                work_typeRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ShowSnack(model.getWork_name() + " " + getResources().getString(R.string.hasbeendeleted));
                                                                        } else {
                                                                            ShowSnack(getResources().getString(R.string.somethingisworng));
                                                                        }
                                                                    }
                                                                });
                                                                dialog.dismiss();
                                                            }
                                                        }).setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                AlertDialog alert = deleteBuilder.create();
                                                alert.setCanceledOnTouchOutside(false);
                                                alert.show();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                        return true;
                    }
                });
            }

            @NonNull
            @Override
            public WorksHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.works_row, viewGroup, false);
                return new WorksHolder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
        worksRecyclerView.setAdapter(adapter);
        worksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (NewWork.isShown()) {
                        NewWork.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!NewWork.isShown()) {
                        NewWork.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        adapter.startListening();

    }

    public static class WorksHolder extends RecyclerView.ViewHolder {
        TextView workName, workPrice;

        public WorksHolder(@NonNull View itemView) {
            super(itemView);
            workName = itemView.findViewById(R.id.row_workname);
            workPrice = itemView.findViewById(R.id.row_workprice);
        }
    }


    private void GetWork(String id) {
        work_typeRef.child(id).orderByChild("work_price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("work_name")) {
                    String work_name = dataSnapshot.child("work_name").getValue().toString();
                    String work_price = dataSnapshot.child("work_price").getValue().toString();

                    m.setWork_name(work_name);
                    m.setWork_price(work_price);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ShowAddWorkDialog() {
        addDialog = new Dialog(SettingsActivity.this);
        addDialog.setContentView(R.layout.custom_dialog);

        addDialog.setTitle(getString(R.string.NewWork));
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addDialog.setCanceledOnTouchOutside(false);
        addDialog.show();


        // --------------InitializationFields-------------------------
        addtype = addDialog.findViewById(R.id.addcustomtype);
        addprice = addDialog.findViewById(R.id.addcustomprice);
        addOk_btn = addDialog.findViewById(R.id.addOk_btn);
        addCancel_btn = addDialog.findViewById(R.id.addCancel_btn);
        //--------------------- OK Button ----------------------------
        addOk_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String work_name = addtype.getText().toString();
                String work_price = addprice.getText().toString();

                if (work_name == null || work_name.equals("")) {
                    addtype.setError(getResources().getString(R.string.EnterWorkName));
                } else if (work_price == null || work_price.equals("")) {
                    addtype.setError(null);
                    addprice.setError(getResources().getString(R.string.EnterTotalPrice));
                } else {
                    addprice.setError(null);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("work_name", work_name);
                    hashMap.put("work_price", work_price);
                    work_typeRef.push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.WorkSaved), Toast.LENGTH_SHORT).show();
                                addDialog.dismiss();
                            } else {
                                Toast.makeText(SettingsActivity.this, getResources().getString(R.string.somethingisworng), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //--------------------- Cancel Button ----------------------------
        addCancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
    }

    //-----------------------------Check Internet Connection-------------------------------------------
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //--------------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);

    }

    //    ----------------------------------------------------------------------------------------------
    @SuppressLint("NewApi")
    private void ShowSnack(String message) {
        Snackbar mSnackBar = Snackbar.make(findViewById(R.id.SettingsActivity), message, Snackbar.LENGTH_LONG);
        View view = mSnackBar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM;
        view.setLayoutParams(params);
        view.setBackgroundColor(getColor(R.color.colorPrimary));
        TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
        mainTextView.setTextColor(getColor(R.color.colorNameApp));
        mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mSnackBar.show();
    }
//    --------------------------------------------------------------------------------------------------

    @SuppressLint("ValidFragment")
    public static class BottomSheetDialog extends BottomSheetDialogFragment {
        DatabaseReference work_type = FirebaseDatabase.getInstance().getReference().child("work_type");
        private EditText work_name, work_price;

        public BottomSheetDialog() {
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View v = View.inflate(getContext(), R.layout.bottom_sheet_dialog, null);
            dialog.setContentView(v);
            dialog.setCanceledOnTouchOutside(false);

            work_name = v.findViewById(R.id.dialog_update_customtype);
            work_price = v.findViewById(R.id.dialog_updatecustomprice);
            work_name.setText(m.getWork_name());
            work_price.setText(m.getWork_price());

            Button ok = v.findViewById(R.id.dialog_updateOk_btn);
            Button cancel = v.findViewById(R.id.dialog_updateCancel_btn);

            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = work_name.getText().toString();
                    String price = work_price.getText().toString();

                    if (name == null || name.equals("")) {
                        work_name.setError(getResources().getString(R.string.EnterWorkName));
                    } else if (price == null || price.equals("")) {
                        work_name.setError(null);
                        work_price.setError(getResources().getString(R.string.EnterTotalPrice));
                    } else {
                        work_price.setError(null);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("work_name", name);
                        hashMap.put("work_price", price);
                        String id = mid.getId();
                        work_type.child(id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), getResources().getString(R.string.WorkUpdated), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getContext(), getResources().getString(R.string.somethingisworng), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    //    --------------------------------------------------------------------------------------------------
    public class ID {
        private String id;

        public ID() {
        }

        public ID(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
