#!/bin/sh
find . -type f -print0 | xargs -0 md5sum >> check.md5
sed check.md5 -e "s/.*.git.*//" | sed -e "s/.*check\.md5.*//" | sed -e "s/.*md5gen.*//" | sed '/^$/d' >> check2.md5
mv check2.md5 check.md5

