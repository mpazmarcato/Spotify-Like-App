package model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass // estratégia: reutilziar os atributos
public abstract class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    @ElementCollection
    private final List<String> contributors = new ArrayList<>();
    private Integer duration;
    private String description;

    public Content() {}

    public Content(Integer id, String title, Integer duration, String description) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
