package com.example.lab5_starter;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CityDialogFragment.CityDialogListener {

    private Button addCityButton;
    private RecyclerView cityRecyclerView;

    private ArrayList<City> cityArrayList;
    private CityRecyclerAdapter cityRecyclerAdapter;
    private FirebaseFirestore db;
    private CollectionReference citiesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addCityButton = findViewById(R.id.buttonAddCity);
        cityRecyclerView = findViewById(R.id.recyclerViewCities);

        db = FirebaseFirestore.getInstance();
        citiesRef = db.collection("cities");

        cityArrayList = new ArrayList<>();
        cityRecyclerAdapter = new CityRecyclerAdapter(cityArrayList,city -> {
            CityDialogFragment cityDialogFragment =
                    CityDialogFragment.newInstance(city);
            cityDialogFragment.show(getSupportFragmentManager(),"City Details");
        });

        cityRecyclerView.setAdapter(cityRecyclerAdapter);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        citiesRef.addSnapshotListener(((value, error) -> {
            if (error != null){
                Log.e("Firestone",error.toString());
            }
            if (value != null && !value.isEmpty()){
                cityArrayList.clear();
                for (QueryDocumentSnapshot snapshot : value){
                    String name = snapshot.getString("name");
                    String province = snapshot.getString("province");

                    cityArrayList.add(new City(name,province));

                }
                cityRecyclerAdapter.notifyDataSetChanged();
            }
        }));

        addCityButton.setOnClickListener(view -> {
            CityDialogFragment cityDialogFragment = new CityDialogFragment();
            cityDialogFragment.show(getSupportFragmentManager(),"Add City");
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                City cityToDelete = cityArrayList.get(position);

                citiesRef.document(cityToDelete.getName()).delete()
                        .addOnSuccessListener(aVoid -> Log.d("Firestone","Deleted:"+ cityToDelete.getName()))
                        .addOnFailureListener(e -> Log.e("Firestore","Error Deleteing city",e));

                cityArrayList.remove(position);
                cityRecyclerAdapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(cityRecyclerView);


    }

    @Override
    public void updateCity(City city, String title, String year) {
        String oldName     = city.getName();
        String newName     = title;
        String newProvince = year;

        if (!oldName.equals(newName)) {
            citiesRef.document(oldName).delete()
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Deleted old doc: " + oldName))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error deleting old doc", e));
        }

        city.setName(newName);
        city.setProvince(newProvince);

        citiesRef.document(newName).set(city)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Upserted: " + newName))
                .addOnFailureListener(e -> Log.e("Firestore", "Error upserting", e));

    }

    @Override
    public void addCity(City city){
        DocumentReference docRef = citiesRef.document(city.getName());
        docRef.set(city)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Upserted: " + city.getName()))
                .addOnFailureListener(e -> Log.e("Firestore", "Error upserting", e));
    }

}