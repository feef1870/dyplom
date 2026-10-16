ALTER TABLE "work_reports"
    ADD COLUMN "author_id" BIGINT NOT NULL;

ALTER TABLE "work_reports"
    ADD CONSTRAINT fk_report_author
        FOREIGN KEY (author_id)
            REFERENCES workers(id);

CREATE INDEX idx_work_reports_author_id
    ON "work_reports" ("author_id");