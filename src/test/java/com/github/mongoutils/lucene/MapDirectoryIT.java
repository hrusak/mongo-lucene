package com.github.mongoutils.lucene;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import com.github.mongoutils.collections.DBObjectSerializer;
import com.github.mongoutils.collections.MongoConcurrentMap;
import com.github.mongoutils.collections.SimpleFieldDBObjectSerializer;

public class MapDirectoryIT extends AbstractMongoIT {
    
    Directory dir;
    ConcurrentMap<String, MapDirectoryEntry> store;
    
    @Override
    @Before
    public void createMongo() throws Exception {
        super.createMongo();
        
        DBObjectSerializer<String> keySerializer = new SimpleFieldDBObjectSerializer<String>("key");
        DBObjectSerializer<MapDirectoryEntry> valueSerializer = new MapDirectoryEntrySerializer("value");
        
        store = new MongoConcurrentMap<String, MapDirectoryEntry>(dbCollection, keySerializer, valueSerializer);
        dir = new MapDirectory(store);
    }
    
    @Test
    public void indexWorkspace() throws Exception {
        StandardAnalyzer analyser = new StandardAnalyzer(Version.LUCENE_36);
        IndexWriter w = new IndexWriter(dir, new IndexWriterConfig(Version.LUCENE_36, analyser));
        
        addDoc(w, "A Directory is a flat list of files. Files may be written once,");
        addDoc(w, "when they are created. Once a file is created it may only be opened for read,");
        addDoc(w, "or deleted. Random access is permitted both when reading and writing.");
        addDoc(w, "Java's i/o APIs not used directly, but rather all i/o is through this API.");
        addDoc(w, "This permits things such as:");
        addDoc(w, "implementation of RAM-based indices;");
        addDoc(w, "implementation indices stored in a database, via JDBC;");
        addDoc(w, "implementation of an index as a single file;");
        addDoc(w, "Directory locking is implemented by an instance of LockFactory,");
        addDoc(w, "and can be changed for each Directory instance using setLockFactory.");
        w.close();
        
        Query q = new QueryParser(Version.LUCENE_36, "title", analyser).parse("file*");
        IndexReader reader = IndexReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
        searcher.search(q, collector);
        
        assertEquals(3, collector.getTotalHits());
        
        searcher.close();
    }
    
    void addDoc(final IndexWriter w, final String value) throws IOException {
        Document doc = new Document();
        doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
        w.addDocument(doc);
    }
    
}
