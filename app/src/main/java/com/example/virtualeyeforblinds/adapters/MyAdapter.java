package com.example.virtualeyeforblinds.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualeyeforblinds.Place;
import com.example.virtualeyeforblinds.PlacesActivity;
import com.example.virtualeyeforblinds.R;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.example.virtualeyeforblinds.models.Direction;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.holder> {


    private static ArrayList<Place> place;

    public void addPlace(Place p){
        place.add(p);
        notifyDataSetChanged();
    }



   public MyAdapter(ArrayList<Place> place){
        this.place=place;


    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.list_item_each,parent,false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {

        Place currentPlace = place.get(position);

        holder.name.setText("Name: "+currentPlace.getName());
        holder.type.setText("Type: "+currentPlace.getTypename());
        holder.floor.setText("Floor: "+currentPlace.getFloorname());

        holder.doorDirection.setText("Door Direction: "+ currentPlace.getDoorDirectionName());





        holder.img.setImageURI(currentPlace.getImg());



    }

    @Override
    public int getItemCount() {
        return place.size();
    }

    public void updateList(ArrayList<Place> newData) {
        place = new ArrayList<>(newData);
        notifyDataSetChanged(); // Notify the RecyclerView that the data has changed
    }


    class holder extends RecyclerView.ViewHolder{
        TextView name,floor,type,doorDirection;
        ImageView img;
        //ImageButton deletePlace;

        public holder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tv_for_name);
            floor=itemView.findViewById(R.id.tv_for_floor);
            type=itemView.findViewById(R.id.tv_for_type);
            doorDirection=itemView.findViewById(R.id.tv_for_doorDirection);
            img=itemView.findViewById(R.id.image_of_place_list_item);
            //deletePlace=itemView.findViewById(R.id.deletePlaceRecycler);
//            deletePlace.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position=getAdapterPosition();
//                    place.remove(position);
//
//                    // Notify the adapter that the data has changed
//                    notifyItemRemoved(position);
//                }
//            });
//            <LinearLayout
//            android:clickable="true"
//
//            android:layout_weight="3"
//            android:gravity="end"
//            android:layout_width="1dp"
//            android:layout_height="100dp">
//        <ImageButton
//            android:id="@+id/deletePlaceRecycler"
//            android:src="@drawable/baseline_delete_forever_24"
//            android:backgroundTint="@color/blue_p"
//            android:background="@drawable/simple_borders_4"
//            android:layout_marginTop="30dp"
//            android:layout_marginRight="30dp"
//            android:layout_width="40dp"
//            android:layout_height="40dp"></ImageButton>
//    </LinearLayout>
        }
    }
}
