#!/bin/sh
sh /etc/profile
rm -rf  $1
mysqldump -uroot -ppointred cems | gzip > $1
