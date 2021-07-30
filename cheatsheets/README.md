# Cheatsheets

Elasticsearch-related cheatsheets to be used with [`cheat`](https://github.com/cheat/cheat).

## Install

You need to install `cheat` and add this directory as cheatsheets.

MacOS:

```
brew install cheat
```

Use `cheat` and let it guide you for the initialization:

```
$ cheat
A config file was not found. Would you like to create one now? [Y/n]: Y
Would you like to download the community cheatsheets? [Y/n]: Y
Cloning into '/Users/mincong.huang/.config/cheat/cheatsheets/community'...
remote: Enumerating objects: 823, done.
remote: Counting objects: 100% (119/119), done.
remote: Compressing objects: 100% (89/89), done.
remote: Total 823 (delta 47), reused 71 (delta 30), pack-reused 704
Receiving objects: 100% (823/823), 231.26 KiB | 19.27 MiB/s, done.
Resolving deltas: 100% (310/310), done.
Created config file: /Users/mincong.huang/.config/cheat/conf.yml
Please read this file for advanced configuration information.
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
+     tags: [ elasticsearch, es ]
+     readonly: true

    - name: personal
      path: ~/documents/cheat/personal  # this is a separate directory and repository than above
      tags: [ personal ]
      readonly: false                   # new sheets may be written here
```

## Usage

```sh
# Show all the curl commands related to Elasticsearch
cheat -t elasticsearch curl
cheat -t es curl

# Show curl commands related to module "snapshot"
cheat -t elasticsearch:snapshot curl
cheat -t es:snapshot curl
```
