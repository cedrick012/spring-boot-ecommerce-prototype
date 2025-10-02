# アプリケーション概要（ビジネス向け説明）

## 1. 目的・価値提案

本アプリケーションは、**小規模〜中規模のオンライン販売事業**を想定した、在庫を持つ商品カタログと**セッション単位のショッピングカート**を提供する**軽量 EC API**です。最小限の機能に絞ることで、短期間での導入・検証（PoC）や教育用途に適し、将来的なユーザー認証や支払い連携への拡張を前提としています。

## 2. 対象ユーザー / ユースケース

* **店舗担当者 / 事業オーナー**：商品登録・在庫管理の検証、販売フローの確認
* **開発チーム / 研修生**：API 設計・在庫整合・トランザクション設計・バリデーション等の習得
* **プロダクトマネージャー**：MVP レベルの体験確認、要件の精緻化

## 3. 提供機能（要約）

* **商品カタログ**：商品一覧・詳細参照（価格・在庫・説明）
* **カート管理（セッション方式）**

  * カート作成/取得（`HttpSession` を使用）
  * 商品追加（新規明細 or 既存明細の数量加算）
* **チェックアウト**：在庫検証後の在庫減算・カートクリア（失敗時はエラー集約）
* **エラーハンドリング**：一貫したエラーレスポンス（ステータス/メッセージ/詳細）

## 4. 主なエンドポイント（抜粋）

* **Products**

  * `GET /api/products`：商品一覧
  * `GET /api/products/{id}`：商品詳細
* **Carts**

  * `GET /api/carts/session`：セッションのカート取得（存在しなければ新規作成）
  * `POST /api/carts/session/add-product`：セッションのカートに商品追加
  * `POST /api/carts/{id}/add-product`：指定カートに商品追加
  * `DELETE /api/carts/{id}/checkout`：チェックアウト（在庫検証→在庫減算→カート消去）

> **テスト推奨**：フロント（HTML/JS）実装前に **Postman** で各 API を検証し、リクエスト/レスポンスとエラーケースを固めることで、フロント実装の手戻りを抑制します。

## 5. ドメインモデル（要点）

* **Product**：`id(UUID) / name / price / description / stock`

  * バリデーション：`name@NotBlank`, `price@Min(1)`, `stock@Min(0)`
* **Cart**：`id(UUID) / sessionId / createdAt / updatedAt / items(Set<CartItem>)`

  * `@CreationTimestamp/@UpdateTimestamp` により自動管理
* **CartItem**：`id(UUID) / product / quantity / cart`

  * 参照循環対策：`Cart.items @JsonManagedReference`、`CartItem.cart @JsonBackReference`
  * バリデーション：`quantity@Min(1)`

## 6. アーキテクチャ / 技術スタック

* **フレームワーク**：Spring Boot（Web / Validation / Data JPA）
* **永続化**：Spring Data JPA（H2/PostgreSQL/MySQL へ容易に切替可能）
* **シリアライゼーション**：Jackson（循環参照対策済み）
* **エラーハンドリング**：`@RestControllerAdvice` による共通化
* **構成拡張**：OpenAPI/Swagger、Flyway、Docker 等に発展可能

## 7. 主要業務フロー

* **商品閲覧**：WebUI → ProductController → ProductService → ProductRepository → 商品一覧/詳細を返却
* **カート追加**：WebUI → CartController → `HttpSession.getId()` 取得 → CartService

  * 既存カート検索（なければ新規作成）
  * 明細存在判定（あれば数量加算、なければ新規作成）
* **チェックアウト**：CartService

  * 全明細の在庫検証（不足があればエラーを集約して 400 応答）
  * 問題なければ在庫減算（ProductService 経由）→ 明細削除 → カート削除

## 8. 非機能要件（推奨）

* **信頼性**：在庫整合性の担保（同時更新対策は将来の要件に応じて悲観/楽観ロック検討）
* **可観測性**：アクセスログ、必要に応じてメトリクス（Micrometer/Prometheus）
* **保守性**：サービス層にユースケースを集約、例外と検証の共通化
* **拡張性**：ユーザー認証・決済連携・カートの永続化戦略変更への対応

## 9. 品質保証（テスト指針）

* **ユニットテスト**：

  * ProductService（`findById`/`reduceStock`）
  * CartService（`getOrCreateCartBySession`/`addProductToCart`/`checkout`）
* **Web テスト**：`@WebMvcTest` でステータス・JSON 形状を検証
* **データテスト**：`@DataJpaTest` でリポジトリのクエリ確認
* **Postman コレクション**：成功/失敗の例を保存し手動回帰を容易化

## 10. セキュリティ / 今後の拡張

* **現状**：セッション方式のカート、認証/認可は未実装（学習・PoC 前提）
* **拡張案**：

  * 認証（Basic/JWT）と**ユーザー紐づけカート**への移行
  * 支払いゲートウェイ（Stripe 等）連携に向けたチェックアウトの拡張
  * DB マイグレーション（Flyway）、本番 DB（PostgreSQL）対応
  * Docker 化と CI/CD 導入

## 11. 成功指標（例）

* API の 2xx/4xx の期待動作が Postman で再現できること
* 在庫不足時のエラーが**集約**され、ユーザーが修正可能なメッセージになること
* 単体/結合テストが安定し、基本フローの回帰に耐えること

---

**まとめ**：
本アプリは**「小さく作って確実に動かす」EC の最小構成**を提供します。Postman で API 品質を先に固めることで、静的 HTML やフロントエンド実装の着手後の手戻りを削減し、学習・検証・本番移行の各フェーズをスムーズに進められます。
