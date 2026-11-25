-- FitWell LK - MySQL database setup
-- Run in MySQL Workbench or: mysql -u root -p < database/fitwell_lk_schema.sql
-- The backend creates the same schema automatically with Flyway on first start
-- (fitwell-backend/src/main/resources/db/migration). This script is for manual setup and reference.

CREATE DATABASE IF NOT EXISTS fitwell_lk CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fitwell_lk;


CREATE TABLE users (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    full_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    password_hash       VARCHAR(100) NOT NULL,
    daily_water_goal_ml INT          NOT NULL DEFAULT 2500,
    created_at          DATETIME(6)  NOT NULL,
    updated_at          DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE workout_plans (
    id               VARCHAR(36)  NOT NULL,
    user_id          BIGINT       NOT NULL,
    name             VARCHAR(100) NOT NULL,
    description      VARCHAR(500),
    category         VARCHAR(30)  NOT NULL,
    difficulty       VARCHAR(20)  NOT NULL,
    duration_minutes INT          NOT NULL,
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_plans_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_plans_user ON workout_plans (user_id, difficulty);

CREATE TABLE plan_exercises (
    plan_id      VARCHAR(36)  NOT NULL,
    position     INT          NOT NULL,
    name         VARCHAR(100) NOT NULL,
    sets         INT          NOT NULL,
    reps         INT          NOT NULL,
    rest_seconds INT          NOT NULL,
    PRIMARY KEY (plan_id, position),
    CONSTRAINT fk_exercises_plan FOREIGN KEY (plan_id) REFERENCES workout_plans (id) ON DELETE CASCADE
);

CREATE TABLE workout_sessions (
    id               VARCHAR(36)  NOT NULL,
    user_id          BIGINT       NOT NULL,
    plan_id          VARCHAR(36),
    workout_type     VARCHAR(30)  NOT NULL,
    duration_minutes INT          NOT NULL,
    calories         INT,
    performed_at     DATETIME(6)  NOT NULL,
    notes            VARCHAR(500),
    created_at       DATETIME(6)  NOT NULL,
    updated_at       DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_sessions_plan FOREIGN KEY (plan_id) REFERENCES workout_plans (id) ON DELETE SET NULL
);
CREATE INDEX idx_sessions_user_date ON workout_sessions (user_id, performed_at);

CREATE TABLE hydration_records (
    id         VARCHAR(36) NOT NULL,
    user_id    BIGINT      NOT NULL,
    amount_ml  INT         NOT NULL,
    drink_type VARCHAR(20) NOT NULL,
    logged_at  DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_hydration_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_hydration_user_date ON hydration_records (user_id, logged_at);

CREATE TABLE wellness_records (
    id          VARCHAR(36)   NOT NULL,
    user_id     BIGINT        NOT NULL,
    entry_date  DATE          NOT NULL,
    weight_kg   DOUBLE,
    sleep_hours DOUBLE,
    mood        INT           NOT NULL,
    notes       VARCHAR(1000),
    created_at  DATETIME(6)   NOT NULL,
    updated_at  DATETIME(6)   NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wellness_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_wellness_user_date ON wellness_records (user_id, entry_date);

CREATE TABLE fitness_goals (
    id            VARCHAR(36)  NOT NULL,
    user_id       BIGINT       NOT NULL,
    title         VARCHAR(100) NOT NULL,
    goal_type     VARCHAR(30)  NOT NULL,
    target_value  DOUBLE       NOT NULL,
    current_value DOUBLE       NOT NULL,
    unit          VARCHAR(20)  NOT NULL,
    start_date    DATE         NOT NULL,
    target_date   DATE         NOT NULL,
    created_at    DATETIME(6)  NOT NULL,
    updated_at    DATETIME(6)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_goals_user ON fitness_goals (user_id, target_date);
