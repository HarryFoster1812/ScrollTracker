package com.ScrollTracker.scrolltracker.FragmentControllers.Analytics;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import com.example.scrolltracker.R;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class AppEntryAdapter extends RecyclerView.Adapter<AppEntryAdapter.ViewHolder> {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView appName;
        TextView packageName;
        TextView distance;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            icon = view.findViewById(R.id.appIcon);
            appName = view.findViewById(R.id.appName);
            packageName = view.findViewById(R.id.packageName);
            distance = view.findViewById(R.id.distanceText);
        }

    }

    private List<AppEntry> localDataSet;

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public AppEntryAdapter(List<AppEntry> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.app_entry_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.icon.setImageDrawable(localDataSet.get(position).icon);
        viewHolder.appName.setText(localDataSet.get(position).appName);
        viewHolder.packageName.setText(localDataSet.get(position).packageName);
        viewHolder.distance.setText(localDataSet.get(position).distance.toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}