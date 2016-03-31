read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "Reading activity specification at time "

curl -i -X POST -H "Content-Type: application/json; charset=utf-8" -d @"timeParams3DaysHorizon.json" http://localhost:9999/v1/easc/defaultName/activityspecifications 
#curl -i -X POST -H "Content-Type: application/json; charset=utf-8" -d @"timeParams.json" http://localhost:9999/v1/easc/defaultName/activityspecifications
#curl -i -X POST -H "Content-Type: application/json; charset=utf-8" -d @"timeParamsSpotted.json" http://localhost:9999/v1/easc/defaultName/activityspecifications
