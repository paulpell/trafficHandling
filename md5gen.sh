#!/bin/sh
find . -type f -print0 -name '*.java'| xargs -0 md5sum >> check.md5

