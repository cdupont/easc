read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "get Activity Plan that is currently scheduled..."

curl -i -X GET -H "Content-Type: application/json; charset=utf-8" http://localhost:9999/v1/easc/EASC-IMI/activityplan
