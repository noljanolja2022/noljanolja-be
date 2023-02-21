## Migration service

This service manages all operations related to database migration and operation

## Start to migrate for the first time

### Usage for local machine

To migrate the tenant database

```zsh
python cli.py migrate-db <db_name>
```

To generate a new migration file:

```zsh
python cli.py revision -m "a meaning message"
```

### A recommended setup to run this script

Create virtual environment
```zsh
python3 -m venv venv
```

Active venv
```zsh
source venv/bin/activate
```

Install Python dependencies
```zsh
pip install -r requirements.txt
```
You might get errors if you're not install MySQL on your local computer.
In fact, we use Docker, so it doesn't necessary to install MySQL to your host computer.
You only need to install `mysql-client`.

Ubuntu:
```zsh
apt-get install mysql-client
```

Brew: 
```zsh
brew install mysql-client
```

Window:
Change your OS or use Linux, Mac

## Update test SQL db schema

```shell
From migrations folder
$ PYTHONPATH=. alembic -c database/alembic.ini upgrade head --sql > schema.sql
$ cp schema.sql ../server/src/test/resources/schema.sql
```
