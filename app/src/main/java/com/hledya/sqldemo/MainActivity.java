package com.hledya.sqldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // references to buttons and other controls on the layout
    Button btn_add, btn_viewAll;
    EditText et_name, et_age;
    Switch sw_activeCustomer;
    ListView lv_customerList;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_add = findViewById(R.id.btn_add);
        btn_viewAll = findViewById(R.id.btn_viewAll);
        et_age = findViewById(R.id.et_age);
        et_name = findViewById(R.id.et_name);
        sw_activeCustomer = findViewById(R.id.sw_active);
        lv_customerList = findViewById(R.id.lv_customerList);

        dataBaseHelper = new DataBaseHelper(MainActivity.this);
        showCustomersOnListView();

        // button listeners for the add and view all buttons
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerModel customerModel = null;
                try{
                    customerModel = new CustomerModel(-1,  et_name.getText().toString(),
                            Integer.parseInt(et_age.getText().toString()), sw_activeCustomer.isChecked());
                    Toast.makeText(MainActivity.this, customerModel.toString(), Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Toast.makeText(MainActivity.this, "invalid input", Toast.LENGTH_SHORT).show();
                    return;
                }

//                boolean success = dataBaseHelper.addOne(customerModel);
                AddOneTask task = new AddOneTask();
                task.execute(customerModel);
            }
        });

        btn_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomersOnListView();
            }
        });

        lv_customerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerModel clickedCustomer = (CustomerModel) adapterView.getItemAtPosition(i);
                dataBaseHelper.deleteOne(clickedCustomer);
                showCustomersOnListView();
            }
        });

    }

    private class AddOneTask extends AsyncTask<CustomerModel, Void, Boolean> {

        @Override
        protected Boolean doInBackground(CustomerModel... customerModels) {
            return dataBaseHelper.addOne(customerModels[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
//            super.onPostExecute(aBoolean);
            Toast.makeText(MainActivity.this, "addOne returned "+success, Toast.LENGTH_SHORT).show();
            if (success){
                showCustomersOnListView();
            }
        }
    }

    private void showCustomersOnListView(){
        ArrayAdapter arrayAdapter = new ArrayAdapter<CustomerModel>
                (MainActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper.getEveryone());
        lv_customerList.setAdapter(arrayAdapter);
    }
}