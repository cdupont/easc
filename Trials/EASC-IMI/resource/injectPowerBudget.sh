read -p "Please launch EASC in a separate terminal, press Enter when ready"
echo "Testing option plan request, CTRL inject power budget"
wget --post-file=powerBudget.json --header="content-type: application/json" http://localhost:9999/v1/easc/EASC-IMI/optionplan
