read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "Testing option plan request"
wget --post-file=powerBudget2.json --header="content-type: application/json" http://localhost:9999/v1/easc/TrentoEASC/optionplan
read -p "press Enter to continue"
echo "Testing activity plan execution"
curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"activity.json" http://localhost:9999/v1/easc/TrentoEASC/activityplan
