import type { UserConfig } from "@commitlint/types";
import { RuleConfigSeverity } from "@commitlint/types";

/* Known `type-enum`s of writing this:
 *
 * build:
 *  Changes that affect the build system or external dependencies (example
 *  scopes: gulp, broccoli, npm)
 *
 * chore:
 *  Change to the build process or auxiliary libraries/tools
 *
 * ci:
 *  Changes to our CI configuration files and scripts (example scopes:
 *  Travis, Circle, BrowserStack, SauceLabs)
 *
 * docs:
 *  Documentation only changes
 *
 * feat:
 *  Introduces a new feature
 *
 * fix:
 *  Patches a bug
 *
 * perf:
 *  Performance improvement
 *
 * refactor:
 *  A code change that rewrites existing code to improve readability,
 *  reusability, or structure. It neither fixes a bug nor adds a feature.
 *
 * revert:
 *  Undo previous changes
 *
 * style:
 *  Changes that do not affect the meaning of the code
 *
 * test:
 *  Add missing tests or correcting existing tests
 */
const Configuration: UserConfig = {
  extends: ["@commitlint/config-conventional"],
  helpUrl: `https://github.com/conventional-changelog/commitlint/#what-is-commitlint

    Once you're ready to fix your commit message, run:

    git commit --edit --file $(git rev-parse --git-path COMMIT_EDITMSG)
  `,
  rules: {
    "subject-case": [
      RuleConfigSeverity.Error,
      "always",
      ["lower-case", "sentence-case"],
    ],
  },
};

module.exports = Configuration;
