source /etc/profile
source ~/.bash_profile

cmd="sh /data/deploy/starshuttle/bin/starshuttle.sh $@"

echo ${cmd}
eval ${cmd}