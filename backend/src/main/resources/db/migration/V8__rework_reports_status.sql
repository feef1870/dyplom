CREATE TYPE report_status AS ENUM ('PENDING', 'ACCEPTED', 'REJECTED');

ALTER TABLE "work_reports" DROP COLUMN "is_accepted";

ALTER TABLE "work_reports"
    ADD COLUMN "status" report_status NOT NULL DEFAULT 'PENDING';

ALTER TABLE "work_reports"
    ADD COLUMN "feedback" TEXT;

ALTER TABLE "work_reports"
    ADD COLUMN "reviewed_at" TIMESTAMP WITH TIME ZONE;
