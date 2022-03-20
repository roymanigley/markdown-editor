package ch.bytecrowd.markupeditor.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class MarkdownImage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mdf_content", nullable = false)
    @NotNull
    @JsonIgnore
    private byte[] content;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MarkdownImage id(UUID id) {
        this.setId(id);
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public MarkdownImage content(byte[] content) {
        this.setContent(content);
        return this;
    }

    @Override
    public String toString() {
        return "MarkdownImage{" +
                "id=" + id +
                '}';
    }
}
