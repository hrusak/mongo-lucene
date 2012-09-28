package com.github.mongoutils.lucene;

import java.io.EOFException;
import java.io.IOException;

import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;

public class MapDirectoryInputStream extends IndexInput {
    
    MapDirectoryEntry file;
    byte[] currentBuffer;
    int bufferLength;
    int bufferPosition;
    int currentBufferIndex;
    long bufferStart;
    long length;
    
    public MapDirectoryInputStream(final String name, final MapDirectoryEntry file) throws IOException {
        super(name);
        this.file = file;
        length = file.length;
        if (length / file.getBufferSize() >= Integer.MAX_VALUE) {
            throw new IOException("MapDirectoryInputStream too large length=" + length + ": " + name);
        }
        
        currentBufferIndex = -1;
        currentBuffer = null;
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public long length() {
        return length;
    }
    
    @Override
    public byte readByte() throws IOException {
        if (bufferPosition >= bufferLength) {
            currentBufferIndex++;
            switchCurrentBuffer(true);
        }
        return currentBuffer[bufferPosition++];
    }
    
    @Override
    public void readBytes(final byte[] b, int offset, int len) throws IOException {
        while (len > 0) {
            if (bufferPosition >= bufferLength) {
                currentBufferIndex++;
                switchCurrentBuffer(true);
            }
            
            int remainInBuffer = bufferLength - bufferPosition;
            int bytesToCopy = len < remainInBuffer ? len : remainInBuffer;
            System.arraycopy(currentBuffer, bufferPosition, b, offset, bytesToCopy);
            offset += bytesToCopy;
            len -= bytesToCopy;
            bufferPosition += bytesToCopy;
        }
    }
    
    private final void switchCurrentBuffer(final boolean enforceEOF) throws IOException {
        bufferStart = file.getBufferSize() * currentBufferIndex;
        if (currentBufferIndex >= file.numBuffers()) {
            if (enforceEOF) {
                throw new EOFException("Read past EOF (resource: " + this + ")");
            } else {
                currentBufferIndex--;
                bufferPosition = file.getBufferSize();
            }
        } else {
            currentBuffer = file.getBuffer(currentBufferIndex);
            bufferPosition = 0;
            long buflen = length - bufferStart;
            bufferLength = buflen > file.getBufferSize() ? file.getBufferSize() : (int) buflen;
        }
    }
    
    @Override
    public void copyBytes(final IndexOutput out, final long numBytes) throws IOException {
        assert numBytes >= 0 : "numBytes=" + numBytes;
        
        long left = numBytes;
        while (left > 0) {
            if (bufferPosition == bufferLength) {
                ++currentBufferIndex;
                switchCurrentBuffer(true);
            }
            
            final int bytesInBuffer = bufferLength - bufferPosition;
            final int toCopy = (int) (bytesInBuffer < left ? bytesInBuffer : left);
            out.writeBytes(currentBuffer, bufferPosition, toCopy);
            bufferPosition += toCopy;
            left -= toCopy;
        }
        
        assert left == 0 : "Insufficient bytes to copy: numBytes=" + numBytes + " copied=" + (numBytes - left);
    }
    
    @Override
    public long getFilePointer() {
        return currentBufferIndex < 0 ? 0 : bufferStart + bufferPosition;
    }
    
    @Override
    public void seek(final long pos) throws IOException {
        if (currentBuffer == null || pos < bufferStart || pos >= bufferStart + file.getBufferSize()) {
            currentBufferIndex = (int) (pos / file.getBufferSize());
            switchCurrentBuffer(false);
        }
        bufferPosition = (int) (pos % file.getBufferSize());
    }
    
}
