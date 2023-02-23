"""create table announcements

Revision ID: 378064eced05
Revises: ae242b1eee5a
Create Date: 2023-02-23 14:18:18.379423

"""
from alembic import op
import sqlalchemy as sa
from data_types import BinaryUUID


# revision identifiers, used by Alembic.
revision = '378064eced05'
down_revision = 'ae242b1eee5a'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table(
        'announcements',
        sa.Column('id', BinaryUUID(), primary_key=True),
        sa.Column('title', sa.String(length=255)),
        sa.Column('content', sa.Text()),
        sa.Column('priority', sa.String(length=20), server_default='MEDIUM'),
        sa.Column('created_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
        sa.Column('updated_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
    )


def downgrade():
    op.drop_table('announcements')
