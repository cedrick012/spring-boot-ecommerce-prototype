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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
	private Long id;
	
	@NotBlank(message = "セッションIDは空欄にできません。")
	private String sessionId;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	@JsonManagedReference
    private Set<CartItem> items = new HashSet<>();
}
