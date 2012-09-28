package com.github.mongoutils.lucene;

import java.util.ArrayList;
import java.util.List;

public class MapDirectoryEntry {
    
    List<byte[]> buffers = new ArrayList<byte[]>();
    int bufferSize;
    long length;
    long lastModified = System.currentTimeMillis();
    
    public int getBufferSize() {
        return bufferSize;
    }
    
    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }
    
    public long getLength() {
        return length;
    }
    
    protected void setLength(final long length) {
        this.length = length;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    protected void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }
    
    protected byte[] addBuffer(final int size) {
        byte[] buffer = new byte[size];
        buffers.add(buffer);
        return buffer;
    }
    
    protected byte[] getBuffer(final int index) {
        return buffers.get(index);
    }
    
    protected int numBuffers() {
        return buffers.size();
    }
    
    public List<byte[]> getBuffers() {
        return buffers;
    }
    
    public void setBuffers(final List<byte[]> buffers) {
        this.buffers = buffers;
    }
    
}
