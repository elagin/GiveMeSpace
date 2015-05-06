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

                long size = fs.getBlockSize();
                long total = fs.getBlockCount();
                long available = fs.getAvailableBlocks();
                totalSize = total * size;
                freeSize = available * size;
            }
        }
    }
}
