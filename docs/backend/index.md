# Open Legislation Setup

## Required Software

* Java 8
* Maven
* Tomcat 8
* Postgresql 11.1
* Elasticsearch 6.4

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

You will probably just need to set `postgresdb.user` and `postgresdb.pass`

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

## Elasticsearch setup

Add or modify the field `cluster.name` in `elasticsearch.yml` to be unique to your openleg instance e.g.
```
cluster.name: sam-openleg
```

## Building

Run `mvn compile flyway:migrate` to generate a build that is deployable by tomcat.  Our unit tests are currently not in a good state, so we can't get any further in the Maven build process.

