# Contributing to crypto-trading-sdk

## Getting Started

You don’t need Spring or any additional frameworks to run the project.
Simply add your API key from your trading platform and start using the SDK.

## Code Style

### Google Code Formatter

This project uses the **Google Java Formatter**.
Please format your code before committing.

### Avoid deep nested method calls

Avoid writing constructs such as:

```
method(anotherMethod(method1(method2())));
```

Keep the flow readable. Use intermediate variables or extract logic into separate methods.

### Javadoc Requirements

Interfaces and classes that contain important decision-making logic, non-obvious behaviour, or domain-specific details **must be documented using Javadoc**.
Explain *why* something works the way it does, not just *what* it does.
This helps contributors understand the intent behind critical mechanisms in the SDK.

### Versioning

Project versions are maintained in the `<properties>` section of the `pom.xml`.

## Branch Rules

### For MEAI developers

* `feat/[PROJECT_ABBR]-[TASK_NUMBER]`
* `bug/[PROJECT_ABBR]-[TASK_NUMBER]`

Where:
- `PROJECT_ABBR` is an abbreviation of the project you work in
- `TASK_NUMBER` is a number of task you work on

Only the branch type and the task key. No descriptions or extra text.

### For external contributors

* `issue/#12`

No words or descriptions in the branch name.

## Commit Message Rules

All commits must begin with a task identifier:

* `ISSUE-12: ...` — when referencing a GitHub issue
* `[PROJECT_ABBR]-[TASK_NUMBER]: ...` — for internal tasks not tied to an issue

## Pull Requests

1. Branch names must follow the rules above.
2. Code must be formatted using the Google Formatter.
3. PR titles must contain the same identifier used in commits.
4. Code must build successfully and pass all tests.
5. Add or update Javadoc when modifying or introducing important logic.

## Issues

External contributors must create an issue before submitting a PR.

## License

All contributions are submitted under the project’s existing license.
