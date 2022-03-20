package ch.bytecrowd.markupeditor.domain;

import ch.bytecrowd.markupeditor.web.rest.dto.MarkdownFileWithOutContent;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

@Entity
public class MarkdownImage extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mdf_content", nullable = false)
    @NotNull
    @JsonIgnore
    @Lob
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkdownImage that = (MarkdownImage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "MarkdownImage{" +
                "id=" + id +
                '}';
    }
}
