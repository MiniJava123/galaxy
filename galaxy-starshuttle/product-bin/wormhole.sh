source /etc/profile
source ~/.bash_profile
export WORMHOLE_CONNECT_FILE="/data/deploy/dwarch/conf/ETL/conf/connect.props"
cmd="sh /data/deploy/dwarch/bin/wormhole/build/bin/wormhole.sh $1"
echo $cmd
eval $cmd