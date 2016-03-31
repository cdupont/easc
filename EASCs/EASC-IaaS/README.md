# EASC IaaS

This EASC controls several VMs as blackboxes. No knowledge about the application detail or of any kind
are known. A power budget must be respected to meet the DC4Cities goal. The centralsystem (CTRL) will 
compute the option plans for the EASC as soon as it starts. CTRL then send a configuration that optimizes
the infrastructure usage, low power, meeting the goal of respecting a budget. The result should be passed
to plug4green that will translate this option plan into action to OpenStack. For now the system is made
based on OpenStack but this should be adaptable to other systems.

# Creating a New EASC Step-by-step

This step-by-step tutorial intends to guide you through the process of implementing a new EASC.

First create a branch on the git repository.

    git branch easc-new
    
Now checkout the created branch.

    git checkout easc-new
    
Create a new directory on the easc project tree for your module.

    mkdir easc-new
    
Copy the pom.xml file of an old easc and edit the file so the artifact id to match your product.

    cp easc/EASC-CFApp/pom.xml
    emacs easc/EASC-CFApp/pom.xml


# Installation

To install, type:

    mvn install 


# Usage

comming soon


# Tests 

To run the test suite, launch:

   mvn test

## Test Scneario 1 (packing)

two nodes,

    n1 and n2, each one has 2 pCPUs at 100

four VMs,

    vm1, vm2, vm3, and vm4, each use one vCPU

### Initial Setup

    n1 -> runs vm1 and vm2, both operating at 100%
    n2 -> runs vm3 and vm4, both operating at 100%

### Action

changes vm1, vm2, vm3, and vm4 computing rate to 50%

### Expected Result

Now should consolidate all VMs on the same node, i.e.:

    n1 -> runs vm1, vm2, vm3, and vm4 all operating at 50%
    n2 -> runs none


## Test Scenario 2 (unpacking)

two nodes,

    n1 and n2, each one has 2 pCPUs at 100

### Initial Setup

    n1 -> runs vm1, vm2, vm3, and vm4 all operating at 50%
    n2 -> runs none

### Action

changes vm1, vm2, vm3, and vm4 all to 100%

### Expected Result

Should have now each node with only two VMs, for instance:

    n1 -> runs vm1 and vm2, both operating at 100%
    n2 -> runs vm3 and vm4, both operating at 100%


# Todo

- Create configuration file with working modes;

- Match configuration file with SLA;

- Read files configuration and send the application details to centralsystem;

- Receive option plan from centralsystem and transmit then to Plug4Green;

- Set a small scenario to test packing (consolidation);

- Set a small scenario to test resource unpacking (inverse consolidation);

