[#ftl encoding="UTF-8"/]
#!/bin/bash
set -e
DIR="$( cd "$( dirname "${r"${BASH_SOURCE[0]}"}" )" && pwd )"
"$DIR"/java-daemon/start-daemon.sh ${prunsrvLauncherJarFile} $@