package ch.bytecrowd.markupeditor.jwt.rest;

import ch.bytecrowd.markupeditor.jwt.domain.User;
import ch.bytecrowd.markupeditor.jwt.helpers.HashUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Path("/api/users")
public class UserResource {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final Set ALL_ROLES = Set.of(ROLE_ADMIN, ROLE_USER);

    private final String issuer;
    private final  int maxAge;
    private final JsonWebToken jwt;

    @Inject
    public UserResource(
            @ConfigProperty(name = "mp.jwt.verify.issuer")String issuer,
            @ConfigProperty(name = "ch.bytecrowd.quarkusjwt.jwt.duration") int maxAge,
            JsonWebToken jwt
    ) {
        this.issuer = issuer;
        this.maxAge = maxAge;
        this.jwt = jwt;
    }

    @GET
    @RolesAllowed({AuthenticationResource.ROLE_USER})
    public Response findAll() {
        List<User> users = User.findAll().list();
        return Response.ok(users).build();
    }

    @POST
    @Transactional
    @RolesAllowed({AuthenticationResource.ROLE_ADMIN})
    public Response create(@Valid User user, @QueryParam("password") String password) {
        final String hash = HashUtil.sha512(password);
        user.uuid(null).password(hash);
        user.persist();
        return Response.created(URI.create("/api/user/"))
                .entity(user)
                .build();
    }

    @PUT
    @Transactional
    @RolesAllowed({AuthenticationResource.ROLE_USER})
    public Response updatePassword(@QueryParam("password") String password) {
        Optional<User> user = User.find(
                        "from User u where u.login = :login",
                        Map.of("login", jwt.getName())
                ).firstResultOptional();

        return user.map(u -> {
                    String hash = HashUtil.sha512(password);
                    u.password(hash).persist();
                    return u;
                })
                .map(u -> Response.ok(u).build())
                .orElseThrow(NotFoundException::new);
    }

    @PUT
    @Transactional
    @Path("/roles")
    @RolesAllowed({AuthenticationResource.ROLE_ADMIN})
    public Response updateRole(@QueryParam("login") String login, @QueryParam("roles") Set<String> roles) {
        Optional<User> user = User.find(
                "from User u where u.login = :login",
                Map.of("login", login)
        ).firstResultOptional();

        return user
                .map(u -> {
                    u.roles(roles).persist();
                    return u;
                })
                .map(u -> Response.ok(u).build())
                .orElseThrow(NotFoundException::new);
    }
}
