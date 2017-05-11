# Open Legislation Setup

## Required Software

* Java 8
* Maven
* Tomcat 8
* Postgresql 9.6
* Elasticsearch 2.4.4

## Database Setup

### Initalize Schema and Static Data

1. (From the project root) navigate to `src/main/resources/sql`
1. Create the database (in `psql` or your preferred client). `CREATE DATABASE openleg;`
1. Connect to the openleg database.  `\c openleg`
1. Run the following scripts in order (`\i script_name` in `psql`)
   1. `openleg.db-init.sql`
   1. `openleg.schema.sql`
   1. `openleg.data.sql`
  
### Create Login User and Grant Permissions

Using example user `openleg` with the password `ol_pass`

1. Create a new role in postgres. 
   `CREATE USER openleg WITH LOGIN PASSWORD ol_pass`
1. Grant permissions
   * `GRANT ALL PRIVILEGES ON SCHEMA master,public TO openleg`
   * `GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA master,public TO openleg`
   * `GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA master,public TO openleg`
  
## Elasticsearch setup

Add or modify the field `cluster.name` in `elasticsearch.yml` to be unique to your openleg instance e.g.
```
cluster.name: sam-openleg
```

## Property Files

Navigate to `src/main/resources` and copy the following files:
* `app.properties.example` -> `app.properties`
* `log4j.properties.example` -> `log4j.properties`

## `app.properties` Configuration

These are properties that need to be modified for a typical installation.

1. Set `api.auth.enable = false` if you do not want to deal with api authentication.
   Alternatively, modify the regexp `api.auth.ip.whitelist` to not enforce api authentication on matched hosts/ips
1. Admin settings

**More to come soon**
