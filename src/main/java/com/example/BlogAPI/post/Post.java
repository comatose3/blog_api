package com.example.BlogAPI.post;

import com.example.BlogAPI.comment.Commentary;
import com.example.BlogAPI.tag.Tag;
import com.example.BlogAPI.user.User;
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
@Table(name = "post") // Оставляем, так как таблица уже создана через Liquibase
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnoreProperties("posts")
    private User user;

    @Column(nullable = false) // Добавляем валидацию
    private String name;

    @Column(nullable = false, length = 5000) // Указываем длину для content
    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("post")
    private List<Commentary> comments = new ArrayList<>();

    @ManyToMany()
    @JoinTable(
            name = "post_tag", // Оставляем, так как таблица связей уже создана
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
}