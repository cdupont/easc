read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "Injecting activity plan for execution with CloudFoundry"
curl -i -X PUT -H "Content-Type: application/json; charset=utf-8" -d @"AP2.json" http://localhost:9999/v1/easc/EASC-CSUC/activityplan
