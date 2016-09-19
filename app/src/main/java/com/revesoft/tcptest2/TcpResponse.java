package com.revesoft.tcptest2;


import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpResponse extends AsyncTask<Integer,String,String> {
    @Override
    protected String doInBackground(Integer... integers) {
        Socket responseSocket = null;

        try {
            InputStream is = new BufferedInputStream(responseSocket.getInputStream());
            responseSocket = new Socket("72.249.184.143", 30000);
            responseSocket.setSoTimeout(5000);

            byte[] recvBuffer = new byte[2048];
            // while ((serverResponse=inFromServer.readLine())!=null)

            while(true)
            {
                byte[] lenByte = new byte[2];
                while(is.read(lenByte,0,2)==2)
                {
                    int len = lenByte[0]&0xFF;
                    len = len<<8|lenByte[1]&0xFF;

                    int readLen=0;
                    while(readLen<len)
                    {
                        readLen += is.read(recvBuffer,readLen,len-readLen);
                    }

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }
}
