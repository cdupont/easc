read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "Reading read Monitoring Metrics at time "

curl -i -X POST -H "Content-Type: application/json; charset=utf-8" -d @"timeParams.json" http://localhost:9999/v1/easc/EASC-CSUC/metrics
