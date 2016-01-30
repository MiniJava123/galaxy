source /etc/profile
source ~/.bash_profile

IPS=("10.2.2.22" "10.2.2.23" "10.2.2.22" "10.2.2.22" "10.1.110.49")
ip=${IPS[$[`date +%s%N`%5]]}
shellFile="\" sh /data/deploy/dwarch/conf/ETL/bin/start_autoetl.sh "

cmd="ssh -o ConnectTimeout=3 -o ConnectionAttempts=5 -o PasswordAuthentication=no -o StrictHostKeyChecking=no -p 58422 deploy@${ip} /data/deploy/starshuttle22/bin/starshuttle.sh $@"

echo ${cmd}
eval ${cmd}