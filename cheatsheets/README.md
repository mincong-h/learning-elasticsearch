# Cheatsheets

Elasticsearch-related cheatsheets to be used with [cheat](https://github.com/cheat/cheat).

## Install

You need to install `cheat` and add this directory as cheatsheets.

MacOS:

```
brew install cheat
```

Add this path to your `cheat` configuration (`~/.config/cheat/conf.yml`):

```diff
  cheatpaths:
    - name: community                   # a name for the cheatpath
      path: ~/documents/cheat/community # the path's location on the filesystem
      tags: [ community ]               # these tags will be applied to all sheets on the path
      readonly: true                    # if true, `cheat` will not create new cheatsheets here

+   - name: elasticsearch
+     path: /path/to/learning-elasticsearch/cheatsheets
+     tags: [ elasticsearch ]
+     readonly: true
+
    - name: personal
      path: ~/documents/cheat/personal  # this is a separate directory and repository than above
      tags: [ personal ]
      readonly: false                   # new sheets may be written here
```
