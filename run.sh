#!/bin/sh
if [ ! -d "build" ]; then 
	echo "the binary directory didn't exist"
	echo "I'm building it for you"
	if [ $(whereis md5sum) ]; then 
		echo "you have to verify each file in the directory"
		echo "I'm doing it in case of something bad happens"
		md5sum -c check.md5
		if [ $? -ne 0 ]; then 
			echo "the file are corrupted please download the project again" 1>&2
			exit 2
		fi
	ant build
	fi
fi
if [ $? -ne 0 ]; then 
	echo "ant has issue : exiting " 1>&2 
	exit 2
fi

cd build	
java trafficHandling.Main
