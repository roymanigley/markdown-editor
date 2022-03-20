package ch.bytecrowd.markupeditor.jwt.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "mde_user")
public class User extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotNull
    @Column(name = "login", unique = true)
    private String login;
    @JsonIgnore
    @Column(name = "password")
    private String password;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User uuid(UUID uuid) {
        setId(uuid);
        return this;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public User login(String login) {
        this.setLogin(login);
        return this;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User password(String password) {
        this.setPassword(password);
        return this;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public User roles(Set<String> roles) {
        this.setRoles(roles);
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid=" + id +
                ", login='" + login + '\'' +
                ", password='**********************'" +
                ", roles=" + roles +
                '}';
    }
}
