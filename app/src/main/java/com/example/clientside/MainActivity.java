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
    private static String ip = "10.130.1.229";
    //private static String ip = "10.1.1.35";
    /**/
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

                //System.out.println()

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
            Log.d("Debug", "1");
            int BUFFER_LENGTH = 9;
            int tempCounter;

            try {

                //Log.d("Debug", "2");

                byte[] sendBuffer = new byte[BUFFER_LENGTH];

                //Log.d("Debug", "3");

                for (int chunk = 1; chunk < 10; chunk++) {

                    int port;
                    byte tile1 = 19;
                    byte tile2 = (byte)(chunk%15);
                    byte tile3 = 18;
                    byte tile4 = (byte)(chunk%15);
                    frameID = chunk;
                    byte quality = 0;

                    //Log.d("Debug", "4");

                    sendSocket = new Socket(ip, 5550);

                    //Log.d("Debug", "5");

                    for (tempCounter = 1; tempCounter <= Constants.TOTAL_NUM_OF_REC_SOCKETS; tempCounter++) {
                        port = Constants.PORT_INDEX + tempCounter;
                        //Log.d("Debug", String.valueOf(port));
                        receiveSockets[tempCounter - 1] = new Socket(ip, port);
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
                        sendSocket.close();

                        for (tempCounter = 0; tempCounter < Constants.TOTAL_NUM_OF_REC_SOCKETS; tempCounter++) {
                           //Log.d("Debug",String.valueOf(chunk)+ "  "+ String.valueOf(tempCounter));
                            ReceivedBufferUpdateTask receivedBufferUpdateTask = new ReceivedBufferUpdateTask(receiveBuffer[tempCounter]);
                            SocektReceiveFile socektReceiveFile = new SocektReceiveFile(receiveSockets[tempCounter], receivedBufferUpdateTask,tempCounter,chunk);
                            ModuleManager.getDownloadManager().runDownloadFile(socektReceiveFile);
                        }


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
                //Log.d("Debug", e.toString());
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
