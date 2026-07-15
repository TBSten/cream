#!/usr/bin/env python3
"""リリースノートの NG ワード検査・置換。

Usage:
    python3 ng_words.py <file.md>        # 検出のみ（NG があれば exit code 1）
    python3 ng_words.py <file.md> --fix  # 置換案があるものは自動置換して書き戻す

subagent / ユーザレビューで NG 表現の指摘があるたびに NG_WORDS に追加していく。
置換案が文脈依存で決められないものは None にして「手動で言い換え」として報告だけする。
"""
import sys

# NG 表現 -> 置換案（None = 文脈依存のため自動置換せず、手動で言い換える）
# 指摘があるたびに追加していく
NG_WORDS = {
    "fan out": None,  # 「子クラスそれぞれに copy 関数を生成する」等、動作で言う
    "fans out": None,
    "具象 leaf": "子クラス",
    "具象クラス": "子クラス",
    "leak": None,  # 「他の annotation の生成関数にも効いてしまう問題」等、現象で言う
    "subset シグネチャ": None,  # 「一部のプロパティだけを持つ関数」等
    "auto-copy default": None,  # 「自動で付くデフォルト値 (= this.プロパティ名)」
    "目玉": None,  # 「主要な変更」「ハイライト」等（表現として使わない）
}


def main() -> int:
    if len(sys.argv) < 2:
        print(__doc__)
        return 2
    path = sys.argv[1]
    fix = "--fix" in sys.argv[2:]

    with open(path, encoding="utf-8") as f:
        text = f.read()

    found = 0
    for word, replacement in NG_WORDS.items():
        if word not in text:
            continue
        for lineno, line in enumerate(text.splitlines(), 1):
            if word in line:
                found += 1
                if replacement is None:
                    action = "文脈に合わせて手動で言い換え"
                elif fix:
                    action = f"「{replacement}」に置換しました"
                else:
                    action = f"「{replacement}」に置換可（--fix で自動置換）"
                print(f"{path}:{lineno}: NG「{word}」 -> {action}")
        if fix and replacement is not None:
            text = text.replace(word, replacement)

    if fix:
        with open(path, "w", encoding="utf-8") as f:
            f.write(text)

    if found == 0:
        print(f"{path}: NG ワードなし")
    return 1 if found else 0


if __name__ == "__main__":
    sys.exit(main())
