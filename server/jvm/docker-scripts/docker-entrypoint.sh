#!/bin/bash
systemctl start postgresql-14
systemctl enable sshd.service
systemctl start sshd.service
su -c "startServer" - "alpha"
echo "Logged as alpha, starting server"
tail -f /dev/null