package com.example.BlogAPI.entities;

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
public class Commentary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Size(min = 6, max = 50, message = "Некорректный никнейм")
    private String author;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    @ManyToOne()
//    @JoinColumn(name = "post", referencedColumnName = "id")
//    private Post post;
}
