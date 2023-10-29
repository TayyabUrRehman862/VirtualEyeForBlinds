package com.example.virtualeyeforblinds;

import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyPersonAdapter extends RecyclerView.Adapter<MyPersonAdapter.holder>{
    ArrayList<Person> person;

    public void addPerson(Person p){
        person.add(p);
        notifyDataSetChanged();

    }


    public MyPersonAdapter(ArrayList<Person> p) {
        this.person = p;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_each_person, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        Person currentPerson = person.get(position);

        holder.name.setText(currentPerson.getName());
        //holder.img.setImageURI(currentPerson.getImgPerson());
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
