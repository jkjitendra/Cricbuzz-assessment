package com.cricbuzz.news.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String heading;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private Instant newsCreatedAt;
    private Instant newsLastUpdatedAt;
    private boolean isNewsDeleted;
    private Instant newsDeletionTimestamp;

}