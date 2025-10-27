//package com.example.BlogAPI.entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class Tag {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @Column(unique = true)
//    private String name;
//
//    @ManyToMany(mappedBy = "tags")
//    private List<Post> posts = new ArrayList<>();
//
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        LocalDateTime now = LocalDateTime.now();
//        createdAt = now;
//        updatedAt = now;
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//}
