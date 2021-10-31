#!/bin/bash

rm -f ./bot.pid
nohup java -server \
    -cp ./ \
    -jar telegram_bot_kotlin_munin.jar >/dev/null 2>&1 &
echo $! > ./bot.pid
