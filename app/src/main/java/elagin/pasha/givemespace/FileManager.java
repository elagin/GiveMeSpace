package elagin.pasha.givemespace;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FileManager extends ActionBarActivity {

    private final String UPPER_DIR_NAME = "..";

    private String currentPath = "/storage/sdcard0";

    private TextView pathView;
    private FileListAdapter adapter;

    private List<GSFile> records = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pathView = (TextView) findViewById(R.id.path);

        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GSFile item = (GSFile) adapter.getItem(position);

                if (item.name.equals(UPPER_DIR_NAME)) {
                    toUpDir();
                    return;
                }

                if (!item.isFile) {
                    toDownDir(item.name);
                    return;
                }
            }
        });

        update();
        adapter = new FileListAdapter(this, records);

        lv.setAdapter(adapter);
    }

    private void toDownDir(String path) {
        //prevPath = currentPath;
        currentPath = currentPath + File.separator + path;
        update();
    }

    private void toUpDir() {
        int cutPos = currentPath.lastIndexOf(File.separator);
        currentPath = currentPath.substring(0, cutPos);
        update();
    }

    private void update() {
        if (records.size() > 0) {
            records.clear();
        }

        pathView.setText(currentPath);

        GSFile upperDir = new GSFile();
        upperDir.name = UPPER_DIR_NAME;
        records.add(upperDir);

        File dir = new File(currentPath);
        File file[] = dir.listFiles();
        for (int i=0; i < file.length; i++) {
            GSFile item = new GSFile();
            item.name = file[i].getName();
            if(file[i].isFile()) {
                item.size = Long.toString(file[i].length());
                item.isFile = true;
            }
            records.add(item);
        }
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
