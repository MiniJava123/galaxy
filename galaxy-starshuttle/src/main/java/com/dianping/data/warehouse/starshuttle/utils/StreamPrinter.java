package com.dianping.data.warehouse.starshuttle.utils;


import java.io.*;

public class StreamPrinter extends Thread {
    private InputStream is;
    private OutputStream os;

    public StreamPrinter(InputStream is) {
        this(is, null);
    }

    public StreamPrinter(InputStream is, OutputStream redirect) {
        this.is = is;
        this.os = redirect;
    }

    public void run() {
        try {
            PrintWriter pw = null;
            if (os != null)
                pw = new PrintWriter(os);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                if (pw != null)
                    pw.println(line);
                System.out.println(line);
            }
            if (pw != null)
                pw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
