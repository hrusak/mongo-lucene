package com.github.mongoutils.lucene;

import java.util.List;

import com.github.mongoutils.collections.DBObjectSerializer;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

public class MapDirectoryEntrySerializer implements DBObjectSerializer<MapDirectoryEntry> {
    
    private static final String BUFFERS = "buffers";
    private static final String BUFFER_SIZE = "bufferSize";
    private static final String LENGTH = "length";
    private static final String LAST_MODIFIED = "lastModified";
    
    String field;
    
    public MapDirectoryEntrySerializer(final String field) {
        this.field = field;
    }
    
    @Override
    public DBObject toDBObject(final MapDirectoryEntry element, final boolean equalFunctions, final boolean negate) {
        BasicDBObjectBuilder builder = new BasicDBObjectBuilder();
        DBObject dbObject;
        
        builder.append(LAST_MODIFIED, element.getLastModified());
        builder.append(LENGTH, element.getLength());
        builder.append(BUFFER_SIZE, element.getBufferSize());
        builder.append(BUFFERS, element.getBuffers());
        dbObject = builder.get();
        
        if (equalFunctions && negate) {
            dbObject = new BasicDBObject("$ne", dbObject);
        }
        
        if (field != null) {
            dbObject = new BasicDBObject(field, dbObject);
        }
        
        return dbObject;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public MapDirectoryEntry toElement(final DBObject dbObject) {
        Object tmp = field == null ? dbObject : dbObject.get(field);
        MapDirectoryEntry file = new MapDirectoryEntry();
        
        if (tmp != null && tmp instanceof DBObject) {
            file.setLastModified((Long) ((DBObject) tmp).get(LAST_MODIFIED));
            file.setLength((Long) ((DBObject) tmp).get(LENGTH));
            file.setBufferSize((Integer) ((DBObject) tmp).get(BUFFER_SIZE));
            file.setBuffers((List<byte[]>) ((DBObject) tmp).get(BUFFERS));
        }
        
        return file;
    }
    
}
