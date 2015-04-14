package elagin.pasha.givemespace;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by pavel on 14.04.15.
 */
public class FileListAdapter extends ArrayAdapter<GSFile> {

    /// the Android Activity owning the ListView
    private final Activity activity;

    /// a list of gasoline records for display
    private final List<GSFile> records;


    public FileListAdapter(Activity activity, List<GSFile> records) {
        super(activity, R.layout.row_file_list, records);
        this.activity = activity;
        this.records = records;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        // create a view for the row if it doesn't already exist
        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.row_file_list, null);
        }

        // populate row widgets from record data
        GSFile record = records.get(position);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        if(record.isFile) {
            image.setImageResource(R.drawable.ic_menu_paste_holo_light);
        } else if (record.name.equals(FileManager.UPPER_DIR_NAME)) {
            image.setImageResource(R.drawable.ic_menu_back);
        } else {
            image.setImageResource(R.drawable.ic_menu_archive);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(/*Const.timeFormat.format(record.time.getTime()) + " - " + */record.name);

        TextView size = (TextView) view.findViewById(R.id.size);
        size.setText(readableFileSize(record.size));
        return view;
    }

    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public void notifyDataSetChanged() {

        // configuration may have changed - get current settings
        //getSettings();
        super.notifyDataSetChanged();
    }
}
