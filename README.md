# ImageDB
Under construction ....

A simple application to put images in a Postgresql database.
Images must be marked with a unique UUID in UserComment-tag ("Image_UUID:.....")
which also is the primary key in database

The image itself is stored in filesystem (so far) with only its EXIF-data stored
in database, as a json datatype and some ordinary column-data.
