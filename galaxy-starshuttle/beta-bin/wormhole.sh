source /etc/profile
export WORMHOLE_CONNECT_FILE="/data/deploy/wormhole/build/connect.props"
cmd="sh /data/deploy/wormhole/build/bin/wormhole.sh $1"
echo $cmd
eval $cmd