# 🚀 クイックスタートガイド

## 📦 すぐに始める

### 1. アプリケーション起動
```bash
cd SpringBootTaskManager
mvn spring-boot:run
```

### 2. Webブラウザでアクセス
- **タスク管理画面**: http://localhost:8080
- **データベース管理**: http://localhost:8080/h2-console

### 3. 基本操作

#### ✅ タスク作成
1. 「新しいタスクを作成」セクションでタイトルを入力
2. 優先度、カテゴリ、期限などを設定
3. 「タスクを作成」ボタンをクリック

#### 🔍 タスク検索
1. 検索ボックスにキーワードを入力
2. フィルター（状態、優先度、カテゴリ）を設定
3. リアルタイムで結果が表示

#### ✏️ タスク編集
1. タスクカードの「編集」ボタンをクリック
2. モーダルウィンドウで情報を変更
3. 「保存」ボタンで更新

#### ✅ 完了切り替え
- タスクカードの「完了」ボタンをワンクリック

## 🛠 開発者向け

### APIテスト
```bash
# すべてのタスクを取得
curl http://localhost:8080/api/tasks

# 新しいタスクを作成
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{"title":"テストタスク","priority":"HIGH"}'

# 統計情報を取得
curl http://localhost:8080/api/stats
```

### データベース確認
1. http://localhost:8080/h2-console にアクセス
2. 接続情報：
   - JDBC URL: `jdbc:h2:mem:taskdb`
   - ユーザー名: `sa`
   - パスワード: (空白)

### ホットリロード
- ソースコード変更時、DevToolsが自動再起動
- ブラウザのリロードで変更が反映

## 📱 モバイル対応

- レスポンシブデザインでスマートフォン・タブレット対応
- タッチ操作に最適化されたUI

## 🎯 サンプルデータ

初期起動時に以下のサンプルタスクが自動登録されます：
- プロジェクト企画書作成（高優先度、仕事）
- 買い物リスト作成（中優先度、個人）
- 会議資料準備（完了済み、仕事）
- ジムでトレーニング（中優先度、健康）
- 本を読む（低優先度、学習）

## 🔄 リセット方法

### データベースリセット
```bash
# アプリケーション再起動でデータリセット
mvn spring-boot:run
```

### 完全なプロジェクトリセット
```bash
mvn clean
mvn spring-boot:run
```

## ❓ トラブルシューティング

### ポート8080が使用中の場合
```bash
# 別のポートで起動
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Javaバージョンエラー
- Java 17以上がインストールされているか確認
```bash
java -version
```

### Maven依存関係エラー
```bash
mvn clean install -U
```

## 📚 詳細情報

完全なドキュメントは `README.md` を参照してください。 