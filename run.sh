#!/bin/sh
if [ ! -d "build" ]; then 
	echo "the binary directory didn't exist"
	echo "I'm building it"
	ant build
fi
if [ $? -ne 0 ]; then 
	echo "ant has issue : exiting " 1>&2 
	exit 2
fi

cd build	
java trafficHandling.Main
