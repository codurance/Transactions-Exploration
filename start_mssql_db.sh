#!/bin/bash
set -euo pipefail
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

cd "$DIR"

docker run \
    --cap-add SYS_PTRACE \
    -e 'ACCEPT_EULA=1' \
    -e 'MSSQL_SA_PASSWORD=yourStrong(!)Password' \
    -e 'MSSQL_PID=Premium' \
    -p 1433:1433 \
    --name azuresqledge \
    -d \
    mcr.microsoft.com/azure-sql-edge


cd - >/dev/null
