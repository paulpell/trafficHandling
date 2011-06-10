#!/bin/sh
find . -maxdepth 1 -type f -print0 | xargs -0 md5sum >> check.md5

