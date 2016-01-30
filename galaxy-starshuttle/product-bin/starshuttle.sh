source /etc/profile
source ~/.bash_profile
run_path="/data/deploy/starshuttle"
export LANG="zh_CN.utf8"

cmd="/usr/local/jdk/bin/java -classpath \"/data/deploy/starshuttle/lib/*\" com.dianping.data.warehouse.starshuttle.core.Engine $@"

echo ${cmd}
eval ${cmd}