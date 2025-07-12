# Spring Boot タスク管理アプリケーション

Spring Bootフレームワークを使用したモダンなタスク管理Webアプリケーションです。REST API、データベース永続化、レスポンシブWebUIを提供します。

## 🚀 機能

### ✨ 主要機能
- **タスクのCRUD操作**（作成、読み取り、更新、削除）
- **高度な検索機能**（キーワード検索、フィルター機能）
- **優先度管理**（高、中、低）
- **カテゴリ分類**（仕事、個人、学習、健康など）
- **期限管理**（期限切れ、今日期限の表示）
- **タグ機能**（複数タグでの分類）
- **統計情報表示**（完了率、カテゴリ別集計など）

### 🛠 技術的特徴
- **RESTful API**（JSON形式でのデータ交換）
- **データベース永続化**（H2インメモリDB + JPA/Hibernate）
- **入力検証**（Bean Validation）
- **レスポンシブデザイン**（モバイル対応）
- **リアルタイム検索**（Ajax通信）

## 📋 技術スタック

### バックエンド
- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Web (REST API)
  - Spring Data JPA (データアクセス)
  - Spring Boot Validation (入力検証)
  - Spring Boot DevTools (開発支援)
- **H2 Database** (開発用インメモリDB)
- **Maven** (ビルドツール)

### フロントエンド
- **HTML5** / **CSS3** / **JavaScript (ES6+)**
- **Font Awesome** (アイコン)
- **レスポンシブデザイン** (CSS Grid/Flexbox)

## 🔧 インストールと実行

### 必要な環境
- Java 17以上
- Maven 3.6以上

### クローンとビルド
```bash
# リポジトリのクローン
git clone <repository-url>
cd SpringBootTaskManager

# 依存関係のインストールとビルド
mvn clean install

# アプリケーションの起動
mvn spring-boot:run
```

### 直接実行（jarファイル）
```bash
# jarファイルの作成
mvn clean package

# アプリケーションの実行
java -jar target/spring-boot-task-manager-1.0.0.jar
```

### アクセス方法
- **Webアプリケーション**: http://localhost:8080
- **H2 データベースコンソール**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - ユーザー名: `sa`
  - パスワード: (空白)

## 📚 API リファレンス

### タスク管理 API

#### すべてのタスクを取得
```http
GET /api/tasks
```

#### フィルター検索
```http
GET /api/tasks?status=incomplete&priority=HIGH&category=仕事
```

#### キーワード検索
```http
GET /api/tasks?search=プロジェクト
```

#### タスク詳細取得
```http
GET /api/tasks/{id}
```

#### タスク作成
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "新しいタスク",
  "description": "タスクの詳細説明",
  "priority": "HIGH",
  "category": "仕事",
  "tags": "重要,緊急",
  "dueDate": "2024-01-15T10:00:00"
}
```

#### タスク更新
```http
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "更新されたタスク",
  "completed": true
}
```

#### タスク削除
```http
DELETE /api/tasks/{id}
```

#### 完了状態切り替え
```http
PATCH /api/tasks/{id}/toggle
```

### 統計情報 API

#### 統計情報取得
```http
GET /api/stats
```

### カテゴリ API

#### すべてのカテゴリ取得
```http
GET /api/categories
```

## 🗄 データベーススキーマ

### tasksテーブル
| カラム名    | データ型      | 説明           |
|-------------|---------------|----------------|
| id          | BIGINT        | 主キー（自動生成） |
| title       | VARCHAR(200)  | タスクタイトル    |
| description | VARCHAR(1000) | タスク説明       |
| completed   | BOOLEAN       | 完了フラグ       |
| priority    | VARCHAR(10)   | 優先度（HIGH/MEDIUM/LOW） |
| category    | VARCHAR(100)  | カテゴリ        |
| tags        | VARCHAR(500)  | タグ（カンマ区切り） |
| created_at  | TIMESTAMP     | 作成日時        |
| updated_at  | TIMESTAMP     | 更新日時        |
| due_date    | TIMESTAMP     | 期限           |

## 🎨 ユーザーインターフェース

### 主要画面
1. **ダッシュボード** - 統計情報の表示
2. **タスク作成フォーム** - 新規タスクの登録
3. **検索・フィルター** - 高度な検索機能
4. **タスク一覧** - タスクの表示・編集・削除
5. **編集モーダル** - タスクの詳細編集

### UI特徴
- **レスポンシブデザイン** (モバイル・タブレット対応)
- **直感的な操作** (ワンクリック完了切り替え)
- **リアルタイム検索** (入力と同時に検索結果を更新)
- **カラーコード表示** (優先度・状態別の色分け)
- **アニメーション効果** (スムーズな画面遷移)

## 🛠 開発・カスタマイズ

### 開発モード起動
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### カスタマイズポイント
1. **データベース変更** - `application.yml`でMySQL/PostgreSQLに変更可能
2. **認証機能追加** - Spring Securityの組み込み
3. **ファイル添付機能** - MultipartFileを使用したファイルアップロード
4. **通知機能** - WebSocketを使用したリアルタイム通知
5. **ダークテーマ** - CSS変数を使用したテーマ切り替え

### 設定ファイル
- `src/main/resources/application.yml` - アプリケーション設定
- `src/main/resources/data.sql` - 初期データ
- `src/main/resources/static/` - フロントエンドリソース

## 🧪 テスト

### 単体テスト実行
```bash
mvn test
```

### 統合テスト実行
```bash
mvn integration-test
```

## 📦 本番環境デプロイ

### プロファイル設定
```bash
# 本番環境用設定ファイル作成
cp src/main/resources/application.yml src/main/resources/application-prod.yml

# 本番環境での起動
java -jar target/spring-boot-task-manager-1.0.0.jar --spring.profiles.active=prod
```

### Docker化
```dockerfile
FROM openjdk:17-jre-slim
COPY target/spring-boot-task-manager-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 コントリビューション

1. Forkプロジェクト
2. 機能ブランチを作成 (`git checkout -b feature/amazing-feature`)
3. 変更をコミット (`git commit -m 'Add amazing feature'`)
4. ブランチにプッシュ (`git push origin feature/amazing-feature`)
5. Pull Requestを作成

## 📄 ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 🔗 関連リンク

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [H2 Database](https://www.h2database.com/)
- [Maven](https://maven.apache.org/)

## 📞 サポート

問題や質問がある場合は、GitHubのIssuesページまでお気軽にお問い合わせください。 