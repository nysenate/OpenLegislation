# Open Legislation Setup

## Install Software

General installation instructions for Ubuntu.

### Java 8
1. `sudo apt-get install openjdk-8-jdk`
1. Set `$JAVA_HOME` environment variable
    * https://askubuntu.com/questions/175514/how-to-set-java-home-for-java

### Git
1. `sudo apt-get install git`
1. Configuration
    1. `git config --global user.name "<<Name>>"`
    1. `git config --global user.email <<Email>>`

### IntelliJ

1. Download the Ultimate Edition from https://www.jetbrains.com/idea/download/#section=linux
1. Extract: `sudo tar -xzvf <<downloaded_file_name>> -C /usr/share/`
1. Run: `bin/idea.sh` which will be located in the directory extracted in the previous step.

### Tomcat

1. Download the latest version of Tomcat from https://tomcat.apache.org/download-90.cgi
    * You want the Core tar.gz distribution.
1. `mkdir ~/tomcat8`
1. `tar -xzvf ~/Downloads/<<downloaded file>> -C ~/tomcat8`
1. If you need to run tomcat as a non-root user, e.g. in IntelliJ.  
Make sure the contents of the tomcat directory are readable an executable for all users.
e.g. `chmod -R +rx ~/tomcat8`

### Elasticsearch

1. `wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-6.5.4.deb`
1. `sudo dpkg -i elasticsearch-6.5.4.deb`
1. `sudo systemctl enable elasticsearch.service`

### Postgresql, Maven, Nodejs

1. `sudo apt-get install postgresql postgresql-contrib maven nodejs`

### Bower and Grunt

1. `sudo npm install -g bower grunt`

## Source Code Setup

### Clone Repository

Clone the Open Legislation codebase to your computer.

1. `cd <<dev directory>>` Move into the directory where you want to keep the source code.
1. `git clone https://github.com/nysenate/OpenLegislation `

### Open Code in Intellij

1. From the start screen, click Import project
    * If you are already in an open project, go to File->New->Project from Existing Sources
1. Select the Open Legislation directory that was cloned from GitHub
1. In the Import Project screen, select "Import project from external model" and choose Maven from the list of models
1. The next screen will be a bunch of checkbox options. Ensure the following are checked:
    * Search for projects recursively
    * Import Maven projects automatically
    * Automatically download
        * Sources
        * Documentation
1. Be sure to use Java 8 on this project

## Database Setup

### Create Database User

Using example user `openleg` with the password `ol_pass`

1. Create a new role in postgres. 
   `CREATE USER openleg WITH LOGIN PASSWORD ol_pass`

### Create Open Legislation Database

1. Enter psql in a terminal with the database user you created in the previous step: `psql -U openleg postgres`
1. Create a database for Open Legislation: `CREATE DATABSE openleg;`
1. Exit psql with `\q`

## Property Files

Navigate to `src/main/resources` and copy the following files:
* `app.properties.example` -> `app.properties`
* `log4j.properties.example` -> `log4j.properties`
* `flyway.conf.example` -> `flyway.conf`


Also navigate to `src/main/webapp` and copy the following files:
* `grunt.properties.example.json` -> `grunt.properties.json`

### `app.properties` Configuration

These are properties that need to be modified for a typical installation.  Many properties are ignored in this guide but may need to be set to fit your needs.

#### API authentication

Set `api.auth.enable = false` if you do not want to deal with api authentication.
   
Alternatively, modify the regexp `api.auth.ip.whitelist` to not enforce api authentication on matched hosts/ips.

#### Admin Settings

Set `default.admin.user` to your email address and `default.admin.password` to your desired admin password.

Set `admin.email.regex` to match your user email address and fit any other admin users you would want to add.

#### Data Directory Configuration

These are references to your base data directory, staging directory, and your archive directory.

Create these directories where you wish and ensure they are correctly referenced in `env.base`, `env.staging`, and `env.archive` respectively.

#### Elasticsearch Search Configuration

Set `elastic.search.cluster.name` to match the cluster name you set in Elasticsearch Setup.

Ensure that elasticsearch is running prior to Open Legislation startup.

#### Postgres Database Configuration

Set these properties according to the values you set in the Database Setup section.

You will probably just need to set `postgresdb.name`, `postgresdb.user`, and `postgresdb.pass`

#### Scheduler Config

Set `scheduler.process.enabled` = `false`

#### Spotcheck Configuration

If you are not interested in the data qa portion of the app, you may want to start with `bill.scrape.queue.enabled = false`.

If true, the app will scrape qa data from LBDC every data process cycle.

#### Mail Configuration

The `checkmail` properties are only useful if you are interested running spotcheck data qa reports.

Point the `mail.smtp` properties at an smtp server to enable email sending.  This is required for api key registration.

#### Domain Configuration

Set `domain.url` to the hostname and context path you will be using.  This affects urls for automated emails sent by 

### `flyway.conf` Configuration

These configurations are needed for automatic database migrations.

Set `flyway.user` to the database user you created.

Set `flyway.password` to the database user password.

### `grunt.properties.config` Configuration

Set `deployDirectory` to `<<path to OpenLegislation codebase>>/target`

## Elasticsearch setup

Add or modify the field `cluster.name` in `/etc/elasticsearch/elasticsearch.yml` to be unique to your openleg instance e.g.
```
cluster.name: sam-openleg
```

## Building

Run `mvn compile flyway:migrate` to generate a build that is deployable by tomcat.  Our unit tests are currently not in a good state, so we can't get any further in the Maven build process.

## Running Open Legislation

We typically run Open Legislation in Tomcat through IntelliJ.

1. Open Intellij and go to menu: Run -> Edit Configurations
1. Click the plus sign in the top left corner
1. Scroll down until you find Tomcat Server. Select local server
1. In the Server tab -> application server link your download of tomcat from before
1. In the Deployment tab -> hit the plus sign again and select legislation:war exploded
1. Apply these changes

Now you can run Tomcat by selecting it and pressing the green play button towards the top right of the Intellij UI.
Once running you should be able to view the application at: http://localhost:8080

## Legislative Data

### Setup Open Legislation Environment Directories

1. `sudo mkdir /data`
1. `sudo chown -R $USER:$USER /data`
1. `mkdir /data/openleg /data/openleg/archive /data/openleg/staging /data/openleg/staging/xmls`

### Rsync Legislative Data

1. `rsync -a --info=progress2 '<<username>>@tyrol.nysenate.gov:/data/2017-18_xml_files' /data/openleg/staging/xmls`

### Process Data

Now we can process the xml data we downloaded in our local Open Legislation environment. This process can take a long time so be prepared to leave it running for up to a day or two.

1. `curl -XPOST -v -u '<<username>>:<<password>>' localhost:8080/api/3/admin/process/run`

**NOTE**
* Do not attempt this on a system with 4GB of ram without changing the cache limits in app.properties
* Using a SSD can help the performance of this process significantly