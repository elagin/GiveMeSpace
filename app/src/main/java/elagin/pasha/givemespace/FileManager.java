package elagin.pasha.givemespace;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileManager extends ActionBarActivity {


    private static final String TAG = FileManager.class.getSimpleName();

    public static final String UPPER_DIR_NAME = "..";
    public static final String ROOT_DIR_NAME = "/";

    private String currentPath = "/storage/sdcard0";

    private TextView pathView;
    private FileListAdapter adapter;

    private List<GSFile> records = new ArrayList();

    private long folderVolume;

    private android.support.v7.app.ActionBar actionBar;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        Bundle b = getIntent().getExtras();
        currentPath = b.getString("startPath");

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pathView = (TextView) findViewById(R.id.path);

        final ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GSFile item = adapter.getItem(position);

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
        if(currentPath.equals(ROOT_DIR_NAME))
            currentPath = File.separator + path;
        else
            currentPath = currentPath + File.separator + path;
        update(false);
    }

    private void toUpDir() {
        int cutPos = currentPath.lastIndexOf(File.separator);
        currentPath = currentPath.substring(0, cutPos);
        if(currentPath.length() == 0)
            currentPath = ROOT_DIR_NAME;
        update(false);
    }

    private void update(boolean isCalcDir) {
        if (records.size() > 0) {
            records.clear();
        }

        if(mMenu != null) {
            MenuItem menuItem = mMenu.findItem(R.id.action_update);
            menuItem.setVisible(!currentPath.equals(ROOT_DIR_NAME));
        }
        folderVolume = 0;
        setTitle();
        pathView.setText(currentPath);

        GSFile upperDir = new GSFile();
        upperDir.name = UPPER_DIR_NAME;
        records.add(upperDir);

        try {
            File dir = new File(currentPath);
            if( dir != null ) {
                File files[] = dir.listFiles();
                if(files != null){
                    for (int i = 0; i < files.length; i++) {
                        GSFile item = new GSFile();
                        item.name = files[i].getName();
                        if (files[i].isFile()) {
                            item.size = files[i].length();
                            item.isFile = true;
                        } else {
                            if (isCalcDir) {
                                item.size = getFolderSize(currentPath + File.separator + files[i].getName());
                            }
                        }
                        folderVolume += item.size;
                        records.add(item);
                    }
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG + "update", "Invalid new File");
            }
        }
        catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        //Collections.sort(records, TypeeAscComparator);
        Collections.sort(records, SizeDescComparator);
    }

    public static Comparator<GSFile> SizeAscComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if(file1.size > file2.size)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> SizeDescComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if(file1.size < file2.size)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> TypeeAscComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if(file1.isFile)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> TypeDescComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if(file2.isFile)
                return 1;
            else
                return -1;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_manager, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                update(true);
                adapter.notifyDataSetChanged();
                setTitle();
                Toast.makeText(this, this.getString(R.string.succsess), Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }

    private void setTitle() {
        String format = this.getString(R.string.title_activity_file_manager);
        String title = String.format(format, GSConverter.readableFileSize(folderVolume));
        actionBar.setTitle(title);
    }

    private long getFolderSize(String path) {
        long result = 0;
        String fileName = "";
        try {

            File dir = new File(path);
            if(dir != null ) {
                File files[] = dir.listFiles();
                if(files != null) {
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
            } else {
                Log.e(TAG + "getFolderSize", "Invalid new File");
            }
        }
        catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + fileName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("FM", e.getLocalizedMessage());
        }
        return result;
    }
}
