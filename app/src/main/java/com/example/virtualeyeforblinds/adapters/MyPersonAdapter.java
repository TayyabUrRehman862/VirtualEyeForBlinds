package com.example.virtualeyeforblinds.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.virtualeyeforblinds.Person;
import com.example.virtualeyeforblinds.R;
import com.example.virtualeyeforblinds.globalClass.DataStorage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MyPersonAdapter extends RecyclerView.Adapter<MyPersonAdapter.holder>{

    String BASE_URL="http://192.168.100.6:5000";
    ArrayList<Person> person;
    private Context context;
    // Other fields and methods...



    public void addPerson(Person p){
        person.add(p);
        notifyDataSetChanged();

    }


    public MyPersonAdapter(Context context,ArrayList<Person> p) {
        this.person = p;
        this.context=context;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_each_person, parent, false);
        return new holder(view);
    }
    private String getPersonImagePath(Person person, String directory) {
        String personName = person.getName();
        return directory + "/" + personName;
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        try {

            Person currentPerson = person.get(position);

            holder.name.setText(currentPerson.getName());
            if(!currentPerson.getName().equalsIgnoreCase("Tanzeela")) {
                String directory = DataStorage.getInstance().getImageDirectory(holder.itemView.getContext());

                // Assuming you have a method to get the image path for the current person
                String personImagePath = getPersonImagePath(currentPerson, directory);

                Log.e("person image path in adapter", personImagePath);

                // Load the image using Picasso
                Picasso.get()
                        .load(new File(personImagePath)) // Assuming imagePath is a valid file path
                        .into(holder.img);


                //holder.img.setImageURI(currentPerson.getImgPerson());
            }
            else {
                //holder.img.setImageURI(currentPerson.getImgPerson());
            }
        }catch (Exception e){
            e.printStackTrace();
        }




    }

    @Override
    public int getItemCount() {
        if(person!=null){
            return person.size();
        }
        else{
            return 0;
        }

    }
    public void updateList(ArrayList<Person> newData) {
        person = new ArrayList<>(newData);
        notifyDataSetChanged(); // Notify the RecyclerView that the data has changed
    }



    class holder extends RecyclerView.ViewHolder{
        TextView name;

        ImageView img;
        ImageButton deletePerson;
        public holder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.tv_for_name_person);
            img=itemView.findViewById(R.id.image_of_person_list_item);
           // deletePerson=itemView.findViewById(R.id.deletePersonRecyclerButton);
//            deletePerson.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder  builder=new AlertDialog.Builder(itemView.getContext());
//                    builder.setTitle("Delete Confirmation!!");
//                    builder.setMessage("Do you really want to delete");
//
//
//
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            int position = getAdapterPosition();
//                            person.remove(position);
//                            notifyItemRemoved(position);
//                            Toast.makeText(itemView.getContext(), "Person Deleted", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//
//                        }
//                    });
//                    //this statement is used for the alert to be clicked on the given buttons only.
//                    builder.setCancelable(false);
//                    AlertDialog d=builder.create();
//                    d.show();


//                }
  //          });


//            <LinearLayout
//            android:layout_weight="3"
//            android:gravity="end"
//            android:layout_width="1dp"
//            android:layout_height="100dp">
//        <ImageButton
//            android:id="@+id/deletePersonRecyclerButton"
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
