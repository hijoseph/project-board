package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import net.minidev.json.JSONUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("testdb")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("JAP 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository,
            @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // Given

        // When
        List<Article> articles = articleRepository.findAll();

        // Then
        Assertions.assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // Given
        long previousCount = articleRepository.count();

        // When
        Article savedArticle = articleRepository.save(Article.of("new article", "new Content", "#springboot"));

        // Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() throws NoSuchElementException {
        // Given
        try {
            Article article = articleRepository.findById(1L).orElseThrow();

            String updatedHashtag = "#springboot2";
            article.setHashtag(updatedHashtag);

            // When
            Article savedArticle = articleRepository.saveAndFlush(article);

            // Then
            Assertions.assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);

        } catch (NoSuchElementException e) { // Can't Find Id
            System.out.println("Error");
        }

    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // Given

        try {
            Article article = articleRepository.findById(1L).orElseThrow();

            long previousArticleCount = articleRepository.count();
            long previousArticleCommentCount = articleCommentRepository.count();
            int deletedCommentsSize = article.getArticleComments().size();

            String updatedHashtag = "#springboot2";
            article.setHashtag(updatedHashtag);

            // When
            articleRepository.delete(article);

        // Then
            Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
            Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
        } catch (NoSuchElementException e) {
            System.out.println("Error Delete");
        }
    }

}