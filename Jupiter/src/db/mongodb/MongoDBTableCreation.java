package db.mongodb;

import java.text.ParseException;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
public class MongoDBTableCreation {
// Run as Java application to create MongoDB collections with index
	// Step 1. connection to MongoDB
	  // Run as Java application to create MongoDB collections with index.
	  public static void main(String[] args) throws ParseException {
			// Step 1, connetion to MongoDB
			// create() default parameter create("localhost", "27017")
			MongoClient mongoClient = MongoClients.create();
			MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

			// Step 2, remove old collections.
			db.getCollection("users").drop();
			db.getCollection("items").drop();

			// Step 3, create new collections
			IndexOptions indexOptions = new IndexOptions().unique(true);
			// new Document("user", 1)    1: ascending Order of ID
			// new Document 不是帮你创建数据，而是帮你转化出一个原生mongo db 操作的string
			// getCollection() will create a new collection if not exist
			db.getCollection("users").createIndex(new Document("user_id", 1), indexOptions);
			db.getCollection("items").createIndex(new Document("item_id", 1), indexOptions);

			// Step 4, insert fake user data and create index.
			db.getCollection("users").insertOne(
					new Document().append("user_id", "1111").append("password", "3229c1097c00d497a0fd282d586be050")
							.append("first_name", "John").append("last_name", "Smith"));

			mongoClient.close();
			System.out.println("Import is done successfully.");
	  }

}
