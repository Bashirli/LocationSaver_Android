package com.bashirli.locationsaver.DataAdapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bashirli.locationsaver.DataCollect.Data;
import com.bashirli.locationsaver.databinding.RecyclerXmlBinding;
import com.bashirli.locationsaver.view.MapsActivity;

import java.util.List;


public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataHolder> {
   List<Data> list;
   public DataAdapter(List<Data> list){
       this.list=list;
   }
   @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       RecyclerXmlBinding recyclerXmlBinding=RecyclerXmlBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

       return new DataHolder(recyclerXmlBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
holder.recyclerXmlBinding.textRecycler.setText(list.get(position).name);
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(holder.itemView.getContext(), MapsActivity.class);
        intent.putExtra("info","old");
        intent.putExtra("data",list.get(position));
        holder.itemView.getContext().startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DataHolder extends RecyclerView.ViewHolder{
private RecyclerXmlBinding recyclerXmlBinding;
        public DataHolder(@NonNull RecyclerXmlBinding recyclerXmlBinding) {
            super(recyclerXmlBinding.getRoot());
            this.recyclerXmlBinding=recyclerXmlBinding;
        }
    }
}
