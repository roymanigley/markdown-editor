package ch.bytecrowd.markupeditor.web.rest.dto;

import ch.bytecrowd.markupeditor.domain.MarkdownFile;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.FormParam;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MarkdownFileWithOutContent {

    private UUID id;

    private String name;

    private String directory;

    private List<String> tags;

    private String creator;

    private ZonedDateTime createDateTime;

    private String editor;

    private ZonedDateTime editDateTime;

    public static MarkdownFileWithOutContent from(MarkdownFile file) {
        return new MarkdownFileWithOutContent()
                .id(file.getId())
                .name(file.getName())
                .tags(file.getTags())
                .directory(file.getDirectory())
                .editor(file.getEditor())
                .editDateTime(file.getEditDateTime())
                .creator(file.getCreator())
                .createDateTime(file.getCreateDateTime());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MarkdownFileWithOutContent id(UUID id) {
        this.setId(id);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarkdownFileWithOutContent name(String name) {
        this.setName(name);
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public MarkdownFileWithOutContent directory(String directory) {
        this.setDirectory(directory);
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public MarkdownFileWithOutContent tags(List<String> tags) {
        this.setTags(tags);
        return this;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public MarkdownFileWithOutContent creator(String creator) {
        this.setCreator(creator);
        return this;
    }

    public ZonedDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(ZonedDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public MarkdownFileWithOutContent createDateTime(ZonedDateTime createDateTime) {
        this.setCreateDateTime(createDateTime);
        return this;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public MarkdownFileWithOutContent editor(String editor) {
        this.setEditor(editor);
        return this;
    }

    public ZonedDateTime getEditDateTime() {
        return editDateTime;
    }

    public void setEditDateTime(ZonedDateTime editDateTime) {
        this.editDateTime = editDateTime;
    }

    public MarkdownFileWithOutContent editDateTime(ZonedDateTime editDateTime) {
        this.setEditDateTime(editDateTime);
        return this;
    }

    @Override
    public String toString() {
        return "MarkdownFileWithOutContent{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", directory='" + directory + '\'' +
                ", tags=" + tags +
                ", creator='" + creator + '\'' +
                ", createDateTime=" + createDateTime +
                ", editor='" + editor + '\'' +
                ", editDateTime=" + editDateTime +
                '}';
    }
}
