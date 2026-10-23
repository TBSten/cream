[cream.kt](../README.md)

# Documentation

User-facing documentation: English, with a Japanese counterpart (`*.ja.md`) next to each page

markdown 付近の対応する `.md` ファイルも合わせて更新する。例えば `README.md` を更新したら `README.ja.md` も必ず更新する
（対になっていることは `has its Japanese counterpart` 制約が検査する）。

ただし `skills/` 配下は AI Agent 向けなので英語 1 本とし、`.ja.md` は作らない。SKILL.md と、そこから参照される `.md` が対象
（`has no Japanese counterpart` 制約が検査する）。

| Role | Summary |
|---|---|
| [ProjectReadme](./ProjectReadme.md) | The README shown on GitHub, in English and Japanese |
| [License](./License.md) | The MIT license text |
| [FeatureGuide](./FeatureGuide.md) | One page per feature or topic (copy, combine, sealed copy, comparison, ...), linked from the README |
| [CustomizationGuide](./CustomizationGuide.md) | One page per customization point (options, funName, KDoc, visibility, ...) |
| [UseCaseGuide](./UseCaseGuide.md) | A worked example of cream in real code; the snapshot tests reproduce it in `scenario/UseCases.kt` |
| [AiIntegrationGuide](./AiIntegrationGuide.md) | How to use cream from AI agents |
| [DistributedSkill](./DistributedSkill.md) | An agent skill users copy into their own projects. English only, since agents read it |
| [DocImage](./DocImage.md) | An image embedded in the documentation |
| [DesignDoc](./DesignDoc.md) | An internal design note, one per issue (`<issue number>-<topic>.md`) |
| [FeatureOptionMatrix](./FeatureOptionMatrix.md) | The internal reference of which option affects which feature: the Markdown source and its rendered HTML |

## Placement in this group

```
README.md                     ProjectReadme
README.ja.md                  ProjectReadme
LICENSE                       License
LICENSE.txt                   License
doc/
  *.md                        FeatureGuide
  customization/*.md          CustomizationGuide
  use-case/**/*.md            UseCaseGuide
  ai-integration/
    *.md                      AiIntegrationGuide
    skills/*/*.md             DistributedSkill
  *.png                       DocImage
  internal/
    design/*-*.md             DesignDoc
    feature-and-options.md    FeatureOptionMatrix
    feature-and-options.html  FeatureOptionMatrix
```
