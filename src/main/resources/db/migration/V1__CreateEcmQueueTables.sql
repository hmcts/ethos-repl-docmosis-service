-- =====================================================
-- Author: HMCTS Reform
-- Migration: V1__CreateEcmQueueTables
-- Created: 2026-03-04
-- Description: Creates ECM queue tables in ethos-postgres.
--              This is the first Flyway migration after enabling baseline-on-migrate
--              for existing ethos environments.
-- =====================================================

CREATE TABLE IF NOT EXISTS create_updates_queue (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) UNIQUE NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    locked_until TIMESTAMP,
    locked_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_create_updates_queue_status ON create_updates_queue(status);
CREATE INDEX IF NOT EXISTS idx_create_updates_queue_created_at ON create_updates_queue(created_at);
CREATE INDEX IF NOT EXISTS idx_create_updates_queue_locked_until ON create_updates_queue(locked_until);
CREATE INDEX IF NOT EXISTS idx_create_updates_queue_status_locked ON create_updates_queue(status, locked_until);

COMMENT ON TABLE create_updates_queue IS 'Queue table for create-updates messages replacing Azure Service Bus';
COMMENT ON COLUMN create_updates_queue.status IS 'Message status: PENDING, PROCESSING, COMPLETED, FAILED';
COMMENT ON COLUMN create_updates_queue.locked_until IS 'Timestamp until which the message is locked for processing';
COMMENT ON COLUMN create_updates_queue.locked_by IS 'Identifier of the processor that locked this message';

CREATE TABLE IF NOT EXISTS update_case_queue (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(255) UNIQUE NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INTEGER NOT NULL DEFAULT 0,
    error_message TEXT,
    locked_until TIMESTAMP,
    locked_by VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_update_case_queue_status ON update_case_queue(status);
CREATE INDEX IF NOT EXISTS idx_update_case_queue_created_at ON update_case_queue(created_at);
CREATE INDEX IF NOT EXISTS idx_update_case_queue_locked_until ON update_case_queue(locked_until);
CREATE INDEX IF NOT EXISTS idx_update_case_queue_status_locked ON update_case_queue(status, locked_until);

COMMENT ON TABLE update_case_queue IS 'Queue table for update-case messages replacing Azure Service Bus';
COMMENT ON COLUMN update_case_queue.status IS 'Message status: PENDING, PROCESSING, COMPLETED, FAILED';
COMMENT ON COLUMN update_case_queue.locked_until IS 'Timestamp until which the message is locked for processing';
COMMENT ON COLUMN update_case_queue.locked_by IS 'Identifier of the processor that locked this message';
