package com.example.expensesmanagerapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.appsearch.StorageInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.expensesmanagerapplication.databinding.ActivityAddExpenseBinding;
import com.example.expensesmanagerapplication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class AddExpenseActivity extends AppCompatActivity {

    ActivityAddExpenseBinding binding;
    private Spinner categorySpinner;
    private ExpenseModel expenseModel;

    private String type;

     Button deleteButton;
     Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddExpenseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        categorySpinner = findViewById(R.id.categorySpinner);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        submitButton = findViewById(R.id.submitExpenseButton); // Initialize the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expenseModel == null) {
                    createExpense();
                } else {
                    updateExpense();
                }
            }
        });

        deleteButton = findViewById(R.id.submitDeleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExpense();
            }
        });




        type=getIntent().getStringExtra("type");
        expenseModel=(ExpenseModel) getIntent().getSerializableExtra("model");
        if(type==null){
            type=expenseModel.getType();
            binding.amount.setText(String.valueOf(expenseModel.getAmount()));
            binding.note.setText(expenseModel.getNote());


        }

        if(type.equals("Income")){
            binding.incomeRadio.setChecked(true);
        }
        else{
            binding.expenseRadio.setChecked(true);
        }

        binding.incomeRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Income";
            }
        });
        binding.expenseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="Expense";
            }
        });

    }



    private void createExpense(){
        String expenseId= UUID.randomUUID().toString();
        String amount=binding.amount.getText().toString();
        String note=binding.note.getText().toString();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        boolean incomeChecked=binding.incomeRadio.isChecked();

        if(incomeChecked){
            type="Income";
        }
        else{
            type="Expense";
        }

        if(amount.trim().length()==0){
            binding.amount.setError("Empty");
            return;
        }
        ExpenseModel expenseModel=new ExpenseModel(type,expenseId,note,selectedCategory,Long.parseLong(amount), Calendar.getInstance().getTimeInMillis(), FirebaseAuth.getInstance().getUid());
        FirebaseFirestore
                .getInstance()
                .collection("Expenses")
                .document(expenseId)
                .set(expenseModel);
        finish();

    }

    private void updateExpense(){

                String expenseId= expenseModel.getExpenseId();
                String amount=binding.amount.getText().toString();
                String note=binding.note.getText().toString();
                String selectedCategory = categorySpinner.getSelectedItem().toString();

                boolean incomeChecked=binding.incomeRadio.isChecked();

                if(incomeChecked){
                    type="Income";
                }
                else{
                    type="Expense";
                }

                if(amount.trim().length()==0){
                    binding.amount.setError("Empty");
                    return;
                }
                ExpenseModel model=new ExpenseModel(type,expenseId,note,selectedCategory,Long.parseLong(amount),expenseModel.getTime(), FirebaseAuth.getInstance().getUid());
                FirebaseFirestore
                        .getInstance()
                        .collection("Expenses")
                        .document(expenseId)
                        .set(model);
                finish();





    }
    private void deleteExpense(){

        FirebaseFirestore.getInstance()
                .collection("Expenses")
                .document(expenseModel.getExpenseId())
                .delete();
        finish();
    }
}
