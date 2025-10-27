package com.example.BlogAPI.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String author;
    private String name;
    private String content;

    public Post(String author, String name, String content) {
        this.author = author;
        this.name = name;
        this.content = content;
    }

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("post")
    private List<Commentary> comments = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addCommentary(Commentary commentary) {
        comments.add(commentary);
        commentary.setPost(this);
    }

    public void removeCommentary(Commentary commentary) {
        comments.remove(commentary);
        commentary.setPost(null);
    }
//    @ManyToMany()
//    @JoinTable(
//            name = "post_tag",
//            joinColumns = @JoinColumn(name = "post_id"),
//            inverseJoinColumns = @JoinColumn(name = "tag_id")
//    )
//    private List<Tag> tags = new ArrayList<>();
}
