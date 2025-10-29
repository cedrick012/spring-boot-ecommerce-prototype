# Spring Boot Eコマース マーケットプレイス - 包括的講演原稿

## イントロダクション（45秒）

「本チュートリアルへようこそ。本日は、Spring Boot Eコマース マーケットプレイスのデモンストレーションを通じて、完全なレイヤード アーキテクチャによるモダンなJavaエンタープライズ開発手法をご紹介いたします。」

## プロジェクト構造の概要（1分）

「プロジェクト構造を確認してみましょう。`src/main/java/com/example/marketplace`に移動いたします。レイヤード アーキテクチャにご注目ください。コントローラはREST エンドポイント、サービス インターフェースと実装はビジネス ロジック、エンティティはドメイン モデル、マッパー インターフェースとXMLはデータベース操作、バリデーション付きDTOはデータ転送、configはアプリケーション設定、エクセプションは集中的エラーハンドリングを担当しています。`pom.xml`では、MyBatis、バリデーション、H2データベースを含むSpring Boot 3.5.6の依存関係を定義しています。この分離により、保守性、テスト容易性、拡張性が確保されます。」

## エンティティレイヤー - ドメインモデル（1分）

「`src/main/java/com/example/marketplace/entity/Product.java`を開きますと、名前の`@NotBlank`や、価格と在庫の正の値を保証する`@Min`などのバリデーション アノテーションをご確認いただけます。`src/main/java/com/example/marketplace/entity/Cart.java`エンティティでは、セッション ベースのトラッキングによる双方向リレーションシップに`@JsonManagedReference`を使用しています。`src/main/java/com/example/marketplace/entity/CartItem.java`は`@JsonBackReference`でこのパターンを完成させ、JSONシリアライゼーション時の循環参照問題を防いでいます。」

## データアクセス レイヤー - MyBatis統合（1分）

「MyBatis統合では、JavaインターフェースとXMLマッパーの両方を使用しています。`src/main/java/com/example/marketplace/mapper/ProductMapper.java`インターフェースはメソッド契約を定義し、`src/main/resources/mappers/ProductMapper.xml`でクリーンなSQLとパラメータバインディングにより実装されています。`src/main/resources/mappers/CartItemMapper.xml`では、カート アイテムと商品データを結合する複雑なJOIN操作をご覧いただけます。`src/main/resources/mappers/CartMapper.xml`はカートの永続化を処理します。`src/main/resources/schema.sql`では、シーケンスと外部キーを含むデータベース構造を定義し、`src/main/resources/data.sql`では初期商品データを提供しています。MyBatisがオブジェクト リレーションシップの構築と型マッピングを自動的に処理いたします。」

## サービス レイヤー - インターフェース ベース設計（1分15秒）

「サービス レイヤーでは、依存性注入とテスト容易性のためにインターフェース ベース設計を採用しています。`src/main/java/com/example/marketplace/service/ProductService.java`は商品操作の契約を定義し、`src/main/java/com/example/marketplace/service/ProductServiceImpl.java`により実装されています。`reduceStock`メソッドは、数量の検証、商品の存在確認、在庫の可用性チェックを行います。同様に、`src/main/java/com/example/marketplace/service/CartService.java`インターフェースは`src/main/java/com/example/marketplace/service/CartServiceImpl.java`により実装されています。`addProductToCart`は在庫検証付きで新規アイテムと数量更新を処理し、`checkout`は処理前に全てのアイテムを検証することで、トランザクションの整合性を保証しています。」

## コントローラ レイヤー - REST API（1分）

「`src/main/java/com/example/marketplace/controller/ProductController.java`は、商品操作用の適切なHTTPステータス コードを伴う標準的なREST エンドポイントを提供しています。`src/main/java/com/example/marketplace/controller/CartController.java`では、`HttpSession`を使用したセッション ベースのカート管理を実装し、認証なしでユーザー セッション毎にカートを自動作成しています。`src/main/java/com/example/marketplace/controller/HomeController.java`は、適切なWebアプリケーション動作のためのルートパス リダイレクトとfaviconリクエストを処理しています。」

## 例外処理とDTO（45秒）

「`src/main/java/com/example/marketplace/exception/GlobalExceptionHandler.java`では、`@RestControllerAdvice`を使用した集中的エラー処理により、カスタム例外`src/main/java/com/example/marketplace/exception/NotFoundException.java`を処理し、適切なHTTPステータス コードを伴う一貫した`src/main/java/com/example/marketplace/dto/ErrorResponse.java`を返却しています。DTOには、バリデーション アノテーション付きの`src/main/java/com/example/marketplace/dto/AddToCartRequest.java`と構造化レスポンス データ用の`src/main/java/com/example/marketplace/dto/CheckoutResult.java`が含まれています。」

## 設定とフロントエンド統合（1分）

「`src/main/java/com/example/marketplace/config/WebConfig.java`では、フロントエンド・バックエンド間通信を可能にするCORSマッピングを設定しています。`src/main/resources/application.properties`では、H2データベース接続、ログレベル、MyBatis設定を構成しています。フロントエンドには、ユーザー インターフェース用の`src/main/resources/static/index.html`、API呼び出しとセッション管理を処理するMarketplaceAppクラスを含む`src/main/resources/static/app.js`、そしてレスポンシブ デザインとモダンなスタイリングを提供する`src/main/resources/static/styles.css`が含まれています。フロントエンドは、設定されたCORSポリシーを通じてセッション ベースのバックエンドとシームレスに統合されています。」

## アプリケーション デモンストレーション（1分）

「`src/main/java/com/example/marketplace/MarketplaceApplication.java`を右クリックし、'Run As Spring Boot App'を選択いたします。H2データベースがスキーマとサンプルデータで初期化されます。デバッグ ログが有効になった状態でサーバーがポート8080で起動いたします。`localhost:8080`を開きますと、レスポンシブ デザインによる商品カタログが表示されます。リアルタイムのカート更新、在庫検証、セッション管理をデモンストレーションするため、アイテムを追加いたします。」

## 主要機能の要約（45秒）

「このアプリケーションでは、以下のエンタープライズ プラクティスをデモンストレーションしています：テスト容易性のためのインターフェース ベース サービス設計、カスタムDTOによるBean Validation、カスタム例外による集中的例外処理、原子的在庫検証による在庫管理、認証なしのセッション ベース状態管理、CORS対応REST API設計、インターフェースとXMLマッパー両方による完全なMyBatis統合、そしてスキーマとデータファイルによる適切なデータベース初期化です。」

## まとめ（30秒）

「このSpring Bootマーケットプレイスでは、拡張可能で保守しやすいアーキテクチャによる包括的な開発プラクティスをご紹介いたしました。インターフェース ベース設計、適切なバリデーション、完全な関心の分離により、認証、決済、マイクロサービス、高度な在庫機能の拡張に適しています。ご清聴ありがとうございました。」