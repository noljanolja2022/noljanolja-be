import click
import os
from alembic import command
from alembic.config import Config

alembic_cfg = Config('./alembic.ini')

DATABASE_USER = os.getenv('DB_USER', 'noljanolja')
DATABASE_PASSWORD = os.getenv('DB_PASS', 'password')
DATABASE_HOST = os.getenv('DB_HOST', '127.0.0.1')
DATABASE_PORT = os.getenv('DB_PORT', '3306')
DATABASE_NAME = os.getenv("DB_NAME", 'noljanolja_core')

@click.group()
@click.option('--debug/--no-debug', default=False)
def cli(debug):
    click.echo('Debug mode is %s' % ('on' if debug else 'off'))

@cli.command()
def upgrade():
    alembic_cfg.set_main_option('sqlalchemy.url', 'mysql+pymysql://%s:%s@%s:%s/%s' % (
        DATABASE_USER, DATABASE_PASSWORD, DATABASE_HOST, DATABASE_PORT, DATABASE_NAME))
    command.upgrade(alembic_cfg, 'head')

    click.echo('The database %s is migrated successfully' % DATABASE_NAME)

@cli.command()
@click.option('--message', '-m')
def revision(message):
    command.revision(alembic_cfg, message=message)

@cli.command()
def downgrade():
    alembic_cfg.set_main_option('sqlalchemy.url', 'mysql+pymysql://%s:%s@%s:%s/%s' % (
        DATABASE_USER, DATABASE_PASSWORD, DATABASE_HOST, DATABASE_PORT, DATABASE_NAME))
    command.downgrade(alembic_cfg, '-1')

    click.echo('The database %s is downgrade successfully' % DATABASE_NAME)
if __name__ == '__main__':
    cli(obj={})
