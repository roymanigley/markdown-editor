package ch.bytecrowd.markupeditor.web.rest;

import ch.bytecrowd.markupeditor.domain.MarkdownFile;
import ch.bytecrowd.markupeditor.domain.MarkdownImage;
import ch.bytecrowd.markupeditor.jwt.rest.AuthenticationResource;
import ch.bytecrowd.markupeditor.web.rest.dto.FileUploadForm;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Path(ImageResource.API_PATH)
@RolesAllowed({AuthenticationResource.ROLE_USER})
public class ImageResource {

    static final Logger LOG = LoggerFactory.getLogger(ImageResource.class);
    public static final String API_PATH = "/api/images";

    @GET
    @RolesAllowed({AuthenticationResource.ROLE_ADMIN})
    public List<MarkdownImage> findAll() {
        LOG.info("GET request to {}" , API_PATH);
        return MarkdownImage.findAll().list();
    }

    @GET
    @Path("/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response findOne(@PathParam("id") UUID id) {
        LOG.info("GET request to {}/{}" , API_PATH, id);
        MarkdownImage image = MarkdownImage.findById(id);
        return Response.ok(image.getContent())
                .build();
    }

    @POST
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response create(@MultipartForm FileUploadForm input) {
        LOG.info("POST request to {} with file: {}" , API_PATH, input.getData());
        MarkdownImage image = new MarkdownImage().content(input.getData());
        image.persist();
        return Response.temporaryRedirect(URI.create(API_PATH + "/" + image.getId())).entity(image).build();
    }

    @DELETE
    @Transactional
    @Path("{id}")
    @RolesAllowed({AuthenticationResource.ROLE_ADMIN})
    public Response delete(@PathParam("id") UUID id) {
        LOG.info("DELETE request to {}/{}" , API_PATH, id);
        MarkdownFile.deleteById(id);
        return Response.accepted().build();
    }
}

