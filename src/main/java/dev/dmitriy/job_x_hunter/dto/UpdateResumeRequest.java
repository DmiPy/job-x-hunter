package dev.dmitriy.job_x_hunter.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateResumeRequest {
    @NotNull
    private Long resumeId;

    private String title;
    private String url;
    private String description;

    @JsonIgnore
    private boolean titleProvided;
    @JsonIgnore
    private boolean urlProvided;
    @JsonIgnore
    private boolean descriptionProvided;

    public void setTitle(String title) {
        this.titleProvided = true;
        this.title = title;
    }

    public void setUrl(String url) {
        this.urlProvided = true;
        this.url = url;
    }

    public void setDescription(String description) {
        this.descriptionProvided = true;
        this.description = description;
    }

    @AssertTrue(message = "title must be non-blank when explicitly provided")
    @JsonIgnore
    public boolean isTitleValid() {
        return !titleProvided || (title != null && !title.isBlank());
    }

    @AssertTrue(message = "url must be non-blank when explicitly provided")
    @JsonIgnore
    public boolean isUrlValid() {
        return !urlProvided || (url != null && !url.isBlank());
    }
}
