package com.revesoft.tcptest2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.ResponseCache;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button sendBtn = null;
    TextView serverTv = null;
    String serverResponse = null;
    Socket clientSocket = null;

    private byte[] b11 = {0x0d, 0x0a, 0x0d, 0x0a};
    byte b1[] = {0x0d, 0x0a,0x0d, 0x0a};
    byte b2[] = {0x23, 0x23, 0x23, 0x23};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBtn = (Button) findViewById(R.id.send_btn);
        serverTv = (TextView) findViewById(R.id.response_txt);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        TcpRequest tcpRequest = new TcpRequest();
        tcpRequest.execute("bismiAllah");
    }

    class TcpRequest extends AsyncTask<String, String, String> {

        int receivedcount=0;

        @Override
        protected String doInBackground(String... strings) {
            try {
                clientRoutine();
            } catch (Exception e) {
            }
            return null;
        }



        void clientRoutine() throws Exception {

            int port = 30000;
            clientSocket = new Socket("72.249.184.143", port );
            clientSocket.setTcpNoDelay(true);
            InputStream is = new BufferedInputStream(clientSocket.getInputStream());
            //BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            OutputStream os = clientSocket.getOutputStream();
//            clientSocket.setSoTimeout(5000);
            for (int j = 0; j < 50; j++)
            {
                byte[] t;
                byte[] t1 = new String("GET/clientCGI").getBytes();
                byte[] t2 = new String(":request~" + j + ":").getBytes();
                byte[] t3 = concatenateByteArrays(t1, b1);
                byte[] t4 = concatenateByteArrays(t2, b2);
                byte[] t5 = concatenateByteArrays(t3, t4);

                Log.i("Response: "," --> decodeData(is)");
                os.write(t5);
                os.flush();

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
              /*  Log.i("Response: ", serverResponse);


               // t = decodeData(is);

                Log.i("Response: "," decodeData(is) -->");
                Log.i("Received Count: ", String.valueOf(receivedcount++));*/
            }
//            clientSocket.close();


        }

        private boolean containBytes(byte[] b1, byte[] b2)
        {
            int l1 = b1.length;
            int l2 = b2.length;

            int i, i1, j;

            i = 0;
            while(i<l1)
            {
                j = 0;

                i1 = i;
                while(b1[i++]==b2[j++] && (i<l1 && j<l2));
                if(j >= l2) return true;

                i = i1 + 1;
            }

            return false;
        }

      /*  private  byte[] decodeData(InputStream is) throws Exception
        {
            String s1 = "";

            for (int c = 0; (c = is.read(b)) != -1;)
            {
                s1 += new String(b, 0, c);
            }

            return s1.getBytes();
        }*/

        private  byte[] decodeData(InputStream is) throws Exception
        {

            byte[] b = new byte[1024];
            String s1 = "";
            byte[] t = null;

            if(is != null)
                Log.i("String", "is not null ");

            if( clientSocket.isConnected())
                Log.i("String", "socket connected");
           else
                Log.i("String", "socket not connected");

//            for (int c = 0; (c = is.read(b)) > 0;)

//            while(!is.available());

            for (int c = 0; (c = is.read(b)) >= 0;)
            {
                s1 += new String(b, 0, c);

                Log.i("String",s1);

                t = s1.getBytes();

                if(containBytes(t, b11))
                    break;
            }

            return t;
        }

        byte[] concatenateByteArrays(byte[] a, byte[] b) {
            byte[] result = new byte[a.length + b.length];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
            return result;
        }

        public  String getStringFromInputStream(InputStream stream) throws IOException
        {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            return writer.toString();
        }
    }
}
