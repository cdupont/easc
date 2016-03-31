read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "execute Activity Plan that is inside eascCFAppActivityPlan.json"

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM0.json" http://localhost:9999/v2/easc/defaultName/activityplan
sleep 3;
./readMonitoringMetrics.sh
sleep 25;

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM1.json" http://localhost:9999/v2/easc/defaultName/activityplan
sleep 3;
./readMonitoringMetrics.sh
sleep 25;

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM2.json" http://localhost:9999/v2/easc/defaultName/activityplan
sleep 3;
./readMonitoringMetrics.sh
sleep 25;

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM3.json" http://localhost:9999/v2/easc/defaultName/activityplan
./readMonitoringMetrics.sh
sleep 25;

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM3.json" http://localhost:9999/v2/easc/defaultName/activityplan
./readMonitoringMetrics.sh
./readMonitoringMetrics.sh
sleep 25;

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM0.json" http://localhost:9999/v2/easc/defaultName/activityplan
sleep 3;
./readMonitoringMetrics.sh

curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCFAppActivityPlanWM0.json" http://localhost:9999/v2/easc/defaultName/activityplan
sleep 3;
./readMonitoringMetrics.sh
#curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCsucActivityPlan.json" http://localhost:9999/v1/easc/defaultName/activityplan
