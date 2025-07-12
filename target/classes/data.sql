-- サンプルタスクデータの投入
INSERT INTO tasks (title, description, completed, priority, category, tags, created_at, updated_at, due_date) VALUES
('プロジェクト企画書作成', '新プロジェクトの企画書を作成する', false, 'HIGH', '仕事', 'プロジェクト,企画書', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 3, CURRENT_TIMESTAMP)),
('買い物リスト作成', '週末の買い物リストを作成', false, 'MEDIUM', '個人', '買い物,リスト', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
('会議資料準備', '来週の会議用資料を準備する', true, 'HIGH', '仕事', '会議,資料', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
('ジムでトレーニング', '週3回のジムトレーニング', false, 'MEDIUM', '健康', 'ジム,運動', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('本を読む', 'Java関連の技術書を読む', false, 'LOW', '学習', '読書,Java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 7, CURRENT_TIMESTAMP)),
('歯医者の予約', '定期検診の予約を取る', false, 'MEDIUM', '健康', '歯医者,予約', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 2, CURRENT_TIMESTAMP)),
('コードレビュー', 'チームメンバーのコードをレビューする', true, 'HIGH', '仕事', 'コードレビュー,チームワーク', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
('掃除', '部屋の掃除をする', false, 'LOW', '家事', '掃除,整理', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 0, CURRENT_TIMESTAMP)),
('英語の勉強', '英語の文法を復習する', false, 'MEDIUM', '学習', '英語,文法', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 5, CURRENT_TIMESTAMP)),
('友人との食事', '大学時代の友人と食事する', false, 'LOW', '個人', '友人,食事', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, DATEADD('DAY', 10, CURRENT_TIMESTAMP)); 