package com.example.traceusdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetViewHolder> {

    ArrayList<Meet> mMeetings;

    //Initialize Database object
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    //Initialize Event Listener
    private ChildEventListener mChildEventListener;

    public MeetingAdapter(Activity activity) {
        //Call getFbReference from FirebaseUtil class
        FirebaseUtil.openFbReference("meet", activity.getParent());

        //Initialize an instance of the database through FirebaseUtil class
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mChildEventListener = new ChildEventListener() {
            //the first time the activity is loaded, every item that's in the database will trigger this event
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Meet meet = snapshot.getValue(Meet.class); //getValue() serializes data
                Log.d("Met with: ", meet.getMet_user_uid());
                //ua.setPhone(snapshot.getKey());
                mMeetings.add(meet);
                notifyItemInserted(mMeetings.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);
        mMeetings = FirebaseUtil.mMeetings; //retrieve list of meetings to populate RecyclerView
    }

    //onCreateViewHolder called when RecyclerView needs a new ViewHolder
    @NonNull
    @Override
    public MeetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false);
        return new MeetViewHolder(itemView);
    }

    //onBindViewHolder called to display the data
    @Override
    public void onBindViewHolder(@NonNull MeetViewHolder holder, int position) {
        Meet meet = mMeetings.get(position);
        holder.bind(meet);
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public class MeetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Initialize variables for TextViews
        TextView tvPhone;
        TextView tvCovidStatus;
        TextView tvLocation;
        TextView tvDateTime;

        //Retrieve TextViews
        public MeetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPhone = (TextView) itemView.findViewById(R.id.tvPhone);
            tvCovidStatus = (TextView) itemView.findViewById(R.id.tvCovidStatus);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            //itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Meet meet) {
            tvPhone.setText(meet.getMet_user_uid()); //change this to other user's phone number, make method in meet object
            tvCovidStatus.setText(meet.getMeet_status()); //verify that this is the other user's status, not mine
            tvLocation.setText("Long: " + meet.getMeet_longitude() + "\n" + "Lat: " + meet.getMeet_latitude());
            tvDateTime.setText(meet.getMeet_date_time());
        }

        @Override
        public void onClick(View v) {
//            int position = getAdapterPosition();
//            Log.d("Click", String.valueOf(position));
//
//            //Get selected account in RecyclerView, call register user activity
//            //Pass User object to intent as extra
//            Meet selectedMeeting = mMeetings.get(position);
//            Intent intent = new Intent(v.getContext(), UpdateUserActivity.class);
//            intent.putExtra("Meet", selectedMeeting);
//            v.getContext().startActivity(intent);
        }
    }
}
