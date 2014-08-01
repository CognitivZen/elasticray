#!/bin/sh

#set -x

if [ $# -lt 2 ]; then
	echo
	echo Usage: ./create.sh hello-world \"Hello World\"
	echo
	echo The first hello-world is your theme id. A new directory will be created based
	echo on the theme id.
	echo
	echo The second \"Hello World\" is the theme\'s display name. The quotation marks are
	echo only needed because there is a space in the display name.

	exit 127
fi

ant -Dtheme.name=$1 -Dtheme.display.name=\"$2\" create

#ant deploy

exit 0