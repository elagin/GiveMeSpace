package elagin.pasha.givemespace;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pavel on 16.04.15.
 */
public class DeviceListAdapter extends ArrayAdapter<GSDevice> {

    /// the Android Activity owning the ListView
    private final Activity activity;

    /// a list of gasoline records for display
    private final List<GSDevice> records;

    public DeviceListAdapter(Activity activity, List<GSDevice> records) {
        super(activity, R.layout.row_device_list, records);
        this.activity = activity;
        this.records = records;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // create a view for the row if it doesn't already exist
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.row_device_list, null);
        }

        // populate row widgets from record data
        GSDevice record = records.get(position);


        TextView name = (TextView) view.findViewById(R.id.device_list_name);
        name.setText(record.name);

        TextView total = (TextView) view.findViewById(R.id.device_list_total);
        total.setText(GSConverter.readableFileSize(record.totalSize));

        TextView size = (TextView) view.findViewById(R.id.device_list_free);
        size.setText(GSConverter.readableFileSize(record.freeSize));

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.device_list_progressbar);

        long used = record.totalSize - record.freeSize;
        progressBar.setProgress((int) (used / (record.totalSize / 100)));
        return view;
    }

    @Override
    public void notifyDataSetChanged() {

        // configuration may have changed - get current settings
        //getSettings();
        super.notifyDataSetChanged();
    }
}
