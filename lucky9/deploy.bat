cd /d %~dp0
cf api https://api.ng.bluemix.net
cf login -u nathanielcamomot@gmail.com -p secret
cf target -o nathanielcamomot@gmail.com -s dev
cf push lucky9 -p target/lucky9-1.0-SNAPSHOT.war