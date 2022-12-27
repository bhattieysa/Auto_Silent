package com.example.myfyp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventListAdapter  extends RecyclerView.Adapter<EventListAdapter.EventListViewHolder>{

    Context context;
    //ARRAYLIST OF EVENTS
    private ArrayList<AutoSilenceEvent> EVENTS;
    //db HELPER CLASS FOR EVENTS
    private EventDBHelper dbHelper;
    //RECYCLERVIRE CLICK CALLBACKS
    private EventListAdapter.RecyclerViewClickCallbacks recyclerViewClickCallbacks = null;


    //CONSTRUCTOR
    EventListAdapter(ArrayList<AutoSilenceEvent> locations,Context context) {
        this.EVENTS = locations;
        this.context = context;
    }

    //ADDING EVENTS IN THE LIST WHEN LOADED FROM THE DB
    void setEvents(ArrayList<AutoSilenceEvent> locations) {
        this.EVENTS = locations;
        this.notifyDataSetChanged();
    }

    //CALLED WHEN AN ITEM IS DELETED
    void removeLocation(int position) {
        EVENTS.remove(position);
        this.notifyItemRemoved(position);
    }

    //create the view for the row layout
    @NonNull
    @Override
    public EventListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListViewHolder holder, int position) {
        //showing data into views of the row layout
        dbHelper = new EventDBHelper(context);
        holder.listName.setText(EVENTS.get(position).getName());
        holder.start.setText(EVENTS.get(position).getStart());
        holder.end.setText(EVENTS.get(position).getEnd());


    }


    //returns the size of the list whatever it is
    @Override
    public int getItemCount() {
        if (EVENTS == null) return 0;
        return EVENTS.size();
    }



    AutoSilenceEvent getItem(int position) {
        return EVENTS.get(position);
    }


    //CALLS WHEN ITEM IS SWIPED AND DELETED
    public void deleteItem(int position) {
        EVENTS.remove(position);
        notifyItemRemoved(position);
        dbHelper.remove(position);
        this.notifyDataSetChanged();
        Log.d("TAGpos", "deleteItem: "+position);

    }

    public void setRecyclerViewClickCallbacks(RecyclerViewClickCallbacks recyclerViewClickCallbacks) {
        this.recyclerViewClickCallbacks = recyclerViewClickCallbacks;
    }

    public ItemTouchHelper.Callback getSwipeHelper() {
        return new EventListAdapter.ItemSwipeHelper();
    }

    interface RecyclerViewClickCallbacks {
        void onItemClick(View v, int position);

        boolean onItemLongClick(View v, int position);

        void onItemSwipe(int position);
    }

    //viewHolder Class where row layout views are initialize
    public class EventListViewHolder extends RecyclerView.ViewHolder {
        TextView listName, start, end;
        public EventListViewHolder(@NonNull View itemView) {
            super(itemView);
            listName = itemView.findViewById(R.id.listItemName);
            start = itemView.findViewById(R.id.starttimetxt);
            end = itemView.findViewById(R.id.endtimetxt);


        }




    }

    class ItemSwipeHelper extends ItemTouchHelper.SimpleCallback {

        ItemSwipeHelper() {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            //Ignore
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            recyclerViewClickCallbacks.onItemSwipe(viewHolder.getAdapterPosition());
        }
    }
}
