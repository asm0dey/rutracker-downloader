package com.github.asm0dey;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/{id}.torrent")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getIt(@PathParam("id") Long id) throws IOException {
        DefaultHttpClient httpclient = Main.httpclient;
        HttpPost httpPost = new HttpPost("http://dl.rutracker.org/forum/dl.php?t=" + id);
        httpPost.setHeader(new BasicHeader("referer", "http://rutracker.org/forum/viewtopic.php?t=" + id));
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("t", id.toString());
        httpPost.setEntity(new StringEntity("t=" + id));
        System.out.println("httpPost = " + httpPost);
        HttpResponse execute = httpclient.execute(httpPost);
        return Response.ok().entity(execute.getEntity().getContent()).header("Content-type", "application/x-bittorrent").build();
    }
}
