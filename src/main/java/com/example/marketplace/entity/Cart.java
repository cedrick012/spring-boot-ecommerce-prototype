package com.example.marketplace.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ショッピングカート情報を管理するドメインモデルです。
 * セッション単位でカート明細を保持し、商品の一時的な保存を行います。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
	/** カートID */
	private Long id;
	
	/** セッションID（ユーザーセッションとの関連付け） */
	@NotBlank(message = "セッションIDは空欄にできません。")
	private String sessionId;
	
	/** カート作成日時 */
	private LocalDateTime createdAt;
	
	/** カート更新日時 */
	private LocalDateTime updatedAt;
	
	/** カート明細一覧 */
	@JsonManagedReference
    private Set<CartItem> items = new HashSet<>();
}
