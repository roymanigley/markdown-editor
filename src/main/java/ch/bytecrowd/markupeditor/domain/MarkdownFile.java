package ch.bytecrowd.markupeditor.domain;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
public class MarkdownFile extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "mdf_name", nullable = false)
    @NotBlank
    private String name;

    @Column(name = "mdf_content", nullable = false)
    @NotBlank
    private String content;

    @Column(name = "mdf_directory", nullable = false)
    @NotBlank
    private String directory;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    private List<String> tags;

    @Column(name = "mdf_creator", nullable = false)
    @NotBlank
    private String creator;

    @Column(name = "mdf_creator_date_time", nullable = false)
    @NotNull
    private ZonedDateTime createDateTime;

    @Column(name = "mdf_editor", nullable = false)
    @NotBlank
    private String editor;

    @Column(name = "mdf_edit_date_time", nullable = false)
    @NotNull
    private ZonedDateTime editDateTime;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MarkdownFile id(UUID id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarkdownFile name(String name) {
        this.setName(name);
        return this;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MarkdownFile content(String content) {
        this.setContent(content);
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public MarkdownFile directory(String directory) {
        this.setDirectory(directory);
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public MarkdownFile tags(List<String> tags) {
        this.setTags(tags);
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public MarkdownFile creator(String creator) {
        this.setCreator(creator);
        return this;
    }

    public ZonedDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(ZonedDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public MarkdownFile createDateTime(ZonedDateTime createDateTime) {
        this.setCreateDateTime(createDateTime);
        return this;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public MarkdownFile editor(String editor) {
        this.setEditor(editor);
        return this;
    }

    public ZonedDateTime getEditDateTime() {
        return editDateTime;
    }

    public void setEditDateTime(ZonedDateTime editDateTime) {
        this.editDateTime = editDateTime;
    }

    public MarkdownFile editDateTime(ZonedDateTime editDateTime) {
        this.setEditDateTime(editDateTime);
        return this;
    }

    @Override
    public String toString() {
        return "MarkdownFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", directory='" + directory + '\'' +
                ", tags=" + tags +
                ", creator='" + creator + '\'' +
                ", createDateTime=" + createDateTime +
                ", editor='" + editor + '\'' +
                ", editDateTime=" + editDateTime +
                '}';
    }
}
