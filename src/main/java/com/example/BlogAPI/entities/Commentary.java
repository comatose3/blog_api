package com.example.BlogAPI.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Commentary")
public class Commentary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    private String author;

    @NotNull
    private String text;

    public Commentary(String author, String text) {
        this.author = author;
        this.text = text;
    }

    @ManyToOne()
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    @JsonIgnoreProperties("comments")
    private Post post;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
//        updatedAt = now;
    }

//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }

}
