package ch.bytecrowd.markupeditor.web.rest;

import ch.bytecrowd.markupeditor.domain.MarkdownFile;
import ch.bytecrowd.markupeditor.jwt.rest.AuthenticationResource;
import ch.bytecrowd.markupeditor.web.rest.dto.MarkdownFileWithOutContent;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
@Path(FileResource.API_PATH)
@RolesAllowed({AuthenticationResource.ROLE_USER})
public class FileResource {

    static final Logger LOG = LoggerFactory.getLogger(FileResource.class);
    public static final String API_PATH = "/api/files";

    private final JsonWebToken jwt;

    @Inject
    public FileResource(JsonWebToken jwt) {
        this.jwt = jwt;
    }

    @Transactional
    @GET
    public List<MarkdownFileWithOutContent> findAll(@QueryParam("query") String query, @QueryParam("tags") List<String> tags) {
        LOG.info("GET request to {} with query={} and tags=" , API_PATH, query, tags);
        PanacheQuery<MarkdownFile> files;
        if (query.isBlank() && tags.isEmpty()) {
            files = MarkdownFile.find(
                    " select distinct mdf " +
                            "from " +
                            "   MarkdownFile mdf " +
                            "       left join fetch mdf.tags tags " +
                            "where " +
                            "   mdf.creator = :creator",
                    Parameters.with("creator", jwt.getName()));
        } else {
            files = MarkdownFile.find(
                    " select distinct mdf " +
                            "from " +
                            "   MarkdownFile mdf " +
                            "       left join fetch mdf.tags tags " +
                            "where " +
                            "   mdf.creator = :creator" +
                            "   AND (:query is null or lower(mdf.name) like '%' || lower(:query) || '%')" +
                            "   AND (:tags is null or tags in :tags)",
                    Parameters.with("query", query.isBlank() ? null : query)
                            .and("tags", tags.isEmpty() ? null : tags)
                            .and("creator", jwt.getName())
            );
        }
        return files.list().stream()
                .map(file -> MarkdownFileWithOutContent.from(file))
                .collect(Collectors.toList());
    }

    @Transactional
    @GET
    @Path("/{id}")
    public MarkdownFile findOne(@PathParam("id") UUID id) {
        LOG.info("GET request to {}/{}" , API_PATH, id);

        LOG.info("JDBC_DATABASE_URL: {}",  System.getenv("JDBC_DATABASE_URL"));
        LOG.info("DATABASE_URL: {}",  System.getenv("DATABASE_URL"));
        Optional<MarkdownFile> file = MarkdownFile.findByIdOptional(id);
        return file.orElseThrow(NotFoundException::new);
    }

    @Transactional
    @POST
    public Response create(@Valid MarkdownFile file) {
        LOG.info("POST request to {} with file: {}" , API_PATH, file);
            file.id(null)
                .creator(jwt.getName())
                .createDateTime(ZonedDateTime.now())
                .editor(jwt.getName())
                .editDateTime(ZonedDateTime.now());
            file.persist();
            return Response.created(URI.create("/api/files/" + file.getId())).entity(file).build();
    }

    @Transactional
    @PUT
    public Response update(@Valid MarkdownFile file) {
        LOG.info("PUT request to {} with file: {}" , API_PATH, file);
                Optional<MarkdownFile> fromDb = MarkdownFile
                    .findByIdOptional(file.getId());
                return fromDb
                    .map(f -> f
                            .content(file.getContent())
                            .name(file.getName())
                            .directory(file.getDirectory())
                            .tags(file.getTags())
                            .editDateTime(ZonedDateTime.now())
                            .editor(jwt.getName())
                    ).stream().peek(f -> f.persist())
                    .map(f -> Response.ok(f).build())
                    .findFirst()
                    .orElseThrow(NotFoundException::new);
    }

    @Transactional
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") UUID id) {
        LOG.info("DELETE request to {}/{}" , API_PATH, id);
        MarkdownFile.deleteById(id);
        return Response.accepted().build();
    }
}
