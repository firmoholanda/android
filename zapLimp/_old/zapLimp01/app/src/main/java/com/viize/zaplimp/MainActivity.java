package com.viize.zaplimp;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private static final String ERROR = "error";

    public void onButtonTap(View v) {

        // get all mounted drives
        File[] SDFiles = ContextCompat.getExternalFilesDirs(this,null);

        // source dir
        File pathWhatsMedia = new File( SDFiles[0].toString().replace("/Android/data/com.viize.zaplimp/files", "/WhatsApp/MediaCopy") );
        //System.out.println(pathWhatsMedia);

        // target dir
        File pathWhatsMediaSD = new File( SDFiles[1].toString() );
        //System.out.println(pathWhatsMediaSD);


        // move files
        //pathWhatsMedia.renameTo(pathWhatsMediaSD);



        // copy files
        try {
            copyDirectory(pathWhatsMedia, pathWhatsMediaSD);
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }


        // delete original files
        if (pathWhatsMedia.isDirectory())
        {
            String[] children = pathWhatsMedia.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(pathWhatsMedia, children[i]).delete();
            }
        }


        Toast myToast = Toast.makeText(getApplicationContext(), "limpo!", Toast.LENGTH_LONG);
        myToast.show();


        /*
        try {
            String filename = "abc.txt";
            File myFile = new File(pathWhatsMediaSD.toString(), filename);
            if (!myFile.exists())
                myFile.createNewFile();
            FileOutputStream fos;
            byte[] data = filename.getBytes();
            try {
                fos = new FileOutputStream(myFile);
                fos.write(data);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        */




        //File[] SDFiles = ContextCompat.getExternalFilesDirs(this,null);
        //System.out.println( SDFiles.length );
        //System.out.println( SDFiles[0] );
        //System.out.println( SDFiles[1] );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ads
        //MobileAds.initialize(getApplicationContext(), "ca-app-pub-8172094365756128~2881970294");
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        TextView tv1 = (TextView) findViewById(R.id.textView1);
        TextView tv2 = (TextView) findViewById(R.id.textView2);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        TextView tv4 = (TextView) findViewById(R.id.textView4);

        tv1.setText(Boolean.toString(externalMemoryAvailable()));
        tv2.setText(getAvailableInternalMemorySize());
        tv3.setText(getAvailableExternalMemorySize());

        File[] SDFiles = ContextCompat.getExternalFilesDirs(this,null);
        File pathWhatsMedia = new File(SDFiles[0].toString().replace("/Android/data/com.viize.zaplimp/files", "/WhatsApp/Media"));
        tv4.setText( bytesToHuman(getFileSize(pathWhatsMedia)) );

    }

    //----------------------------------------------------------------------------------------------

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public  String getAvailableInternalMemorySize() {
        File[] SDFiles = ContextCompat.getExternalFilesDirs(this,null);
        File path = SDFiles[0];

        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return bytesToHuman(availableBlocks * blockSize);
    }

    public  String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        //File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(this,null);
        //File path = new File( externalStorageFiles[0].toString().replace("/Android/data/com.viize.zaplimp/files","") );

        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        //return formatSize(totalBlocks * blockSize);
        return bytesToHuman(totalBlocks * blockSize);
    }

    public  String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File[] SDFiles = ContextCompat.getExternalFilesDirs(this,null);
            File path = SDFiles[1];

            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return bytesToHuman(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            //File path = Environment.getExternalStorageDirectory();
            File path =  new File( getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).getAbsolutePath() );
            //File[] externalStorageFiles= ContextCompat.getExternalFilesDirs(this,null);
            //File path = new File( externalStorageFiles[1].toString().replace("/Android/data/com.viize.zaplimp/files","") );

            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            //return formatSize(totalBlocks * blockSize);
            return bytesToHuman(totalBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static long getFileSize(final File file)
    {
        if(file==null||!file.exists())
            return 0;
        if(!file.isDirectory())
            return file.length();
        final List<File> dirs=new LinkedList<File>();
        dirs.add(file);
        long result=0;
        while(!dirs.isEmpty())
        {
            final File dir=dirs.remove(0);
            if(!dir.exists())
                continue;
            final File[] listFiles=dir.listFiles();
            if(listFiles==null||listFiles.length==0)
                continue;
            for(final File child : listFiles)
            {
                result+=child.length();
                if(child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }

    //----------------------------------------------------------------------------------------------

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }
    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }

    //----------------------------------------------------------------------------------------------

    public void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }

    //----------------------------------------------------------------------------------------------



}
