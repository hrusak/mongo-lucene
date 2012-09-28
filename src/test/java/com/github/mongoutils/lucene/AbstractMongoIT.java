package com.github.mongoutils.lucene;

import org.junit.After;
import org.junit.Before;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public abstract class AbstractMongoIT {

    protected Mongo mongo;
    protected DB db;
    protected DBCollection dbCollection;

    @Before
    public void createMongo() throws Exception {
        MongoOptions options = new MongoOptions();
        mongo = new Mongo("localhost", options);
        mongo.dropDatabase("testdb");
        db = mongo.getDB("testdb");
        dbCollection = db.getCollection("testcollection");
    }

    @After
    public void closeMongo() {
        db.dropDatabase();
        mongo.close();
    }

}
