package ch.bytecrowd.markupeditor.jwt.rest;

import ch.bytecrowd.markupeditor.jwt.domain.User;
import ch.bytecrowd.markupeditor.jwt.helpers.HashUtil;
import ch.bytecrowd.markupeditor.jwt.helpers.TokenGenerator;
import io.quarkus.security.Authenticated;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Path("/api/auth")
public class AuthenticationResource {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";
    public static final Set ALL_ROLES = Set.of(ROLE_ADMIN, ROLE_USER);

    private final String issuer;
    final String cookieDomain;
    final Boolean cookieSecure;
    private final JsonWebToken jwt;

    @Inject
    public AuthenticationResource(
            @ConfigProperty(name = "mp.jwt.verify.issuer")String issuer,
            @ConfigProperty(name = "ch.bytecrowd.quarkusjwt.jwt.domain") String cookieDomain,
            @ConfigProperty(name = "ch.bytecrowd.quarkusjwt.jwt.secure") Boolean cookieSecure,
            JsonWebToken jwt
    ) {
        this.issuer = issuer;
        this.cookieDomain = cookieDomain;
        this.cookieSecure = cookieSecure;
        this.jwt = jwt;
    }

    @POST
    @Path("/login")
    public Response login(@QueryParam("login") String login, @QueryParam("password") String password) {
        final String hash = HashUtil.sha512(password);
        final Optional<User> user = User.find(
                "select u from User u where u.login = :login and u.password = :pass",
                        Map.of("login", login, "pass", hash))
                .firstResultOptional();

        return user
                .map(u -> TokenGenerator.generateToken(u.getLogin(), u.getRoles(), issuer))
                .map(token -> getOkResponse(user.get(), token, 1000 * 60 * 4 / 100))
                .orElseGet(this::getUnauthorizedResponse);
    }

    @POST
    @Path("/refresh")
    @RolesAllowed({AuthenticationResource.ROLE_USER})
    public Response refresh(@Context SecurityContext context) {
        final String login = jwt.getName();
        final String token = TokenGenerator.generateToken(login, ALL_ROLES, issuer);
        return getOkResponse(new User().login(login), token, calcMaxAge());
    }

    private Integer calcMaxAge() {
        Long maxAgeMs = new Date(jwt.getExpirationTime() * 1_000).getTime() - new Date().getTime();
        return maxAgeMs.intValue() / 1_000;
    }

    @POST
    @Path("/logout")
    public Response logout() {
        return getOkResponse(new User(), null, 0);
    }

    private Response getOkResponse(User user, String token, Integer maxAge) {
        final Response.ResponseBuilder builder = Response.status(Response.Status.OK).cookie(getNewCookie(token, maxAge));
        if (user != null) builder.entity(user);
        return builder.build();
    }

    private Response getUnauthorizedResponse() {
        return Response.status(Response.Status.UNAUTHORIZED).cookie(getNewCookie(null, 0)).build();
    }

    private NewCookie getNewCookie(String token, int maxAge) {
        return new NewCookie("jwt", token, "/", cookieDomain, "", maxAge, cookieSecure, true);
    }
}
