ALTER TABLE "clients"
    ADD CONSTRAINT uq_clients_name UNIQUE ("email");

ALTER TABLE "clients"
    ALTER COLUMN "email" SET NOT NULL;

ALTER TABLE "price_tags"
    ADD CONSTRAINT uq_price_tags_name UNIQUE ("name");