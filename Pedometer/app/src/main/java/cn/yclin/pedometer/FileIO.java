package cn.yclin.pedometer;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class FileIO {
    private static final String TAG = "ChartViewActivity";
    private String filePath;
    private String fileName;
    private File file;
    private BufferedWriter bw;
    private BufferedReader br;

    public FileIO(String filePath){
        this.filePath = filePath;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            File dir = new File(filePath);
            if(!dir.exists()){
                Boolean res = dir.mkdir();
                if(!res)
                    Log.d(TAG,"create direction failed");
            }
        }
        else
            Log.d(TAG,"ExternalStorageState: "+Environment.getExternalStorageState());
    }

    public void writeFile(long time,float a, float b,float c){
        try{
            bw.write(String.format(Locale.getDefault(),"%d,%.2f,%.2f,%.2f",time,a,b,c));
            bw.newLine();
        }catch (Exception e){
            Log.d(TAG,Log.getStackTraceString(e));
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void closeFile(){
        try {
            if(bw != null) {
                bw.close();
            }
            if(br != null) {
                br.close();
            }
        }catch (Exception e){
            Log.d(TAG,Log.getStackTraceString(e));
        }
    }

    public Boolean setFileToWrite(String fileName) throws IOException{
        this.fileName = fileName;
        Boolean res = true;
        file = new File(filePath+File.separator+fileName);
        if(!file.exists()) {
            res = file.createNewFile();
        }
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
        }catch (Exception e){
            Log.d(TAG,Log.getStackTraceString(e));
            res = false;
        }
        return res;
    }

    public Boolean setFileToRead(String fileName) throws IOException{
        this.fileName = fileName;
        Boolean res = true;
        file = new File(filePath+File.separator+fileName);
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        }catch (Exception e){
            Log.d(TAG,Log.getStackTraceString(e));
            res = false;
        }
        return res;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }
}
