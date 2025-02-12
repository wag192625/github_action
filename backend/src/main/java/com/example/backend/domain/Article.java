package com.example.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "articles")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Article {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 1000)
	private String content;

	// S3 객체의 접근 URL
	@Column(nullable = true)
	private String imageUrl;

	// S3 객체의 키(식별자)
	// 버킷 내 객체를 구분하기 위해 필요
	// 객체 삭제에 활용되는 필드
	// uuid_filename.jpg
	@Column(nullable = true)
	private String s3Key;

	// 업로드 파일의 원본 파일명
	// 화면 출력용
	@Column(nullable = true )
	private String originFileName;

	@CreatedDate
	@Column(nullable = true, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(nullable = true)
	private LocalDateTime updatedAt;
}