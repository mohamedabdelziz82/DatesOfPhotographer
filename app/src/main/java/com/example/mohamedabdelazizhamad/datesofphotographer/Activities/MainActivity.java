package com.example.mohamedabdelazizhamad.datesofphotographer.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.mohamedabdelazizhamad.datesofphotographer.R;
import com.example.mohamedabdelazizhamad.datesofphotographer.models.ClientDetails;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private Button NewDate_btn, Settins_btn;
    private TextView noDates;
    private DatabaseReference datesRef;
    private RecyclerView clientsRecyclerView;
    private CardView cardView;
    private MaterialSearchView searchView;
    private DatabaseReference work_typeRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        InitializationFields();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setEllipsize(true);
        searchView.setBackgroundColor(Color.WHITE);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchClients(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SearchClients(newText);
                return true;
            }
        });
        clientsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        datesRef = FirebaseDatabase.getInstance().getReference().child("Dates");
        work_typeRef = FirebaseDatabase.getInstance().getReference().child("work_type");

        datesRef.keepSynced(true);

        NewDate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewClientActivity.class);
                startActivity(i);
                Animatoo.animateFade(MainActivity.this);
            }
        });
        Settins_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToSettingActivity();
            }
        });
    }

    private void SearchClients(String q) {

        Query query = datesRef.orderByChild("client_name").startAt(q).endAt(q + "\uf8ff");
        FirebaseRecyclerOptions<ClientDetails> option =
                new FirebaseRecyclerOptions.Builder<ClientDetails>()
                        .setQuery(query, ClientDetails.class)
                        .build();
        FirebaseRecyclerAdapter<ClientDetails, DatesHolder> adapter = new FirebaseRecyclerAdapter<ClientDetails, DatesHolder>(option) {
            @Override
            protected void onBindViewHolder(@NonNull DatesHolder holder, final int position, @NonNull final ClientDetails model) {
                holder.name.setText(model.getClient_name());
                holder.date.setText(model.getDate());
                holder.place.setText(model.getPlace());
                holder.phone.setText(model.getClient_phone());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        SendToViewActivity(id);
                    }
                });
                final String id = getRef(position).getKey();

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String[] items = {getResources().getString(R.string.View), getResources().getString(R.string.Edit), getResources().getString(R.string.Delete)};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(model.getClient_name())
                                .setIcon(R.drawable.icon)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SendToViewActivity(id);
                                                break;
                                            case 1:
                                                SendToUpdateActivity(id);
                                                break;
                                            case 2:
                                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle(getResources().getString(R.string.Attention))
                                                        .setMessage(getResources().getString(R.string.Areyousuretodelete) + " " + model.getClient_name() +  getResources().getString(R.string.s))
                                                        .setIcon(android.R.drawable.ic_menu_delete)
                                                        .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                datesRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ShowSnack(model.getClient_name() + " " + getResources().getString(R.string.hasbeendeleted));
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
            public DatesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dates_row, viewGroup, false);
                return new DatesHolder(v);
            }
        };
        clientsRecyclerView.clearOnChildAttachStateChangeListeners();
        clientsRecyclerView.clearOnScrollListeners();
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
        clientsRecyclerView.setAdapter(adapter);

        clientsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (cardView.isShown()) {
                        cardView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!cardView.isShown()) {
                        cardView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        adapter.startListening();
    }


    private void InitializationFields() {
        NewDate_btn = findViewById(R.id.NewDatebtn);
        Settins_btn = findViewById(R.id.settingbtn);
        noDates = findViewById(R.id.noDates);
        clientsRecyclerView = findViewById(R.id.datesRecyclerList);
        cardView = findViewById(R.id.CardView);


    }

    @Override
    protected void onStart() {
        super.onStart();
        datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GetAllClients();
                    noDates.setVisibility(View.GONE);
                } else {
                    noDates.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        work_typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    SendToSettingActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GetAllClients() {
        FirebaseRecyclerOptions<ClientDetails> options = new FirebaseRecyclerOptions.Builder<ClientDetails>()
                .setQuery(datesRef.orderByChild("date"), ClientDetails.class)
                .build();
        FirebaseRecyclerAdapter<ClientDetails, MainActivity.DatesHolder> adapter = new FirebaseRecyclerAdapter<ClientDetails, DatesHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DatesHolder holder, final int position, @NonNull final ClientDetails model) {
                holder.name.setText(model.getClient_name());
                holder.date.setText(model.getDate());
                holder.place.setText(model.getPlace());
                holder.phone.setText(model.getClient_phone());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        SendToViewActivity(id);
                    }
                });
                final String id = getRef(position).getKey();

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final String[] items = {getResources().getString(R.string.View), getResources().getString(R.string.Edit), getResources().getString(R.string.Delete)};
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                .setTitle(model.getClient_name())
                                .setIcon(R.drawable.icon)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                SendToViewActivity(id);
                                                break;
                                            case 1:
                                                SendToUpdateActivity(id);
                                                break;
                                            case 2:
                                                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(MainActivity.this)
                                                        .setTitle(getResources().getString(R.string.Attention))
                                                        .setMessage(getResources().getString(R.string.Areyousuretodelete) + " " + model.getClient_name() + getResources().getString(R.string.s))
                                                        .setIcon(android.R.drawable.ic_menu_delete)
                                                        .setPositiveButton(getResources().getString(R.string.Ok), new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                datesRef.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            ShowSnack(model.getClient_name() + " " + getResources().getString(R.string.hasbeendeleted));
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
            public DatesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dates_row, viewGroup, false);
                return new DatesHolder(v);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();


        clientsRecyclerView.setAdapter(adapter);
        clientsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (cardView.isShown()) {
                        cardView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (!cardView.isShown()) {
                        cardView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        adapter.startListening();
    }


    public static class DatesHolder extends RecyclerView.ViewHolder {
        TextView name, place, date, phone;

        public DatesHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.row_name);
            place = itemView.findViewById(R.id.row_place);
            date = itemView.findViewById(R.id.row_date);
            phone = itemView.findViewById(R.id.row_phone);

        }
    }

    //------------------------------------Option menu---------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //--------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
//        searchView.clearSuggestions();
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void SendToSettingActivity() {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(i);
        Animatoo.animateCard(MainActivity.this);
    }

    private void SendToViewActivity(String id) {
        Intent i = new Intent(MainActivity.this, ViewActivity.class);
        i.putExtra("id", id);
        startActivity(i);
        Animatoo.animateSplit(MainActivity.this);
    }

    private void SendToUpdateActivity(String id) {
        Intent i = new Intent(MainActivity.this, UpdateActivity.class);
        i.putExtra("id", id);
        startActivity(i);
        Animatoo.animateFade(MainActivity.this);
    }

    @SuppressLint("NewApi")
    private void ShowSnack(String message) {
        Snackbar mSnackBar = Snackbar.make(findViewById(R.id.MainActivity), message, Snackbar.LENGTH_LONG);
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

}
