"""create conversation table

Revision ID: 9de383b2fe3b
Revises: 378064eced05
Create Date: 2023-03-02 11:43:45.323536

"""
from alembic import op
import sqlalchemy as sa
from data_types import BinaryUUID

# revision identifiers, used by Alembic.
revision = '9de383b2fe3b'
down_revision = '378064eced05'
branch_labels = None
depends_on = None


def upgrade():
    op.create_table(
        'conversations',
        sa.Column('id', sa.BigInteger(), primary_key=True),
        sa.Column('title', sa.String(length=255)),
        sa.Column('type', sa.Text()),
        sa.Column('creator_id', BinaryUUID()),
        sa.Column('created_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
        sa.Column('updated_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
    )


def downgrade():
    op.drop_table('conversations')
