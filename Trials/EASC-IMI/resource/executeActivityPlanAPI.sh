read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "execute Activity Plan that is inside eascImiActivityPlan.json"

#curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascImiActivityPlanNoWorkInTS0.json" http://localhost:9999/v1/easc/defaultName/activityplan
curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"eascImiActivityPlan.json" http://localhost:9999/v1/easc/EASC-IMI/activityplan
