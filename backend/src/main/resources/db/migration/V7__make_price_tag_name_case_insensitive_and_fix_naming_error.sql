ALTER TABLE "price_tags" DROP CONSTRAINT uq_price_tags_name;
CREATE UNIQUE INDEX uq_price_tags_name_lower
    ON "price_tags" (lower("name"));

ALTER TABLE "clients" RENAME CONSTRAINT uq_clients_name TO uq_clients_email;

ALTER TABLE "clients" DROP CONSTRAINT uq_clients_email;
CREATE UNIQUE INDEX uq_clients_email_lower
    ON "clients" (lower("email"));