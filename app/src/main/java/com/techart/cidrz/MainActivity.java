package com.techart.cidrz;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.techart.cidrz.constants.Constants;
import com.techart.cidrz.constants.FireBaseUtils;
import com.techart.cidrz.model.Facility;
import com.techart.cidrz.setup.LoginActivity;
import com.techart.cidrz.utils.TimeUtils;
import static com.techart.cidrz.constants.FireBaseUtils.mAuth;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private RecyclerView mFacilityList;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(MainActivity.this,FacilityUploadActivity.class);
                startActivity(loginIntent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()==null) {
                    Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        };
        mFacilityList = findViewById(R.id.rv_story);
        progressBar = findViewById(R.id.pb_loading);
        mFacilityList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mFacilityList.setLayoutManager(linearLayoutManager);
        bindView();
    }

    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void bindView() {
        FirebaseRecyclerAdapter<Facility,FacilityViewHolder> fireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Facility, FacilityViewHolder>(
                Facility.class,R.layout.item_facility,FacilityViewHolder.class, FireBaseUtils.mDatabaseFacility)
        {
            @Override
            protected void populateViewHolder(FacilityViewHolder viewHolder, final Facility model, int position) {
                progressBar.setVisibility(View.GONE);
                viewHolder.tvFacilityName.setText(model.getFacilityName());
                viewHolder.setTint(MainActivity.this);
                if (model.getImageUrl() != null){
                    viewHolder.setIvImage(MainActivity.this,model.getImageUrl());
                }
                if (model.getTimeCreated() != null) {
                    String time = TimeUtils.timeElapsed(model.getTimeCreated());
                    viewHolder.tvTime.setText(time);
                }

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this,FullImageActivity.class);
                        intent.putExtra(Constants.IMAGE_URL,model.getImageUrl());
                        startActivity(intent);
                    }
                });
            }
        };
        mFacilityList.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            logOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE)
                        {
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.are_you_sure))
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }
}
