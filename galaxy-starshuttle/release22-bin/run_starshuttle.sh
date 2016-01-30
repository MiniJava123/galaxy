source /etc/profile
source ~/.bash_profile

cmd="ssh -o ConnectTimeout=3 -o ConnectionAttempts=5 -o PasswordAuthentication=no -o StrictHostKeyChecking=no -p 58422 deploy@10.1.6.49 sh /data/deploy/starshuttle22/bin/starshuttle.sh $@"

echo ${cmd}
eval ${cmd}