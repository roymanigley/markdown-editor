package ch.bytecrowd.markupeditor.web.rest;

import ch.bytecrowd.markupeditor.domain.MarkdownFile;
import io.quarkus.hibernate.reactive.panache.common.runtime.ReactiveTransactional;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.UUID;

@ApplicationScoped
@Path(FileResource.API_PATH)
public class FileResource {

    static final Logger LOG = LoggerFactory.getLogger(FileResource.class);
    public static final String API_PATH = "/api/files";

    @GET
    public Multi<MarkdownFile> findAll(@QueryParam("query") String query) {
        LOG.info("GET request to {} with query={}" , API_PATH, query);
        if (query == null || query.isBlank()) {
            return MarkdownFile.findAll().stream();
        }
        return MarkdownFile.find(
                "from MarkdownFile mdf where lower(mdf.name) like '%' || lower(:query) || '%'",
                Parameters.with("query", query)
        ).stream();
    }

    @GET
    @Path("/{id}")
    public Uni<MarkdownFile> findOne(UUID id) {
        LOG.info("GET request to {}/{}" , API_PATH, id);
        return MarkdownFile.findById(id);
    }

    @ReactiveTransactional
    @POST
    public Uni<Response> create(@Valid MarkdownFile file) {
        LOG.info("POST request to {} with file: {}" , API_PATH, file);
            file.id(null)
                .creator("anonymous")
                .createDateTime(ZonedDateTime.now())
                .editor("anonymous")
                .editDateTime(ZonedDateTime.now());
            Uni<MarkdownFile> saved = file.persist();
            return saved.map(f -> Response.created(URI.create("/api/files/" + f.getId())).entity(f).build());
    }

    @ReactiveTransactional
    @PUT
    public Uni<Response> update(@Valid MarkdownFile file) {
        LOG.info("PUT request to {} with file: {}" , API_PATH, file);
                Uni<MarkdownFile> fromDb = MarkdownFile
                    .findById(file.getId());
                return fromDb
                    .invoke(f -> f
                            .content(file.getContent())
                            .name(file.getName())
                            .directory(file.getDirectory())
                            .tags(file.getTags())
                            .editDateTime(ZonedDateTime.now())
                            .editor("anonymous")
                    )
                    .flatMap(f -> f.persist())
                    .map(f -> Response.ok(f).build());
    }

    @ReactiveTransactional
    @DELETE
    @Path("{id}")
    public Uni<Response> delete(UUID id) {
        LOG.info("DELETE request to {}/{}" , API_PATH, id);
        return MarkdownFile.deleteById(id).map(b -> Response.accepted().build());
    }
}
