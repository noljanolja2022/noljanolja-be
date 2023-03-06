FROM liquibase/liquibase

ARG SERVICE

WORKDIR "/liquibase/changelog"

COPY "./*.sql" "./"

COPY "./main-changelog.yml" "./"

ENV INSTALL_MYSQL=true

CMD [ \
    "sh", \
    "-c", \
    "liquibase --url=\"jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?user=${DB_USER}&password=${DB_PASS}\" --changelog-file=main-changelog.yml --log-level=info --contexts=\"${MIGRATE_CONTEXT}\" update" \
]
