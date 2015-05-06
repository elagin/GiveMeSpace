package elagin.pasha.givemespace;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private List<GSDevice> devices = new ArrayList();
    private String[] paths = new String[]{"/data/sdext2", "/storage/sdcard0", "/storage/sdcard1"};
    private DeviceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        ListView lv = (ListView) findViewById(R.id.listView);
        update();

        adapter = new DeviceListAdapter(this, devices);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GSDevice item = adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this, FileManager.class);
                Bundle bundle = new Bundle();
                bundle.putString("startPath", item.name);
                intent.putExtras(bundle);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void update() {
        devices.clear();

        for (String device : paths) {
            GSDevice item = new GSDevice(device);
            devices.add(item);
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
            case R.id.main_action_update:
                update();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Обновлено.", Toast.LENGTH_LONG).show();
                return true;
        }
        return false;
    }
}
