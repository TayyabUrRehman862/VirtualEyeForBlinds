package com.example.virtualeyeforblinds.adapters;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualeyeforblinds.MainActivity;
import com.example.virtualeyeforblinds.PlacesActivity;
import com.example.virtualeyeforblinds.R;
import com.example.virtualeyeforblinds.api.RetrofitClient;
import com.example.virtualeyeforblinds.api.WebApi;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.SimpleResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.FloorViewHolder> {

    private ArrayList<String> originalFloorList;

    private ArrayList<String> floorList;

    public FloorAdapter(ArrayList<String> floorList) {
        this.floorList = floorList;
        this.originalFloorList = new ArrayList<>(floorList);
    }
    public void updateData(ArrayList<String> newFloorList) {
        if (floorList != null) {
            floorList.clear(); // Clear the current dataset if it exists
            floorList.addAll(newFloorList);
             originalFloorList.clear();
             originalFloorList.addAll(floorList);
        } else {
            floorList = newFloorList;
            originalFloorList=floorList;
        }
        notifyDataSetChanged(); // Notify adapter that dataset has changed
    }
    public void setCurrentQuery(String query) {

        filter(query.toLowerCase()); // Apply filtering based on the new query
    }
    public void addFloor(String newFloorName, String currentQuery) {
        int insertPosition = floorList.size(); // Position where the item will be inserted

        floorList.add(newFloorName);
        notifyItemInserted(insertPosition);

        // If a search query exists, reapply the filter
        if (!currentQuery.isEmpty()) {
            filter(currentQuery);
        }
    }

    public void filter(String query) {
        floorList.clear();
        if (query.isEmpty()) {
            floorList.addAll(originalFloorList);
        } else {
            query = query.toLowerCase();
            for (String floor : originalFloorList) {
                if (floor.toLowerCase().contains(query)) {
                    floorList.add(floor);
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter that dataset has changed
    }
    public boolean isItemInList(String query) {
        if (floorList != null && !floorList.isEmpty()) {
            for (String item : floorList) {
                if (item.equalsIgnoreCase(query)) {
                    return true; // Found a match
                }
            }
        }
        return false; // No match found
    }




    @NonNull
    @Override
    public FloorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.floor_item_layout, parent, false);
        return new FloorViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull FloorViewHolder holder, int position) {
        String floorName = floorList.get(position);
        holder.floorNameTextView.setText(floorName);
        holder.floorNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (view.getContext() instanceof PlacesActivity) {
                        ((PlacesActivity) view.getContext()).onFloorSelected(floorName);
                    }

                }catch (Exception e){
                    Log.e("Exception for floor",e.getMessage());
                }


            }
        });


        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id=0;
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {

                    for(int i = 0; i < DataStorage.getInstance().getFloorsArrayList().size(); i++){
                        if(floorList.get(adapterPosition).equalsIgnoreCase(DataStorage.getInstance().getFloorsArrayList().get(i).name)){
                            id=DataStorage.getInstance().getFloorsArrayList().get(i).id;
                            break;
                        }
                    }
                    WebApi api= RetrofitClient.getInstance().getMyApi();
                    Log.e("id to be send for deletion",id+"");
                    api.deleteFloorById(id).enqueue(new Callback<SimpleResponse>() {
                        @Override
                        public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                            if(response.isSuccessful()){
                                DataStorage.getInstance().getAllFloors();
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





                    floorList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return floorList.size();
    }

    public static class FloorViewHolder extends RecyclerView.ViewHolder {
        TextView floorNameTextView;
        ImageView deleteIcon;

        public FloorViewHolder(@NonNull View itemView) {
            super(itemView);
            floorNameTextView = itemView.findViewById(R.id.floorNameTextView);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}

