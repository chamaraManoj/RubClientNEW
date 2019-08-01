package com.example.clientside;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocektReceiveFile implements Runnable {

    private Socket socket;
    byte[] imageFrame;
    ReceivedBufferUpdateTask  updateResults;
    int thread;
    int chunk;

    public SocektReceiveFile(Socket socket, ReceivedBufferUpdateTask updateMainThreadBuffer,int thread, int chunk){
        this.socket = socket;
        this.updateResults = updateMainThreadBuffer;
        this.thread = thread;
        this.chunk = chunk;
        ModuleManager.getDownloadManager().getMainThreadExecutor().execute(updateResults);
    }

    @Override
    public void run() {
        getFIle();
        updateResults.setBackgroundMsg(imageFrame);
    }

    public boolean getFIle(){
        int bytesRead = 0;
        int bytesAvailable = 0;

        int totByteRead = 0;
        int[] layerLength = new int[4];
        int tempCounter;


        imageFrame = new byte[600000];
        if (socket != null) {
            //Log.d("Debug", "1");
           //do {
            try {
                //Log.d("Debug", "2");
                InputStream inputStream = socket.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                bytesRead = bufferedInputStream.read(imageFrame, 0, 8);

                for (tempCounter = 0; tempCounter < 4; tempCounter++) {
                    layerLength[tempCounter] = (imageFrame[tempCounter*2] & 0xFF) << 8 | imageFrame[tempCounter * 2 + 1] & 0xFF;
                }
                totByteRead = bytesRead;

                //Log.d("Debug", String.valueOf(bytesRead));
                int tempByteRead=0;
                for (tempCounter = 0; tempCounter < 4; tempCounter++) {
                    //Log.d("Debug", "Length " + String.valueOf(layerLength[tempCounter]) + "\n");
                    int endOfByte = totByteRead + layerLength[tempCounter];
                    //Log.d("Debug", "End Byte " + String.valueOf(endOfByte) + "\n");
                    do {
                        tempByteRead = bufferedInputStream.read(imageFrame, totByteRead, endOfByte - totByteRead);
                        if (tempByteRead >= 0) totByteRead += tempByteRead;
                        //Log.d("Debug", "Temp Byte " + String.valueOf(tempByteRead) + "\n");
                    } while (tempByteRead > 0);

                    tempByteRead=0;
                }
                Log.d("Debug", "Thread "+String.valueOf(thread)+"    Chunk "+String.valueOf(chunk)+ "     Tot " + String.valueOf(totByteRead) + "\n");

                //Log.d("Debug", "Reply Byte " +bytesRead+ "\n");
                socket.close();
                //Log.d("Debug", "3");
                //}

            } catch (IOException e) {
                e.printStackTrace();
            }
            //} while (bytesRead == 0);
        }
        return true;
    }

}


