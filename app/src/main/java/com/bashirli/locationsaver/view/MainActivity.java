package com.bashirli.locationsaver.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bashirli.locationsaver.DataAdapter.DataAdapter;
import com.bashirli.locationsaver.DataCollect.Data;
import com.bashirli.locationsaver.R;
import com.bashirli.locationsaver.database.DataDAO;
import com.bashirli.locationsaver.database.DataDB;
import com.bashirli.locationsaver.databinding.ActivityMainBinding;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
private ActivityMainBinding binding;
    DataDB dataDB;
    DataDAO dataDAO;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
      dataDB= Room.databaseBuilder(getApplicationContext(),DataDB.class,"Data").build();
      dataDAO=dataDB.dataDAO();
      compositeDisposable.add(dataDAO.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(MainActivity.this::handler));
    }
public void handler(List<Data> list){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    DataAdapter dataAdapter=new DataAdapter(list);
    binding.recyclerView.setAdapter(dataAdapter);
}
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(R.id.mymenu==item.getItemId()){
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.create_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    compositeDisposable.clear();
    }
}