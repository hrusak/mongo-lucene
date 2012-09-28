# mongo-lucene

MongoDB-backed lucene directory for a scalable real-time search.

## License

Apache 2.0 License (http://www.apache.org/licenses/LICENSE-2.0)

## Requirements / Dependencies

* Java 1.6+ (http://www.java.com/de/download/)
* Apache Lucene 3.6.1+ (http://lucene.apache.org)
* MongoDB Java-Driver 2.8.0+ (https://github.com/mongodb/)

## How to get it

The maven dependecy:

```xml
<dependency>
    <groupId>com.github.mongodbutils</groupId>
    <artifactId>mongo-lucene</artifactId>
    <version>1.0</version>
</dependency>
```

## How to use it

```java
// Mongo connection
Mongo mongo = new Mongo("localhost", options);
DB db = mongo.getDB("testdb");
DBCollection dbCollection = db.getCollection("testcollection");

// serializers + map-store
DBObjectSerializer<String> keySerializer = new SimpleFieldDBObjectSerializer<String>("key");
DBObjectSerializer<MapDirectoryEntry> valueSerializer = new MapDirectoryEntrySerializer("value");
ConcurrentMap<String, MapDirectoryEntry> store = new MongoConcurrentMap<String, MapDirectoryEntry>(dbCollection, keySerializer, valueSerializer);

// lucene directory
Directory dir = new MapDirectory(store);

// index files
StandardAnalyzer analyser = new StandardAnalyzer(Version.LUCENE_36);
IndexWriter writer = new IndexWriter(dir, ...);
Document doc = new Document();
doc.add(new Field("title", "My file's content ...", Field.Store.YES, Field.Index.ANALYZED));
writer.addDocument(doc);
writer.close();

...

// search index
Query q = new QueryParser(Version.LUCENE_36, "title", analyser).parse("My*content");
IndexReader reader = IndexReader.open(dir);
IndexSearcher searcher = new IndexSearcher(reader);
TopScoreDocCollector collector = TopScoreDocCollector.create(10, true);
```

## Test it using mongodb-vm

The project comes with a fully functional VM with an mongodb installation for testing purpose.
You need to have VirtualBox (https://www.virtualbox.org/) and Vagrant (http://vagrantup.com/) installed to run the VM.
All necessary ports are forwarded to the VM so you can connect to mongodb as it were installed on your system directly.

Check the project out, open a console in that directory and type:

```text
cd mongovm
vagrant up
```

Integration tests are done with https://github.com/joelittlejohn/embedmongo-maven-plugin.
