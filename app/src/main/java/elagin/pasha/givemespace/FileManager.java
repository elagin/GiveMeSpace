package elagin.pasha.givemespace;

import android.app.Dialog;
import android.os.Bundle;
import android.os.StatFs;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class FileManager extends ActionBarActivity implements ConfirmationDialog.Listener {

    private static final String TAG = FileManager.class.getSimpleName();

    public static final String UPPER_DIR_NAME = "..";
    public static final String ROOT_DIR_NAME = "/";

    protected static final int DIALOG_CONFIRM_DELETE_ID = 1;

    private String currentPath = "/storage/sdcard0";

    private TextView pathView;
    private TextView infoView;
    private FileListAdapter adapter;

    private List<GSFile> records = new ArrayList();
    private List<GSFile> foldersList = new ArrayList();
    private List<GSFile> filesList = new ArrayList();

    private long folderVolume;
    private long avaibleSize;

    private android.support.v7.app.ActionBar actionBar;
    private Menu mMenu;

    /// the currently selected row from the list of records
    private int selectedRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);

        Bundle b = getIntent().getExtras();
        currentPath = b.getString("startPath");

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pathView = (TextView) findViewById(R.id.path);
        infoView = (TextView) findViewById(R.id.info);

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

        registerForContextMenu(lv);

        update(false);
        adapter = new FileListAdapter(this, records);

        lv.setAdapter(adapter);
    }

    private void toDownDir(String path) {
        if (currentPath.equals(ROOT_DIR_NAME))
            currentPath = File.separator + path;
        else
            currentPath = currentPath + File.separator + path;
        update(false);
    }

    private void toUpDir() {
        int cutPos = currentPath.lastIndexOf(File.separator);
        currentPath = currentPath.substring(0, cutPos);
        if (currentPath.length() == 0)
            currentPath = ROOT_DIR_NAME;
        update(false);
    }

    private void update(boolean isCalcDir) {
        records.clear();
        foldersList.clear();
        filesList.clear();

        if (mMenu != null) {
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
            if (dir != null) {
                File files[] = dir.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        GSFile item = new GSFile();
                        item.name = files[i].getName();
                        if (files[i].isFile()) {
                            item.size = files[i].length();
                            item.isFile = true;
                            filesList.add(item);
                        } else {
                            if (isCalcDir) {
                                item.size = getFolderSize(currentPath + File.separator + files[i].getName());
                            }
                            foldersList.add(item);
                        }
                        folderVolume += item.size;
                    }
                }
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            } else {
                Log.e(TAG + "update", "Invalid new File");
            }
            Collections.sort(foldersList, SizeDescComparator);
            Collections.sort(filesList, SizeDescComparator);

            records.addAll(foldersList);
            records.addAll(filesList);

            if (isCalcDir) {
                StatFs fs = new StatFs(dir.getAbsolutePath());
                long size = fs.getBlockSize();
                long available = fs.getAvailableBlocks();
                avaibleSize = available * size;
                setInfo();
            } else {
                infoView.setText("");
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static Comparator<GSFile> SizeAscComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if (file1.size > file2.size)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> SizeDescComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if (file1.size < file2.size)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> TypeeAscComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if (file1.isFile)
                return 1;
            else
                return -1;
        }
    };

    public static Comparator<GSFile> TypeDescComparator = new Comparator<GSFile>() {

        public int compare(GSFile app1, GSFile app2) {

            GSFile file1 = app1;
            GSFile file2 = app2;

            if (file2.isFile)
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
        String formatVolume = this.getString(R.string.title_activity_file_manager);
        String title = String.format(formatVolume, GSConverter.readableFileSize(folderVolume));
        actionBar.setTitle(title);
    }

    private void setInfo() {
        String formatVolume = this.getString(R.string.title_activity_file_manager);
        String formatAvaible = this.getString(R.string.title_activity_avaible_size);
        String title = String.format(formatVolume, GSConverter.readableFileSize(folderVolume)) + " / " + String.format(formatAvaible, GSConverter.readableFileSize(avaibleSize));
        infoView.setText(title);
    }

    private long getFolderSize(String path) {
        long result = 0;
        String fileName = "";
        try {

            File dir = new File(path);
            if (dir != null) {
                File files[] = dir.listFiles();
                if (files != null) {
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
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + fileName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("FM", e.getLocalizedMessage());
        }
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // create the menu
        getMenuInflater().inflate(R.menu.filemanager_context_menu, menu);

        // get index of currently selected row
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        selectedRow = (int) info.id;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // get index of currently selected row
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selectedRow = (int) info.id;
        switch (item.getItemId()) {
            case R.id.itemDelete:
                showDialog(DIALOG_CONFIRM_DELETE_ID);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        String title;
        String message;

        switch (id) {
            case DIALOG_CONFIRM_DELETE_ID:
                title = getString(R.string.title_confirm_delete_dialog);
                message = getString(R.string.message_confirm_delete_dialog);
                dialog = ConfirmationDialog.create(this, this, id, title, message);
                break;
            default:
                dialog = null;
        }

        return dialog;
    }

    @Override
    public void onConfirmationDialogResponse(int id, boolean confirmed) {

        removeDialog(id);

        if (!confirmed) return;

        switch (id) {
            case DIALOG_CONFIRM_DELETE_ID:
                deleteFile();
                break;
            default:
        }
    }

    protected void deleteFile() {
        GSFile record = records.get(selectedRow);
        String fullPath = currentPath + File.separator + record.name;
        File file = new File(fullPath);
        boolean result = true;

        if (file.isDirectory()) {
            result = clearFolder(fullPath);
        }
        if( result ) {
            result = file.delete();
        }
        if (result) {
            records.remove(selectedRow);
            update(true);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, this.getString(R.string.succsess), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, this.getString(R.string.toast_delete_failed), Toast.LENGTH_LONG).show();
        }
    }

    private boolean clearFolder(String path) {
        boolean result = true;
        String fileName = "";
        try {

            File dir = new File(path);
            if (dir != null) {
                File files[] = dir.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        File file = files[i];
                        fileName = file.getName();
                        if (file.isFile()) {
                            result = file.delete();
                            if (result) {
                                Toast.makeText(this, this.getString(R.string.succsess), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, this.getString(R.string.toast_delete_failed) + path + File.separator + fileName, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            result = clearFolder(path + File.separator + file.getName());
                        }
                    }
                }
            } else {
                Log.e(TAG + "clearFolder", "error delete file");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + fileName + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("FM", e.getLocalizedMessage());
        }
        return result;
    }
}
