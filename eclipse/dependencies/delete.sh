#!/bin/bash
files=(
artifacts.jar

content.jar
)

user=simon
token=secret
deletePath=vogellacompany/OpenReq-Eclipse/Eclipse-Plugin-Deps

for item in ${files[*]}; do
	curl -u $user:$token -X "DELETE" https://api.bintray.com/content/$deletePath/$item
done



