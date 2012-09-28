package com.github.mongoutils.lucene;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.store.SingleInstanceLockFactory;

public class MapDirectory extends Directory {
    
    public static final int DEFAULT_BUFFER_SIZE = 512;
    
    ConcurrentMap<String, MapDirectoryEntry> store;
    int bufferSize;
    
    public MapDirectory(final ConcurrentMap<String, MapDirectoryEntry> store) throws IOException {
        this(store, DEFAULT_BUFFER_SIZE);
    }
    
    public MapDirectory(final ConcurrentMap<String, MapDirectoryEntry> store, final int bufferSize) throws IOException {
        this.store = store;
        this.bufferSize = bufferSize;
        setLockFactory(new SingleInstanceLockFactory());
    }
    
    public ConcurrentMap<String, MapDirectoryEntry> getStore() {
        return store;
    }
    
    @Override
    public String[] listAll() throws IOException {
        String[] files = new String[store.size()];
        int index = 0;
        
        for (String file : store.keySet()) {
            files[index++] = file;
        }
        
        return files;
    }
    
    @Override
    public boolean fileExists(final String name) throws IOException {
        return store.containsKey(name);
    }
    
    @Override
    @Deprecated
    public long fileModified(final String name) throws IOException {
        if (!store.containsKey(name)) {
            throw new FileNotFoundException(name);
        }
        return store.get(name).getLastModified();
    }
    
    @Override
    @Deprecated
    public void touchFile(final String name) throws IOException {
        MapDirectoryEntry file;
        
        if (!store.containsKey(name)) {
            throw new FileNotFoundException(name);
        }
        
        file = store.get(name);
        file.setLastModified(System.currentTimeMillis());
        store.put(name, file);
    }
    
    @Override
    public void deleteFile(final String name) throws IOException {
        store.remove(name);
    }
    
    @Override
    public long fileLength(final String name) throws IOException {
        if (!store.containsKey(name)) {
            throw new FileNotFoundException(name);
        }
        return store.get(name).getLength();
    }
    
    @Override
    public IndexOutput createOutput(final String name) throws IOException {
        MapDirectoryEntry file;
        
        ensureOpen();
        file = new MapDirectoryEntry();
        file.setBufferSize(bufferSize);
        store.put(name, file);
        return new MapDirectoryOutputStream(file, name, store, bufferSize);
    }
    
    @Override
    public IndexInput openInput(final String name) throws IOException {
        ensureOpen();
        if (!store.containsKey(name)) {
            throw new FileNotFoundException(name);
        }
        return new MapDirectoryInputStream(name, store.get(name));
    }
    
    @Override
    public void close() throws IOException {
        isOpen = false;
    }
    
}
