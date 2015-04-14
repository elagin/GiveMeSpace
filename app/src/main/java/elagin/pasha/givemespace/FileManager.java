package elagin.pasha.givemespace;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        update(false);
        adapter = new FileListAdapter(this, records);

        lv.setAdapter(adapter);
    }

    private void toDownDir(String path) {
        //prevPath = currentPath;
        currentPath = currentPath + File.separator + path;
        update(false);
    }

    private void toUpDir() {
        int cutPos = currentPath.lastIndexOf(File.separator);
        currentPath = currentPath.substring(0, cutPos);
        update(false);
    }

    private void update(boolean isCalcDir) {
        if (records.size() > 0) {
            records.clear();
        }

        pathView.setText(currentPath);

        GSFile upperDir = new GSFile();
        upperDir.name = UPPER_DIR_NAME;
        records.add(upperDir);

        try {
            File dir = new File(currentPath);
            File file[] = dir.listFiles();
            for (int i=0; i < file.length; i++) {
                GSFile item = new GSFile();
                item.name = file[i].getName();
                if(file[i].isFile()) {
                    item.size = file[i].length();
                    item.isFile = true;
                } else {
                    if(isCalcDir) {
                        item.size = getFolderSize(currentPath + File.separator + file[i].getName());
                    }
                }
                records.add(item);
            }
            if(adapter != null)
                adapter.notifyDataSetChanged();

        }
        catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                update(true);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Обновлено.", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }

    private long getFolderSize(String path) {

        long result = 0;
        String fileName = "";
        try {

            File dir = new File(path);
            File files[] = dir.listFiles();

            for (int i = 0; i < files.length; i++) {
                File file = files[i];

                fileName = file.getName();

                if (file.isFile()) {
                    result += file.length();
                } else if (file.isDirectory()) {
                    result += getFolderSize(path + File.separator + file.getName());
                } else {
                    int a = 0;
                }

            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + fileName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("FM", e.getLocalizedMessage());
        }
        return result;
    }
}
