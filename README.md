# mongodb-codec-generator

Simple MongoDB Codec generator allowing easy object-document mapping.

Configure MongoClient as follows:
```java
// project root directory
String path = "com.example.myproject";

// Get generated CodecRegistry
CodecRegistry generatedRegistry = new CodecGenerator(path).getCodecRegistry();

// Add generated codec registry to default codec registry
CodecRegistry registry = CodecRegistries.fromRegistries(
                MongoClients.getDefaultCodecRegistry(),
                registry);

//Setting for MongoDB Cluster
ClusterSettings clusterSettings = ClusterSettings.builder()
                .hosts(asList(new ServerAddress("localhost")))
                .description("Mongo Server")
                .build();

// Creating MongoDB Client Settings
MongoClientSettings settings = MongoClientSettings.builder()
                .clusterSettings(clusterSettings)
                .codecRegistry(codecRegistry) // register generated codec with settings
                .build();

// Create MongoDB Client
MongoClient client = MongoClients.create(settings);
```

Example usage with class

```java
// Class to store in MongoDB.
@Document
class User{
    @Id // field will be stored as "_id"
    String id;
    String name;
    @Ignore // field will not be stored in mongo
    String ignored;
    
    User(String name){
        this.name = name;
    }
}
```

Example mongo operations.

```java
// Get Database
MongoDatabase db = client.getDatabase("mydb");

//Get Collection
MongoCollection<User> collection = db.getCollection("users", User.class);

User testUser = new User("test");

// Save User
collection.insertOne(testUser);

// Get User
User user = collection.find(Filters.eq("name": "testUser")).first();
```

