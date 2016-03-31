# EASC CloudFoundry demo

This documentation describes the steps to run a demo on the integration of EASC with CloudFoundry through a multi-tier application. First, we describe how to install bosh-lite to run a lightweight CloudFoundry. Then, we describe how to get the right EASC branch from the git that has been developed for this demo. As the technologies behind CloudFoundry and BOSH are evolving we don't provide a script, since most probably the script will fail. We have written the full steps explaining about commands.

## BOSH-LITE and CloudFoundry Installation

### Install latest version of bosh_cli by installing the following packages:

```
$ sudo apt-get install build-essential ruby ruby-dev libxml2-dev libsqlite3-dev libxslt1-dev libpq-dev libmysqlclient-dev
$ sudo gem install bosh_cli bosh_cli_plugin_micro --no-ri --no-rdoc
```

### Install Vagrant, Bosh-Lite and VirtualBox

[Get Vagrant](http://www.vagrantup.com/downloads.html), and install it.

   ```
$sudo dpkg --install vagrant_1.7.2_x86_64.deb
   ```
Known working version:

```
$ vagrant --version
Vagrant 1.6.3
```

Install bosh-lite:
```
$ cd ~/workspace
$ git clone https://github.com/cloudfoundry/bosh-lite

```
[Get VirtualBox](https://www.virtualbox.org/wiki/Downloads) and install it.

Get it from http://download.virtualbox.org/virtualbox/4.3.28/virtualbox-4.3_4.3.28-100309~Ubuntu~raring_amd64.deb (if this link still works), and install it.

Known working version:
```
$ VBoxManage --version
4.3.14r95030
```

### Start Vagrant

Start Vagrant in the bosh-lite directory:

```
$ cd bosh-lite
$ vagrant up --provider=virtualbox
```

The most recent version of the BOSH Lite boxes will be downloaded by default from the Vagrant Cloud when you run `vagrant up`. If you have already downloaded an older version you will be warned that your version is out of date. You can use the latest version by running `vagrant box update`.

### Initialize BOSH-LITE configuration

```
$ bosh target 192.168.50.4 lite
Target set to `Bosh Lite Director'

$bosh login
Your username: admin
Enter password: admin
Logged in as `admin'
```

Add a set of route entries to your local route table to enable direct Warden container access every time your networking gets reset (e.g. reboot or connect to a different network). Your sudo password may be required.

```
$bin/add-route

```

## Install CloudFoundry

### Install Spiff

Download the latest binary of Spiff from https://github.com/cloudfoundry-incubator/spiff/releases. Then, extract it into your local binary file directory.

```
$ sudo unzip spiff_linux_amd64.zip -d /usr/local/bin

```

### Clone CloudFoundry and provision it

```
$ git clone https://github.com/cloudfoundry/cf-release
$ ./bin/provision_cf

```

Note: this process will use a lot of disk space. If necessary, change the temporary folder to a bigger partition with:

```
export TMPDIR=~/tmp
```


# Prepare Cloud Foundry deployment

Install the Cloud Foundry CLI and run the following:

Download CF client for your Operating System https://github.com/cloudfoundry/cli#downloads, and install it.

```
$ cf api --skip-ssl-validation https://api.10.244.0.34.xip.io
$ cf auth admin admin
$ cf create-org  DC4Cities
$ cf target -o DC4Cities
$ cf create-space TrentoTrial
$ cf target -s TrentoTrial
```

# Clone EASC master branch from the git repository, and build the EASC system if there is any need.

# Push a multi-tier application by creating three CF applications into the CF

The application tiers are already available in the EASCs/EASC-PaaS/applications directory. They are simple PHP webpages as application that print out the name of the tier.

```
$ cd ~/git/easc/EASCs/EASC-PaaS/applications

```

Go to the each app tier directory and issue the following command to push the tier to the CF:

```
$ cd frontend
$ cf push frontend -n frontend -k 100M -m 128M

```

Repeat the same process for the elaboration, and backend tiers:

```
$ cd ../elaboration/
$ cf push elaboration -n elaboration -k 100M -m 128M

$ cd ../backend/
$ cf push backend -n backend -k 100M -m 128M
```

Reference on CF for more info: https://github.com/cloudfoundry/bosh-lite/blob/master/docs/deploy-cf.md

# Run EASC-PaaS instance with the right parameter

EASC-PaaS main is parametrized to allow users to specify their environment. You need to pass mock, or bosh-lite, or multi-easc to specify the CF instance, and the EASC type. "mock" refers to a mock instance of CloudFoundry, there is no real CloudFoundry. bosh-lite refers to a bosh-lite or a CF instance. The last one is for a co-located EASCs that share the same infrastructure. For instance, in case each CF application is controlled by an EASC, then we use "multi-easc" parameter.

```
$ cd ~/git/easc/EASCs/EASC-PaaS/
$ bin/EASC.sh mock
```

If there is no problem, EASC should be waiting to receive activity plans for execution.

If you are working with a real CF instance, you need to edit CloudFoundryConfig.yaml in order to provide CF endpoint, and credentials.
Edit this file to provide CF endpoint, and credentials.

```
$vim resource/CloudFoundryConfig.yaml
```

```
$ cd ~/git/easc/EASCs/EASC-PaaS/
$ bin/EASC.sh bosh-lite
```

# Prepare Activity Plan and send it to EASC

Edit eascCFAppActivityPlan.json in order to specify the desired working mode. Then, by executing ./executeActivityPlanAPI.sh you ask the EASC to execute the activity plan you specified earlier. You can see the WM definitions in AppConfig.yaml.

```
$ cd resources
$ vim eascCFAppActivityPlan.json
$ ./executeActivityPlanAPI.sh

```

If there is no problem, EASC service should schedule the activity plan execution, and you will see the scheduling in the EASC output.

Then, as it enacts different working mode, you should see how EASC-CloudFoundry scales applications, starting, stoping applications, etc. It will also print out some monitoring information on applications containers.


# Stop and restart bosh-lite

To stop the CF VMS:
```
$ cd bosh-lite
$ vagrant suspend
```

To restart:
```
$ vagrant up
```

If something goes wrong:
```
$ bosh cloudcheck
```
This will recreate the deleted VMs.


That's it.
