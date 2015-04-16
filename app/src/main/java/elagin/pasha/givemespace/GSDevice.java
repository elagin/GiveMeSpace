package elagin.pasha.givemespace;

import android.os.StatFs;

import java.io.File;

/**
 * Created by pavel on 16.04.15.
 */
public class GSDevice {
    String name = "";
    long totalSize = 0;
    long freeSize = 0;

    public GSDevice(String pStr) {
        this.name = pStr;
        getSize(pStr);
    }

    private void getSize(String path) {
        if (path.length() > 0) {
            File dir = new File(path);
            if (dir.canRead()) {
                StatFs fs = new StatFs(dir.getAbsolutePath());
                totalSize = fs.getBlockCount() * fs.getBlockSize();
                freeSize = fs.getAvailableBlocks() * fs.getBlockSize();
            }
        }
    }
}
