read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "execute Activity Plan that is inside eascCsucActivityPlan.json"

#curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCsucActivityPlanNoWorkInTS0.json" http://localhost:9999/v1/easc/defaultName/activityplan
curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascCsucActivityPlan.json" http://localhost:9999/v1/easc/EASC-CSUC/activityplan
