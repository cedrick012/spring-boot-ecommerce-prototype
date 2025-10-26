package com.example.marketplace.exception;

/**
 * リソースが見つからない場合にスローされる例外です。
 * 商品やカートなどの検索時に該当データが存在しない場合に使用します。
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * エラーメッセージを指定してNotFoundException を生成します。
	 *
	 * @param message エラーメッセージ
	 */
	public NotFoundException(String message) {
		super(message);
	}
}
