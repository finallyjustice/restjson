#!/bin/sh

DATAHOME=$HOME/restjsonhome
CONFIGFILE=restjson.config

# create json data home folder
mkdir $DATAHOME
# create application configuration file
cp $CONFIGFILE $DATAHOME/.

echo

# print api url
HOSTURL=`cat $CONFIGFILE | grep "^url" | awk '{print $2}'`
APIURL=$HOSTURL/restjson/object
echo "######## Thank you for using the REST API ########"
echo "URL: "$APIURL
echo "You can try with: "
echo "  curl --request GET "$APIURL
echo "##################################################"

echo
