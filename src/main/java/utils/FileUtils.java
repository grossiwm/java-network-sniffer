package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static String getDumpFileName() {
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        String fileName = dateFormat.format(date) + ".pcap";
        return fileName;
    }
}
