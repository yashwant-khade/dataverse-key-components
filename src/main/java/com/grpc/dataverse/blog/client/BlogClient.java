package com.grpc.dataverse.blog.client;

import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
    public static void main(String[] args) {

        System.out.println("Blog client started");

        BlogClient blogClient = new BlogClient();

        blogClient.run();
    }

    private void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext().build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        Blog blog = Blog.newBuilder()
                .setAuthorId("Yashwant1")
                .setTitle("New blog")
                .setContent("Hello this is my new blog")
                .build();

        CreateBlogResponse blogResponse = blogClient.createBlog(CreateBlogRequest.newBuilder().setBlog(blog).build());

        System.out.println("Recieved created blog");
        System.out.println(blogResponse.getBlog());

        String blogId = blogResponse.getBlog().getId();

        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder().setBlogId(blogId).build());
        System.out.println(readBlogResponse);

    }
}
