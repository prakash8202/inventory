#!/bin/sh
sh /etc/profile
 ./droptables.sh root pointred cems
gunzip < $1 | mysql -uroot -ppointred cems
