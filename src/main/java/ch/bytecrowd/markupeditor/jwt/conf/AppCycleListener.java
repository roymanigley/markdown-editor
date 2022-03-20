package ch.bytecrowd.markupeditor.jwt.conf;

import ch.bytecrowd.markupeditor.jwt.domain.User;
import ch.bytecrowd.markupeditor.jwt.helpers.HashUtil;
import ch.bytecrowd.markupeditor.jwt.rest.UserResource;
import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

@ApplicationScoped
public class AppCycleListener {

    private static final Logger LOG = LoggerFactory.getLogger(AppCycleListener.class);

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (User.count() < 1) {
            User user = new User()
                    .login("admin")
                    .password(HashUtil.sha512("admin"))
                    .roles(UserResource.ALL_ROLES);
            user.persist();
            LOG.info("Initial user created: {}", user);
        }
    }
}
