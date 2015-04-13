package elagin.pasha.givemespace;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


public class FileManager extends ActionBarActivity {

    private String path = "/storage/sdcard0";

    private ArrayList<String> fileList = new ArrayList<String>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView lv = (ListView) findViewById(R.id.listView);
        update();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        lv.setAdapter(adapter);
    }

    private void update() {
        if (fileList.size() > 0) {
            fileList.clear();
        }
        File dir = new File(path);
        File file[] = dir.listFiles();
        for (int i=0; i < file.length; i++) {
            String name = file[i].getName();
            String size = "dir";

            if(file[i].isFile()) {
                size = Long.toString(file[i].length());
            }
            fileList.add(name + " : " + size);
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
