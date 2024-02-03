package com.example.shelved;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference mRefBase;
    private List<String> tasks;
    private List<String> keys;

    private ListView listView;
    private Toolbar toolbar;
    private ArrayAdapter<String> adapter, adapter2;
    private FirebaseUser user;
    private Button addButton;
    private EditText inputTask;

    private Switch swis;
    boolean end = false;
    boolean deleteMode = false;
    String cathegorry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        listView = findViewById(R.id.discr_for_task);
        tasks = new ArrayList<>();
        keys = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, keys);

        user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        mRef = FirebaseDatabase.getInstance().getReference(MessageFormat.format("db/{0}/categories", user.getUid()));
        mRefBase = mRef;
        getDataFromDB();
        setOnClickItem();
        listView.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(end);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRef = FirebaseDatabase.getInstance().getReference(MessageFormat.format("db/{0}/categories/", user.getUid()));
                getDataFromDB();
                end = false;
                getSupportActionBar().setDisplayHomeAsUpEnabled(end);
            }
        });


        inputTask = (EditText) findViewById(R.id.et_inputTask);
        addButton = (Button) findViewById(R.id.btn_add);
        findViewById(R.id.btn_add).setOnClickListener(this);

        swis = (Switch) findViewById(R.id.switch1);
        swis.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                deleteMode = isChecked;
            }
        });
    }



    private void getDataFromDB(){
        ValueEventListener vListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (tasks.size() != 0){ tasks.clear(); keys.clear();}


//                GenericTypeIndicator<String> t = new GenericTypeIndicator<String>() {};
//
//                for (DataSnapshot ds : snapshot.getChildren()){
//                    String temp;
//                    if (ds.hasChildren() || ds.getValue() == "")
//                        temp = ds.getKey();
//                    else temp = ds.getValue(String.class);
//
//
//                    tasks.add(temp);
//                }
                GenericTypeIndicator<String> t = new GenericTypeIndicator<String>() {};

                for (DataSnapshot ds : snapshot.getChildren()){
                    String temp;
                    temp = ds.getKey();
                    keys.add(temp);
                }

                for (DataSnapshot ds : snapshot.getChildren()){
                    String temp;
                    if (ds.hasChildren() || ds.getValue() == "")
                        temp = ds.getKey();
                    else temp = ds.getValue(String.class);
                    tasks.add(temp);
                }

                adapter.notifyDataSetChanged();
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mRef.addValueEventListener(vListener);
    }


    private void setOnClickItem(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!deleteMode){
                    if (!end) {
                        String category = tasks.get(position);
                        cathegorry = category;
                        mRef = FirebaseDatabase.getInstance().getReference(MessageFormat.format("db/{0}/categories/{1}", user.getUid(), category));
                        getDataFromDB();
                        end = true;
                        getSupportActionBar().setDisplayHomeAsUpEnabled(end);


                    }
                }else {
                    if (!end || mRef == mRefBase) {
                        String category = tasks.get(position);
                        mRef = FirebaseDatabase.getInstance().getReference(MessageFormat.format("db/{0}/categories/{1}", user.getUid(), category));
                        mRef.removeValue();
                    }
                    else {
                        String category = keys.get(position);
                        mRef = FirebaseDatabase.getInstance().getReference(MessageFormat.format("db/{0}/categories/{1}/{2}", user.getUid(), cathegorry, category));
                        mRef.removeValue();
                    }

                    //Remove the item from the adapter.
                    tasks.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            String temp = inputTask.getText().toString();
            mRef.push().setValue(temp);
            inputTask.setText("");
            getDataFromDB();
        }
    }
}