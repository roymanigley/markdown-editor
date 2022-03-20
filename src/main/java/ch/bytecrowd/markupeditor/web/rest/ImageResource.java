package ch.bytecrowd.markupeditor.web.rest;

import ch.bytecrowd.markupeditor.domain.MarkdownFile;
import ch.bytecrowd.markupeditor.domain.MarkdownImage;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@ApplicationScoped
@Path(ImageResource.API_PATH)
public class ImageResource {

    static final Logger LOG = LoggerFactory.getLogger(ImageResource.class);
    public static final String API_PATH = "/api/images";

    @GET
    @Path("/{id}")
    @Produces("image/jpeg")
    public Uni<Response> findOne(UUID id) {
        LOG.info("GET request to {}/{}" , API_PATH, id);
        Uni<MarkdownImage> image = MarkdownImage.findById(id);

        return image
                .map(img -> Response.ok(img.getContent()).header("Content-Disposition", "attachment;filename=" + "file.jpeg").build());
    }

    // TODO: reactive fileupload does not work
    @POST
    @ReactiveTransactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> create(byte[] bytes) {
        LOG.info("POST request to {} with file: {}" , API_PATH, bytes);
        return Uni.createFrom().item(bytes)
                .flatMap(b -> new MarkdownImage().content(b).persist())
                .map(image -> Response.ok(image).build());
    }

    @ReactiveTransactional
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(UUID id) {
        LOG.info("DELETE request to {}/{}" , API_PATH, id);
        return MarkdownFile.deleteById(id).map(b -> Response.accepted().build());
    }
}
