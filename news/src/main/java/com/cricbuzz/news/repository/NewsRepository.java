package com.cricbuzz.news.repository;

import com.cricbuzz.news.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByTag_Name(String tagName, Pageable pageable);
    Page<News> findByIsNewsDeletedFalse(Pageable pageable);
}