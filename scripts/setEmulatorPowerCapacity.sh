#!/bin/bash
expect << EOF
spawn telnet localhost 5554
expect "OK"
send -- "power capacity $1\r"
expect "OK"
send -- "exit\r"
EOF
