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

    public SocektReceiveFile(Socket socket, ReceivedBufferUpdateTask updateMainThreadBuffer){
        this.socket = socket;
        this.updateResults = updateMainThreadBuffer;
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
        imageFrame = new byte[600000];
        if (socket != null) {
            //Log.d("Debug", "1");
            do {
                try {
                    //Log.d("Debug", "2");
                    InputStream inputStream = socket.getInputStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    //bytesAvailable = inputStream.available();

                    //if (bytesAvailable > 0) {
                    int current;
                    //bytesRead = inputStream.read(imageFrame, 0, imageFrame.length);
                    bytesRead = bufferedInputStream.read(imageFrame, 0, imageFrame.length);
                    current = 0;
                    //Log.d("Debug", String.valueOf(bytesRead));
                    current = bytesRead;

                    do {
                        bytesRead = bufferedInputStream.read(imageFrame, current, (imageFrame.length - current));
                        if (bytesRead >= 0) current += bytesRead;
                        //Log.d("Debug", String.valueOf(bytesRead));
                    } while (bytesRead > 0);

                    Log.d("Debug", "Reply Byte " + current + "\n");
                    //Log.d("Debug", "Reply Byte " +bytesRead+ "\n");
                    socket.shutdownInput();
                    //Log.d("Debug", "3");
                    //}

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (bytesRead == 0);
        }
        return true;
    }

}


