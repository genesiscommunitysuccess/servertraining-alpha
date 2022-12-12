#!/bin/bash
source /home/alpha/.bashrc
systemctl start postgresql-14
su -c "source /home/alpha/.bashrc ; genesisInstall" - "alpha"
echo "genesisInstall done"