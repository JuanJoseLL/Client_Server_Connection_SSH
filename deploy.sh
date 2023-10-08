#!/bin/bash

#echo "1000000 1000 0" | java -jar client.jar
#echo "1000000 1000 0" | java -jar client.jar

start=$1
end=$2

echo "running command into \n------\n"
sequence=$(seq $start $end)
for i in $sequence
do
    host="xhgrid$i"
    echo "executing: into $host"
    sshpass -p swarch scp -o StrictHostKeyChecking=no "client/buil/libs/client.jar" "swarch@$host:MBA/JUAN/"
    sshpass -p swarch ssh -o StrictHostKeyChecking=no "swarch@$host" "sh MBA/JUAN/run.sh"
    echo "\ncommand executed\n------"
done
