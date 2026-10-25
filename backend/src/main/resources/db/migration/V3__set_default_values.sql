ALTER TABLE "orders" ALTER COLUMN "status" SET DEFAULT 'PLANNED'::status;
ALTER TABLE "work_steps" ALTER COLUMN "status" SET DEFAULT 'PLANNED'::status;
ALTER TABLE "work_tasks" ALTER COLUMN "status" SET DEFAULT 'PLANNED'::status;
ALTER TABLE "work_reports" ALTER COLUMN "is_accepted" SET DEFAULT FALSE;