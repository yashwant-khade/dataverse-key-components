package com.grpc.dataverse.blog.server;

import com.mongodb.client.*;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.rmi.server.ObjID;

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        Blog blog = request.getBlog();
        System.out.println("Recieved create blog request");

        Document document = new Document("author_id", blog.getAuthorId()).
                append("title", blog.getTitle()).
                append("content", blog.getContent());

        collection.insertOne(document);

        System.out.println("Inserting a blog");
        String id = document.getObjectId("_id").toString();
        System.out.println("Inserted a blog: " + id);

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build()).build();

        responseObserver.onNext(response);

        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
        String blogId = request.getBlogId();

        Document result = collection.find(eq("_id", new ObjectId(blogId))).first();

        if (result == null) {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("No blog found with the corresponding id")
                            .asRuntimeException()
            );
        } else {
            Blog blog = Blog.newBuilder()
                    .setId(blogId)
                    .setAuthorId(result.getString("author_id"))
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .build();

            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();
        }

    }
}
