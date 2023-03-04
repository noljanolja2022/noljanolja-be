"""create table users

Revision ID: ae242b1eee5a
Revises: 
Create Date: 2023-02-21 08:55:59.560359

"""
from alembic import op
import sqlalchemy as sa
from data_types import BinaryUUID

# revision identifiers, used by Alembic.
revision = 'ae242b1eee5a'
down_revision = None
branch_labels = None
depends_on = None

def upgrade():
    op.create_table(
        'users',
        sa.Column('id', BinaryUUID(), primary_key=True),
        sa.Column('firebase_user_id', sa.String(length=255)),
        sa.Column('name', sa.String(length=255)),
        sa.Column('profile_image', sa.Text()),
        sa.Column('email', sa.String(length=64)),
        sa.Column('push_token', sa.Text()),
        sa.Column('push_noti_enabled', sa.Boolean, default=False),
        sa.Column('phone', sa.String(length=32)),
        sa.Column('is_email_verified', sa.Boolean, default=False),
        sa.Column('dob', sa.TIMESTAMP()),
        sa.Column('gender', Enum('Other', 'Male', 'Female')),
        sa.Column('created_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
        sa.Column('updated_at', sa.TIMESTAMP(), server_default=sa.text('now()')),
        sa.Column('collect_and_user_personal_info', sa.Boolean, default=False),
    )


def downgrade():
    op.drop_table('users')
