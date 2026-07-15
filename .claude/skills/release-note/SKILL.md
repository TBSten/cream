---
name: release-note
description: 'cream のリリースノート・リリース準備資料を作成する。前バージョンからの変更を git log / merged PR から収集し、.local/v<version>-release-note/ に 3 ファイル構成（all.md=全変更リスト / highlight.md=ヘッドライン級のみ / release-note.md=GitHub Release に貼る英日併記本文）でまとめ、バージョン bump〜publish 確認までのリリース手順を案内する。Use when: "release note を書きたい", "リリースノート", "リリース準備", "次バージョンの変更をまとめて", "release notes for the next version".'
---

# release-note

cream のリリース準備資料を作るスキル。成果物は `.local/v<version>-release-note/` の 4 ファイル。

**実例は v0.9.0-alpha01 以降の公開済み release note を参考にする**（形式の正は常に公開版）:

```bash
gh release list --repo TBSten/cream --limit 10                          # 参考にできるリリース一覧
gh release view v0.9.0-alpha01 --repo TBSten/cream --json body --jq .body  # 公開版の本文を取得
```

`.local/v0.9.0-alpha01/` や `.local/v0.9.0-alpha02-release-note/` が残っていれば、
`all.md` / `highlight.md`（公開されない内部資料）の実例としてあわせて参照する
（`.local/` は gitignored なので無い環境もある — その場合は公開版 release note だけで形式を再現できる）。

成果物の 4 ファイル:

| ファイル | 内容 | 言語 |
|---|---|---|
| `all.md` | 全変更リスト（PR / issue 対応表） | 日本語 |
| `highlight.md` | ヘッドライン級の変更**のみ** | 日本語 |
| `release-note.md` | **GitHub Release の body にそのまま貼る本文** | 英語 + `<details>` 内に日本語全訳 |
| `README.md` | インデックス + リリース手順メモ + PR 出典表 | 日本語 |

## Step 1 — 変更の収集

1. `git fetch origin --tags` して `git log --pretty='%h %s' v<prev>..origin/main` で範囲を確定
   （shell の git が wrapper の場合は plumbing の `/opt/homebrew/bin/git` を使う。merge commit が
   porcelain から隠れることがある）。
2. `git log --merges` で merged PR 一覧を取り、各 PR は `gh pr view <n> --json title,body,mergedAt`
   で本文まで読む（分類・概要の根拠は PR 本文）。PR を経ない直接コミットも拾う。
3. 前回リリースの公開形式を必ず取得して手本にする:
   `gh release view v<prev> --json body`。**ローカルの下書きではなく公開版**を見る（構成・
   トグル・見出しレベルの正はそっち）。

## Step 2 — all.md（全変更リスト）

- ヘッダ: 比較範囲（commits 数 / PR 数 / main の SHA）・期間・Full Changelog リンク。
- 冒頭に「このリリースでの cream-runtime の変更ファイル」注記（新規ファイル有無は利用者の関心事）。
- セクション: ⚠️ 破壊的変更 → 🎉 新機能 → 🐛 修正（コード生成 / 診断 / CI などに小分け）→
  📚 ドキュメント → 🔧 そのほか（テスト / リファクタ）→ 🚧 Known Issues。
- 表形式 `| 内容 | PR | issue | 概要 |`。概要は PR 本文と突き合わせた事実のみ書く。

## Step 3 — highlight.md（選定ポリシーが本体）

- **ヘッドライン級の機能変更だけ**を載せる。判定基準:
  「利用者の書くコードが変わる / 新しくできることが増える変更か?」
- **載せないもの**: CI 修正・ドキュメント再構成・気づかれにくい挙動修正・Known Issue・内部
  リファクタ。これらは all.md と release-note.md の該当セクションに置く（消すのではなく分ける）。
- **「目玉」という表現を使わない**（「主要な変更」「ハイライト」等にする）。
- 各項目: 見出し（番号 + 一言サマリ）→ 背景 1 段落 → コード例（`// 生成イメージ:`）→ 補足 bullet。
- 冒頭に `all.md` への相互リンク。

## Step 4 — release-note.md（GitHub Release 本文）

構成（v0.9.0-alpha01 公開版の形式。逸脱するなら理由があるときだけ）:

```
（英語 intro — 控えめな 1〜2 文。alpha なら "pre-release for early testing" の一文）

# ✨ Highlights
## ⭐️⭐️⭐️ <最重要の変更>（破壊的変更なら見出しに ⚠️、本文に **Migration:** 段落）
## ⭐️⭐️☆ <変更>
## ⭐️☆☆ Other fixes & improvements   ← 細かい修正は独立セクションにせず bullet + 末尾 "- etc..."

# 🚧 Known Issues                      ← あるときだけ。issue リンク + 回避策

<details>

<summary> 日本語 </summary>

（上記全セクションの日本語全訳。見出しは ## レベル）

</details>

# 📝 What's Changed
* <GitHub の auto-generated PR list をそのまま>

**Full Changelog**: https://github.com/TBSten/cream/compare/v<prev>...v<version>
```

- ⭐️ は**辛口の絶対評価**: ⭐️⭐️⭐️ は**せいぜい 2〜3 個がマックス**、迷ったら ⭐️☆☆。
  実例: v0.9.0-alpha02 は Via 再設計（破壊的変更 + 致命バグ修正）のみ ⭐️⭐️⭐️ で、
  新機能 2 件（CopyToChildren.Map / excludes）も ⭐️☆☆（ユーザー判断）。
- Known Issues は英日とも **1 行の簡潔な記述 + issue リンク**に留める（詳細・回避策は issue 側に書く）。
- `Other fixes & improvements` に載せるのは**生成コードの挙動に関わる変更だけ**。ドキュメント
  再構成・CI・テスト基盤などは本文に書かず `# 📝 What's Changed` の PR リストに任せる
  （all.md には全部載せる — 本文から消すのではなく置き場所を分ける）。
- コード例のコメント: 英語側 `// ⭐️ will generate` / 日本語側 `// ⭐️ 生成されるコード`。
- Docs リンク: 英語側は `doc/*.md`、日本語側は `doc/*.ja.md` の GitHub URL。
  **リンク先ファイルの実在を ls で確認**してから入れる。
- `<summary>` の直後は空行（無いと GitHub が中の markdown をレンダリングしない）。
- intro は控えめなトーン（誇張しない。何を直したか・何が増えたかを淡々と）。
- **内部用語を使わない**: 「fan out」「具象 leaf」「subset シグネチャ」等の実装者向け語彙は
  利用者に通じない。「子クラスそれぞれに copy 関数を生成する」のように利用者の言葉へ言い換える。
  実装機構の説明（なぜ直ったか等）はリリースノートに書かず all.md / issue / PR に置く。

## Step 5 — セルフレビュー（NG ワードチェック + subagent、必須）

release-note.md が書き上がったら、まず **NG ワードの機械チェック**を実行する:

```bash
python3 .claude/skills/release-note/scripts/ng_words.py <path>/release-note.md        # 検出
python3 .claude/skills/release-note/scripts/ng_words.py <path>/release-note.md --fix  # 置換案があるものを自動置換
```

置換案が None のワード（fan out / leak 等）は文脈に合わせて手動で言い換える。
`all.md` / `highlight.md` にもかけてよい（内部資料なので必須ではない）。

次に、**習熟度の低いユーザ目線でも理解できる表現になっているかを subagent に
レビューさせる**。Agent tool で次の観点を渡す:

- cream を初めて（または浅く）使うユーザとして release-note.md を読み、
  **意味が取れない用語・文・前提知識を要求する説明**をすべて列挙する
- 内部用語・実装者語彙（例:「fan out」「具象 leaf」「subset シグネチャ」「委譲呼び出しの構築」）が
  残っていないか
- コード例だけを見て「自分に関係ある変更か」を判断できるか
- 英語セクションと日本語セクションで難易度・情報量に差がないか

指摘を反映してから次の Step へ進む（反映後に大きく書き換えた場合はもう 1 周レビュー）。
レビューで **新たな NG 表現が見つかったら `scripts/ng_words.py` の `NG_WORDS` に追加**して蓄積する。
ただし指摘の反映は加算バイアスに注意: 「説明を足す」系の指摘は本文を膨らませるので、
言い換え（同分量）で解決できないか先に検討する。

## Step 6 — README.md とリリース手順

README.md はインデックス（3 ファイルへのリンク + PR 出典表）+ 以下の手順メモ:

1. Known Issues に載せた issue をこのリリースで直すか判断（直すなら該当節を Bug Fixes へ書き換え）
2. `gradle/libs.versions.toml` の `cream = "<version>"` を bump（1 行コミット）
3. tag `v<version>` で GitHub Release 作成（alpha は pre-release）。body には
   **release-note.md だけ**を貼る（README のメタ情報・付録は貼らない）
4. publish workflow（`published` トリガー）が自動発火して Maven Central に上がることを確認。
   発火しなければ `workflow_dispatch` で手動 publish

## チェックリスト

- [ ] 全 merged PR + 直接コミットが all.md に載っている（範囲の commit 数と突き合わせ）
- [ ] highlight.md にヘッドライン級以外が混ざっていない
- [ ] release-note.md: 日本語トグルが英語側の全セクションを網羅している
- [ ] Docs リンク（en/ja とも）の実在確認済み
- [ ] What's Changed の PR list と Full Changelog の compare リンクが正しい tag を指す
- [ ] 破壊的変更に Migration 手順がある
- [ ] subagent による初見ユーザ目線レビュー（Step 5）を実施し、指摘を反映した
