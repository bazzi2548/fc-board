package com.fc.board.repository;

import com.fc.board.config.JpaConfig;
import com.fc.board.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@DataJpaTest
@Import(JpaConfig.class)
class JPARepositoryTest {

    @Autowired
    private final ArticleRepository articleRepository;
    @Autowired
    private final ArticleCommentRepository articleCommentRepository;

    public JPARepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select test")
    @Test
    public void givenTestData_whenSelecting_thenWorksFine() {
        //given

        //when
        List<Article> articles = articleRepository.findAll();

        //then
        assertThat(articles).isNotNull().hasSize(6);
    }

    @DisplayName("insert test")
    @Test
    public void givenTestData_whenInserting_thenWorksFine() {
        //given
        long previousCount = articleRepository.count();
        Article article = Article.of("new article", "new content", "#spring");

        //when
        Article savedArticle = articleRepository.save(article);

        //then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update test")
    @Test
    public void givenTestData_whenUpdating_thenWorksFine() {
        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        String updateHashtag = "#springboot";
        article.setHashTag(updateHashtag);

        //when
        Article savedArticle = articleRepository.save(article);
        articleRepository.flush();

        //then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashTag", updateHashtag);
    }

    @DisplayName("delete test")
    @Test
    public void givenTestData_whenDeleting_thenWorksFine() {
        //given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousCount = articleRepository.count();
        long previousCommentCount = articleCommentRepository.count();
        int deletedCommentSize = article.getArticleComment().size();

        //when
        articleRepository.delete(article);

        //then
        assertThat(articleRepository.count())
                .isEqualTo(previousCount - 1);
        assertThat(articleCommentRepository.count())
                .isEqualTo(previousCommentCount - deletedCommentSize);
    }
}
