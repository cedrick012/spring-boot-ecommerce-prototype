package com.example.marketplace.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * カートの精算処理結果を表すデータクラスです。
 * 成功/失敗の状態とメッセージ、エラー詳細を保持します。
 */
@Data
@AllArgsConstructor
public class CheckoutResult {
    /** 精算処理の成功/失敗フラグ */
    private boolean success;
    
    /** 処理結果メッセージ */
    private String message;
    
    /** エラー詳細リスト（失敗時のみ） */
    private List<String> errors;
    
    /**
     * 成功結果を生成します。
     *
     * @param message 成功メッセージ
     * @return 成功を表すCheckoutResult
     */
    public static CheckoutResult success(String message) {
        return new CheckoutResult(true, message, null);
    }
    
    /**
     * エラー詳細付きの失敗結果を生成します。
     *
     * @param message 失敗メッセージ
     * @param errors エラー詳細リスト
     * @return 失敗を表すCheckoutResult
     */
    public static CheckoutResult failure(String message, List<String> errors) {
        return new CheckoutResult(false, message, errors);
    }
    
    /**
     * 失敗結果を生成します。
     *
     * @param message 失敗メッセージ
     * @return 失敗を表すCheckoutResult
     */
    public static CheckoutResult failure(String message) {
        return new CheckoutResult(false, message, null);
    }
}
