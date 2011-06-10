#!/bin/sh
TESTED=0
ant_test(){
	if [ $? -ne 0 ]; then 
		echo "ant has issue : exiting " 1>&2 
		exit 2
	fi
}

md5(){
	if [ $TESTED -eq 0 ]; then
		TESTED=1
		if [ ! -z "$(type -P md5sum)" ]; then 
			echo "you have to verify each file in the directory"
			echo "I'm doing it in case of something bad happens"
			md5sum -c check.md5
			if [ $? -ne 0 ]; then 
				echo "the file are corrupted please download the project again" 1>&2
				exit 2
			fi
		fi
	fi
}

if [ ! -d "build" ]; then 
	echo "the binary directory didn't exist"
	echo "I'm building it for you"
	md5
	ant build
fi
ant_test

if [ ! -d "javadoc" ]; then 
	echo "the javadoc directory didn't exist"
	echo "I'm building it for you"
	md5
	ant javadoc
fi
ant_test

cd build	
java trafficHandling.Main
