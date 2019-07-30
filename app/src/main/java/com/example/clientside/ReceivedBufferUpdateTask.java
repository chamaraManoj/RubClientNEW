package com.example.clientside;

public class ReceivedBufferUpdateTask implements Runnable {
    private byte[] bufferMain;
    private byte[] bufferLocal;

    public ReceivedBufferUpdateTask(byte [] buffer){
        this.bufferMain = buffer;
    }

    public void setBackgroundMsg(byte [] bufferFromScoketRunncable){
        bufferLocal = bufferFromScoketRunncable;
    }

    public void run() {
        this.bufferMain = this.bufferLocal;
    }
}
