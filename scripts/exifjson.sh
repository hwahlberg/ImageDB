#!/bin/bash


# Check if file is an image
# type should be one of:
acceptedImage=(\
"image/jpeg;" \
"image/png;" \
"image/tiff;" \
"image/gif;"\
)

function isImage() {
    local e
    type=$(file -i $1 | awk '{print $2}')
    for e in "${@:2}"
    do 
	[[ "$e" == "$type" ]] && return 0; 
    done 
    return 1;
}



for image in $*
do
    # first a check that its an accepted image
    isImage "$image" "${acceptedImage[@]}"
    if [ $? -eq 0 ]
    then

	# check that we don't already have an UserComment with a "Image_UID:.."
	# If so, skip the file to keep Image_UUID
	file=$(echo $image | cut -f1 -d.)
	if [ $(exiftool -UserComment $image | grep "Image_UUID" | wc -l) -eq 0 ]
	then
	    echo "Update image $image with UserComment"
	    UUID=$(uuidgen)
	    exiftool -UserComment="Image_UUID:${UUID}" $image
	fi
	# echo "exiftool  -json -g -s -d %Y%m%d%H%M%S $image | sed -e 's/\[//'g -e 's/\]//g' >${file}.json"
	exiftool  -json -g -s -d %Y%m%d%H%M%S $image | sed -e 's/\[//'g -e 's/\]//g' >${file}.json
    fi
done

exit 0
