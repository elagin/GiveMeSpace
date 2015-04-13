package elagin.pasha.givemespace;

import android.content.Intent;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    MessageFormat formatGb = new MessageFormat("{0, number,#.##} GB", Locale.US);
    MessageFormat formatMb = new MessageFormat("{0, number,##.#} MB", Locale.US);

    private ArrayList<String> devices = new ArrayList<String>();

    private String[] paths = new String[]{"/data/sdext2", "/storage/sdcard0", "/storage/sdcard1"};

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        ListView lv = (ListView) findViewById(R.id.listView);
        update();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devices);
        lv.setAdapter(adapter);
    }

    private String getSize(String pStr) {
        String res;
        if (pStr.length() > 0) {
            String size = formatGb.format(new Object[]{0});
            int percent = 0;

            File dir = new File(pStr);
            if (dir.canRead()) {
                StatFs fs = new StatFs(dir.getAbsolutePath());
                if (fs.getAvailableBlocks() > 0) {
                    size = formatGb.format(new Object[]{(float) (fs.getAvailableBlocks()) * fs.getBlockSize() / (1 << 30)});
                    percent = (int) (fs.getAvailableBlocks() * 100 / fs.getBlockCount());
                }
                String text = getString(R.string.free, size);
                return text;
            } else
                return "i can't read path";
        } else {
            return "Invalid path";
        }
    }

    private void update() {
        if (devices.size() > 0) {
            devices.clear();
        }
        for (String device : paths) {
            devices.add(device + "\t\t" + getSize(device));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                update();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Обновлено.", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_give:
                Intent i = new Intent(this, FileManager.class);
                this.startActivity(i);
                return true;
        }
        return false;
    }
}
