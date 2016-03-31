CREATE TABLE progress (activity VARCHAR(30), date DATE, quantity INTEGER DEFAULT 0, last_minute INTEGER DEFAULT 0, last_instant_progress INTEGER DEFAULT 0, date_last_minute DATE, PRIMARY KEY(activity, date));
CREATE TABLE fets (activity VAR(30), object VARCHAR(50), PRIMARY KEY(activity, object));
CREATE TABLE working_modes (date DATE, activity VARCHAR(30), wm VARCHAR(5), status VARCHAR(8), PRIMARY KEY(activity,date,wm));
CREATE TABLE virtual_machines(vmid VARCHAR(12), ip VARCHAR(15), PRIMARY KEY(vmid));
CREATE TABLE wm_to_vm(activity VARCHAR(30), date DATE, wm VARCHAR(5), vmid VARCHAR(12), PRIMARY KEY(activity, date, wm, vmid), FOREIGN KEY(activity) REFERENCES working_modes(activity), FOREIGN KEY(date) REFERENCES working_modes(date), FOREIGN KEY(wm) REFERENCES working_modes(wm), FOREIGN KEY(vmid) REFERENCES virtual_machines(vmid));
