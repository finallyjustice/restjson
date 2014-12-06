#!/bin/sh

DATAHOME=$HOME/restjsonhome
JSONDBHOME=$DATAHOME/jsondb
CONFIGFILE=restjson.config

# create json data home folder
mkdir $DATAHOME
# create json database folder
mkdir $JSONDBHOME
# create application configuration file
cp $CONFIGFILE $DATAHOME/.

echo

# print api url
HOSTURL=`cat $CONFIGFILE | grep "^http"`
APIURL=$HOSTURL/restjson/object
echo "######## Thank you for using the REST API ########"
echo "URL: "$APIURL
echo "You can try with: "
echo "  curl --request GET "$APIURL
echo "##################################################"

echo
