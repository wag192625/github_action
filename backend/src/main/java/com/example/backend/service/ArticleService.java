package com.example.backend.service;

import com.example.backend.domain.Article;
import com.example.backend.dto.ArticleRequestDto;
import com.example.backend.dto.ArticleResponseDto;
import com.example.backend.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final S3Service s3Service;
	//	@Transactional
//	public ArticleResponseDto createArticle(ArticleRequestDto requestDto) {
//
//		Article article = Article.builder()
//				.title(requestDto.getTitle())
//				.content(requestDto.getContent())
//				.build();
//
//		Article savedArticle = articleRepository.save(article);
//
//		return toResponseDto(savedArticle);
//	}
	@Transactional
	public ArticleResponseDto createArticle(ArticleRequestDto requestDto) {
		// S3 업로드
		// S3service.upload() 호출
		// uploadResult - imageUrl, s3Key 저장
	  Map<String ,String > uploadResult = s3Service.uploadFile(requestDto.getFile());

		String imageUrl = uploadResult.get("imageUrl");
		String s3Key = uploadResult.get("s3Key");

		Article article = Article.builder()
				.title(requestDto.getTitle())
				.content(requestDto.getContent())
				.imageUrl(imageUrl)
				.s3Key(s3Key)
				.originFileName(requestDto.getFile().getOriginalFilename())
				.build();


		Article saveArticle = articleRepository.save(article);
		return toResponseDto(saveArticle);
	}

	@Transactional
	public List<ArticleResponseDto> getArticles() {

		return articleRepository.findAll()
				.stream()
				.map(this::toResponseDto)
				.collect(Collectors.toList());
	}

  @Transactional
	public ArticleResponseDto getArticleById(Long id)  {
		Article article = articleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));


		return toResponseDto(article);
	}


	@Transactional
	public void	deleteArticle(Long id)  {
		Article article = articleRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Article not found with id: " + id));
		s3Service.deleteFile(article.getS3Key());
		articleRepository.delete(article);
	}
	private ArticleResponseDto toResponseDto(Article article) {

		return ArticleResponseDto.builder()
				.id(article.getId())
				.title(article.getTitle())
				.content(article.getContent())
				.createdAt(article.getCreatedAt())
				.updatedAt(article.getUpdatedAt())
				.imageUrl(article.getImageUrl())
				.originalFileName(article.getOriginFileName())
				.build();
	}
}