CREATE INDEX idx_work_steps_order_id
    ON "work_steps" ("order_id");

CREATE INDEX idx_work_tasks_work_step_id
    ON "work_tasks" ("work_step_id");

CREATE INDEX idx_work_tasks_price_tag_id
    ON "work_tasks" ("price_tag_id");

CREATE INDEX idx_work_reports_work_task_id
    ON "work_reports" ("work_task_id");

CREATE INDEX idx_work_reports_work_step_id
    ON "work_reports" ("work_step_id");

CREATE INDEX idx_work_report_photos_work_report_id
    ON "work_report_photos" ("work_report_id");

CREATE INDEX idx_workers_assigned_work_task_id
    ON "workers_assigned" ("work_task_id");


ALTER TABLE "work_steps"
    DROP CONSTRAINT fk_work_step_order;

ALTER TABLE "work_steps"
    ADD CONSTRAINT fk_work_step_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
            ON DELETE CASCADE;

ALTER TABLE "work_tasks"
    DROP CONSTRAINT fk_work_task_work_step;

ALTER TABLE "work_tasks"
    ADD CONSTRAINT fk_work_task_work_step
        FOREIGN KEY (work_step_id) REFERENCES work_steps(id)
            ON DELETE CASCADE;

ALTER TABLE "work_reports"
    DROP CONSTRAINT fk_report_work_step;

ALTER TABLE "work_reports"
    ADD CONSTRAINT fk_report_work_step
        FOREIGN KEY (work_step_id) REFERENCES work_steps(id)
            ON DELETE CASCADE;

ALTER TABLE "work_reports"
    DROP CONSTRAINT fk_report_work_task;

ALTER TABLE "work_reports"
    ADD CONSTRAINT fk_report_work_task
        FOREIGN KEY (work_task_id) REFERENCES work_tasks(id)
            ON DELETE CASCADE;

ALTER TABLE "workers_assigned"
    DROP CONSTRAINT fk_assigned_worker;

ALTER TABLE "workers_assigned"
    ADD CONSTRAINT fk_assigned_worker
        FOREIGN KEY (worker_id) REFERENCES workers(id)
            ON DELETE CASCADE;

ALTER TABLE "workers_assigned"
    DROP CONSTRAINT fk_assigned_work_task;

ALTER TABLE "workers_assigned"
    ADD CONSTRAINT fk_assigned_work_task
        FOREIGN KEY (work_task_id) REFERENCES work_tasks(id)
            ON DELETE CASCADE;