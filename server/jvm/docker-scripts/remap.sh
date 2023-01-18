#!/bin/bash
source /home/alpha/.bashrc
systemctl start postgresql-14
su -c "source /home/alpha/.bashrc ; yes | remap --commit" - "alpha"
su -c "JvmRun global.genesis.environment.scripts.SendTable -t USER -f /home/alpha/run/site-specific/data/user.csv" - "alpha"
echo "remap done"
