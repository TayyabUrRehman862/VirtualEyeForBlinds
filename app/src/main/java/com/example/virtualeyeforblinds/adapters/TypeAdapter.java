package com.example.virtualeyeforblinds.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualeyeforblinds.PlacesActivity;
import com.example.virtualeyeforblinds.R;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.SimpleResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TypeAdapter extends RecyclerView.Adapter<TypeAdapter.TypeViewHolder> {

    private ArrayList<String> originalTypeList;

    private ArrayList<String> typeList;

    public TypeAdapter(ArrayList<String> typeList) {
        this.typeList = typeList;
        this.originalTypeList = new ArrayList<>(typeList);
    }
    public void updateData(ArrayList<String> newFloorList) {
        if (typeList != null) {
            typeList.clear(); // Clear the current dataset if it exists
            typeList.addAll(newFloorList);
            originalTypeList.clear();
            originalTypeList.addAll(typeList);
        } else {
            typeList = newFloorList;
            originalTypeList = typeList;
        }
        notifyDataSetChanged(); // Notify adapter that dataset has changed
    }
    public void setCurrentQuery(String query) {

        filter(query.toLowerCase()); // Apply filtering based on the new query
    }
    public void addFloor(String newFloorName, String currentQuery) {
        int insertPosition = typeList.size(); // Position where the item will be inserted

        typeList.add(newFloorName);
        notifyItemInserted(insertPosition);

        // If a search query exists, reapply the filter
        if (!currentQuery.isEmpty()) {
            filter(currentQuery);
        }
    }

    public void filter(String query) {
        typeList.clear();
        if (query.isEmpty()) {
            typeList.addAll(originalTypeList);
        } else {
            query = query.toLowerCase();
            for (String floor : originalTypeList) {
                if (floor.toLowerCase().contains(query)) {
                    typeList.add(floor);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that dataset has changed
    }

    public boolean isItemInList(String query) {
        if (typeList != null && !typeList.isEmpty()) {
            for (String item : typeList) {
                if (item.equalsIgnoreCase(query)) {
                    return true; // Found a match
                }
            }
        }
        return false; // No match found
    }


    @NonNull
    @Override
    public TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.type_item_layout, parent, false);
        return new TypeViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull TypeViewHolder holder, int position) {
        String tName = typeList.get(position);

        holder.typeNameTextView.setText(tName);



        //holder.typeNameTextView.setText(tName);
        holder.typeNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (view.getContext() instanceof PlacesActivity) {
                        ((PlacesActivity) view.getContext()).onTypeSelected(tName);
                    }

                }catch (Exception e){
                    Log.e("Exception for type",e.getMessage());
                }


            }
        });


        holder.deleteIconType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=0;
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    for(int i = 0; i < DataStorage.getInstance().getTypesArrayList().size(); i++){
                        if(typeList.get(adapterPosition).equalsIgnoreCase(DataStorage.getInstance().getTypesArrayList().get(i).name)){
                            id=DataStorage.getInstance().getTypesArrayList().get(i).id;
                            break;
                        }
                    }
                    WebApi api= RetrofitClient.getInstance().getMyApi();
                    Log.e("id to be send for deletion",id+"");
                    api.deleteTypeById(id).enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                            if(response.isSuccessful()){
                                DataStorage.getInstance().getAllTypes();
                                Log.v("response successful of deleting floor",response.body()+"");
                            }
                            else{
                                Log.v("response was unsuccessful of deleting floor",response.body()+"");
                            }
                        }

                        @Override
                        public void onFailure(Call<SimpleResponse> call, Throwable t) {
                            Log.v("Api failure of deleting floor",t.getMessage());
                        }
                    });





                    typeList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public static class TypeViewHolder extends RecyclerView.ViewHolder {
        TextView typeNameTextView;
        ImageView deleteIconType;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            typeNameTextView = itemView.findViewById(R.id.typeNameTextView);
            deleteIconType = itemView.findViewById(R.id.deleteIconType);
        }
    }
}


