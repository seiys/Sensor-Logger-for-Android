package com.example.sensor_output;

import android.content.Context;
import android.os.Environment;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CSVWriter {
    private static FileWriter f=null;
    private static PrintWriter p=null;
    private static Context myContext;

    CSVWriter(Context ctx) {
        myContext = ctx;
    }

    public static void init(String fname, String header){
        try{
            f = new FileWriter(myContext.getExternalFilesDir("").getPath()+"/"+fname+".csv", false);
            p = new PrintWriter(new BufferedWriter(f));

            // Header
            writeLine(header);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void writeLine(String contents){
        if(f==null || p==null)
        {
            return;
        }
        p.print(contents);
        p.println();
    }

    public static void close(){
        p.close();
        f=null;
        p=null;
    }
}
