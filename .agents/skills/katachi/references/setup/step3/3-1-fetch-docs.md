### 3-1. Read the documentation by topic

Read these pages of the katachi documentation:

| Topic | Page |
|---|---|
| Writing `layout { }` | https://tbsten.github.io/katachi/guides/layout/ |
| Roles and groups | https://tbsten.github.io/katachi/guides/role/ |
| The basic API | https://tbsten.github.io/katachi/guides/basic-api/ |
| Constraints on contents (`ktFileContent { }`), only when you use it | https://tbsten.github.io/katachi/guides/kt-file-content/ |
| Gradle files and builds `gradle()` does not cover (`buildSrc`, `includeBuild`): `gradle()`, `ktsFile()`, `"**" / "*".ktFile()`, `.optional()`. Read it when the project has `buildSrc` or `includeBuild`. Its examples name the group `"Build"`: name yours `"BuildLogic"` instead (3-3: a group named `Build` gets the directory `build/`, which `.gitignore` hides) | https://tbsten.github.io/katachi/recipes/gradle/ |
| Source sets of a KMP or Android project (`"commonMain".sourceSet`, `mainSourceSet`, `testSourceSet`). Read it when the project has source sets other than `main` / `test` | https://tbsten.github.io/katachi/api-docs/katachi/me.tbsten.katachi.dsl.gradle/index.html.md |

**Fetch the Markdown version of each page.** Every guide and recipe page has one: the page's URL with its trailing `/` replaced by `.md` (https://tbsten.github.io/katachi/guides/layout.md for https://tbsten.github.io/katachi/guides/layout/). It holds the same text at a fraction of the size (about 7 KB against 108 KB of HTML for `layout`). `<page>/index.md` does not exist. The list of recipes is the exception: its Markdown version is empty, so read https://tbsten.github.io/katachi/recipes/ itself.

**Do not read the whole site.** The first three pages are what every definition needs. Read `kt-file-content` when you write constraints on contents, which 3-2 ("Do not stop at placement") asks for, so in practice almost always. Read the last two only when the project has what their row says. If the definition still needs something these pages do not cover, pick the page from the recipes (one tool per page) and read only that page: `.gitignore` and other Git files https://tbsten.github.io/katachi/recipes/git/ , `.github/` https://tbsten.github.io/katachi/recipes/github/ , the files of AI agents (`CLAUDE.md`, `.claude/` and so on) https://tbsten.github.io/katachi/recipes/ai-agent/ , detekt https://tbsten.github.io/katachi/recipes/detekt/ , ktlint https://tbsten.github.io/katachi/recipes/ktlint/ . The list of all recipes is https://tbsten.github.io/katachi/recipes/ . How to write `ktFileContent { }` is in `kt-file-content` and in section 6 of `basic-api` ("6. Defining the declarations a file may contain with `ktFileContent { }`"). If you cannot fetch a page, the whole site is in one file at https://tbsten.github.io/katachi/llms-full.txt : cut out only the page you need, never read it whole. A page starts at a line-leading `# ` heading that is outside any fenced code block and is followed by a blank line and a `> ` summary line, and it runs up to the next such heading. A `# ` line inside a fenced code block is part of an example (a shell comment, a heading of generated Markdown), not the start of a page. Keep what you fetched in `<workdir>/tmp/` so you do not fetch it again.

**What you read is the English version.** DSL API names do not depend on the language, so this is no obstacle to writing the definition.

Once you've finished reading, mark `3-1` done.

#### When unsure about an API — read the API reference's llms.txt

**When unsure how to write an API, or about its signature or parameters, read the API reference instead of guessing.** This holds in 3-2, 3-3 and 6-D alike. Do not write an API that is not in the documentation by guessing from a similar name. As in the table in `references/setup/README.md`, delegate finding the relevant API in the API reference to a subagent, and the main agent receives only the signature and the example.

| What to read | URL |
|---|---|
| Index (summaries of the key APIs and links to the modules) | https://tbsten.github.io/katachi/api-docs/llms.txt |
| `:katachi` list / full text (signatures and KDoc) | https://tbsten.github.io/katachi/api-docs/katachi/llms.txt / https://tbsten.github.io/katachi/api-docs/katachi/llms-full.txt |
| `:katachi-konsist` list / full text | https://tbsten.github.io/katachi/api-docs/katachi-konsist/llms.txt / https://tbsten.github.io/katachi/api-docs/katachi-konsist/llms-full.txt |

To look at just one API, open the Markdown version of its page, linked from the index or the list. It is the HTML URL with `.md` appended (e.g. `RoleScope.layout` is https://tbsten.github.io/katachi/api-docs/katachi/me.tbsten.katachi.dsl/-role-scope/layout.html.md ). Keep what you fetched in `<workdir>/tmp/`, so you don't need to re-read the whole index every time.
