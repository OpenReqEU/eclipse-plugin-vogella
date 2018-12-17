#!/bin/bash
user=simon
token=secret

curl -T ./build/updatesite.zip -u $user:$token -H "X-Bintray-Explode: 1" -H "X-Bintray-Package:Eclipse-Plugin-Deps" -H "X-Bintray-Version:0.5.0" https://api.bintray.com/content/vogellacompany/OpenReq-Eclipse/Eclipse-Plugin-Deps/0.5.0
