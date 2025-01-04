package model;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public abstract class Content implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private List<String> contributors;
    private int duration;
    private String description;

    public Content(int id, String title, List<String> contributors, int duration, String description) {
        this.id = id;
        this.title = title;
        this.contributors = contributors;
        this.duration = duration;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public void setContributors(List<String> contributors) {
        this.contributors = contributors;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
