package com.example.clientside;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    //private UiHandler mUiHandler;
    String message = "";
    //private static String ip = "10.130.1.229";
    private static String ip = "10.1.1.35";

    Button sendButton;
    private byte[][] receiveBuffer;

    private static int frameID;

    private Socket sendSocket = null;
    private Socket receiveSockets[] = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        receiveBuffer = new byte[Constants.TOTAL_NUM_OF_REC_BUFFERS][];
        message = "test";
        sendButton = findViewById(R.id.button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempCounterMain;

                sendMessage(); //This is a Async task to send the message to the Server

                for (tempCounterMain = 0; tempCounterMain < Constants.TOTAL_NUM_OF_REC_SOCKETS; tempCounterMain++) {
                    ReceivedBufferUpdateTask receivedBufferUpdateTask = new ReceivedBufferUpdateTask(receiveBuffer[tempCounterMain]);
                    SocektReceiveFile socektReceiveFile = new SocektReceiveFile(receiveSockets[tempCounterMain], receivedBufferUpdateTask);
                    ModuleManager.getDownloadManager().runDownloadFile(socektReceiveFile);
                }
            }


        });
    }

    public void sendMessage() {
        Client myClient = new Client();
        myClient.execute();
    }

    class Client extends AsyncTask<Void, Void, Void> {

        /*String dstAddress;
        int dstPort;
        String response = "";
        TextView textResponse;*/

        /*byte[] receiverBuffer;
        int frameID;*/

        public Client() {
            receiveSockets = new Socket[4];
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //Log.d("Debug", "2");
            int BUFFER_LENGTH = 9;
            int tempCounter;

            try {

                //Log.d("Debug", "5");
                byte[] sendBuffer = new byte[BUFFER_LENGTH];
                //Log.d("Debug", "6");
                for (int frame = 0; frame < 1; frame++) {
                    byte tile1 = 1;
                    byte tile2 = 3;
                    byte tile3 = 5;
                    byte tile4 = 6;
                    frameID = frame;
                    byte quality = 0;

                    sendSocket = new Socket(ip, 5550);
                    for (tempCounter = 1; tempCounter <= Constants.TOTAL_NUM_OF_REC_SOCKETS; tempCounter++) {
                        receiveSockets[tempCounter] = new Socket(ip, Constants.PORT_INDEX + tempCounter);
                    }

                    //Log.d("Debug", "7");
                    byte[] bytes = ByteBuffer.allocate(4).putInt(frameID).array();
                    sendBuffer[0] = bytes[0];
                    sendBuffer[1] = bytes[1];
                    sendBuffer[2] = bytes[2];
                    sendBuffer[3] = bytes[3];

                    sendBuffer[4] = tile1;
                    sendBuffer[5] = tile2;
                    sendBuffer[6] = tile3;
                    sendBuffer[7] = tile4;
                    sendBuffer[8] = quality;

                    try {
                        //Log.d("Debug", "Created Socket");
                        OutputStream os = sendSocket.getOutputStream();
                        //Log.d("Debug", "Sending msg");
                        os.write(sendBuffer, 0, sendBuffer.length);
                        os.flush();
                        sendSocket.shutdownOutput();

                        /*ReceivedBufferUpdateTask receivedBufferUpdateTask = new ReceivedBufferUpdateTask(receiverBuffer);
                        SocektReceiveFile socektReceiveFile = new SocektReceiveFile(socket2,receivedBufferUpdateTask);
                        ModuleManager.getDownloadManager().runDownloadFile(socektReceiveFile);*/

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                //response = "IOException: " + e.toString();
            } catch (Exception e) {
                e.printStackTrace();
                //Log.d("Degbig", e.toString());
            } finally {
                //Log.d("Degbig", "7");
                if (sendSocket != null) {
                    try {
                        sendSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }
}
