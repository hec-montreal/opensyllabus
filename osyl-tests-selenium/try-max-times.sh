#!/bin/sh

times=$1;
delay=$2;
prog=$3;

function usage {
  echo "Usage: $0 <nb_times_to_try> <sleep_time> <target_program>";
  echo "       Retries the specified program or script until it succeeds or reaches the maximum";
  echo "       number of times to try. Sleeps the specified delay between tries (in seconds).";
}

if [ "$times" == "" ] || [ "$delay" == "" ] || [ "$prog" == "" ]; then
  usage;
  exit 1;
fi

echo "============ Running '$prog' until it succeeds (maximum $times times) ============"

let i=1;
while [ $i -le $times ]; do
   echo Starting pass \#$i;
   $prog;
   if [ $? == 0 ]; then
      echo "===============================================";
      echo "Pass $i succeeded. Exiting $0 now.";
      exit 0;
   else
      echo "Pass $i failed";
      if [ $i -lt $times ]; then
        echo "Sleeping $delay seconds before next try.";
        sleep $delay;
      fi
      echo "";
   fi
   let i++;
done

echo ============ FAILED after $times times ============
exit 1;


