# Spring Boot Eコマース マーケットプレイス - 講演原稿（簡潔版）

## イントロダクション（45秒）

「本チュートリアルへようこそ。本日は、Spring Boot Eコマース マーケットプレイスのデモンストレーションを通じて、モダンなJavaエンタープライズ開発手法をご紹介いたします。」

## プロジェクト構造の概要（45秒）

「Eclipseプロジェクトの構造を確認してみましょう。`src/main/java/com/example/marketplace`に移動いたします。レイヤード アーキテクチャにご注目ください。コントローラはREST エンドポイント、サービスはビジネス ロジック、エンティティはドメイン モデル、マッパーはデータベース操作、DTOはデータ転送、エクセプションはエラーハンドリングを担当しています。この分離により、保守性と拡張性が確保されます。」

## エンティティレイヤー - ドメインモデル（1分）

「`src/main/java/com/example/marketplace/entity/Product.java`を開きますと、名前の`@NotBlank`や、価格と在庫の正の値を保証する`@Min`などのバリデーション アノテーションをご確認いただけます。`src/main/java/com/example/marketplace/entity/Cart.java`エンティティでは、双方向リレーションシップに`@JsonManagedReference`を使用しています。`src/main/java/com/example/marketplace/entity/CartItem.java`は`@JsonBackReference`でこのパターンを完成させ、JSONシリアライゼーション時の循環参照問題を防いでいます。」

## データアクセス レイヤー - MyBatis統合（45秒）

「`src/main/resources/mappers/ProductMapper.xml`では、ハッシュ記法を使用したパラメータバインディングによる、クリーンなSQLを実装しています。`src/main/resources/mappers/CartItemMapper.xml`では、カート アイテムと商品データを結合する複雑なJOIN操作をご覧いただけます。MyBatisがオブジェクト リレーションシップの構築を自動的に処理いたします。」

## サービス レイヤー - ビジネス ロジック（1分）

「`src/main/java/com/example/marketplace/service/ProductServiceImpl.java`の`reduceStock`メソッドは、数量の検証、商品の存在確認、在庫の可用性チェックを行います。`src/main/java/com/example/marketplace/service/CartServiceImpl.java`では、`addProductToCart`が在庫検証付きで新規アイテムと数量更新を処理いたします。`checkout`メソッドは、処理前に全てのアイテムを検証することで、トランザクションの整合性を保証しています。」

## コントローラ レイヤー - REST API（45秒）

「`src/main/java/com/example/marketplace/controller/ProductController.java`は、適切なHTTPステータス コードを伴う標準的なREST エンドポイントを提供しています。`src/main/java/com/example/marketplace/controller/CartController.java`では、`HttpSession`を使用したセッション ベースのカート管理を実装し、認証を必要とすることなく、ユーザー セッション毎にカートを自動作成しています。」

## 例外処理（30秒）

「`src/main/java/com/example/marketplace/exception/GlobalExceptionHandler.java`では、`@RestControllerAdvice`を使用した集中的エラー処理により、適切なHTTPステータス コードを伴う一貫したエラー レスポンスを返却しています。」

## フロントエンド統合（45秒）

「`src/main/resources/static/index.html`がユーザー インターフェースを提供し、`src/main/resources/static/app.js`には、API呼び出しとセッション管理を処理するMarketplaceAppクラスが含まれています。フロントエンドはセッション ベースのバックエンドとシームレスに統合されています。」

## アプリケーション デモンストレーション（1分）

「`src/main/java/com/example/marketplace/MarketplaceApplication.java`を右クリックし、'Run As Spring Boot App'を選択いたします。サーバーがポート8080で起動いたします。`localhost:8080`を開きますと、商品カタログが表示されます。リアルタイムのカート更新と在庫検証をデモンストレーションするため、アイテムを追加いたします。」

## 主要機能の要約（30秒）

「このアプリケーションでは、以下のエンタープライズ プラクティスをデモンストレーションしています：入力チェックのためのBean Validation、集中的エラー処理、在庫検証による在庫管理、セッション ベースの状態管理、標準的なREST API設計、そしてMyBatisデータベース統合です。」

## まとめ（30秒）

「このSpring Bootマーケットプレイスでは、認証、決済、高度な在庫機能の拡張に適した拡張可能なアーキテクチャによる開発プラクティスをご紹介いたしました。ご清聴ありがとうございました。」