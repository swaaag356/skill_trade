CREATE TABLE users (
                       id            INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
                       username      VARCHAR(50)  NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       email         VARCHAR(100) NOT NULL UNIQUE,
                       about         TEXT,
                       rating        FLOAT DEFAULT 0.0 CHECK (rating >= 0 AND rating <= 5)
);

CREATE TABLE skills (
                        id   INTEGER PRIMARY KEY DEFAULT nextval('skill_seq'),
                        name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE user_skills (
                             user_id  INTEGER NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
                             skill_id INTEGER NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
                             PRIMARY KEY (user_id, skill_id)
);

CREATE TABLE trade_offers (
                              id               INTEGER PRIMARY KEY DEFAULT nextval('trade_offer_seq'),
                              user_id          INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                              offer_skill_id   INTEGER NOT NULL REFERENCES skills(id) ON DELETE RESTRICT,
                              request_skill_id INTEGER NOT NULL REFERENCES skills(id) ON DELETE RESTRICT,
                              description      TEXT,
                              status           VARCHAR(20) DEFAULT 'active'
                                  CHECK (status IN ('active', 'in_progress', 'done')),
                              created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trade_responses (
                                 id              INTEGER PRIMARY KEY DEFAULT nextval('trade_response_seq'),
                                 trade_offer_id  INTEGER NOT NULL REFERENCES trade_offers(id) ON DELETE CASCADE,
                                 responder_id    INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 message         TEXT,
                                 created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reviews (
                         id           INTEGER PRIMARY KEY DEFAULT nextval('review_seq'),
                         from_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         to_user_id   INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         rating       INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment      TEXT,
                         created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT chk_not_self_review CHECK (from_user_id <> to_user_id)
);

-- Поиск по навыкам
CREATE INDEX idx_skills_name ON skills(name);

-- Поиск предложений по статусу
CREATE INDEX idx_trade_offers_status ON trade_offers(status);

-- Поиск откликов по предложению
CREATE INDEX idx_trade_responses_offer ON trade_responses(trade_offer_id);

-- Рейтинг пользователей
CREATE INDEX idx_users_rating ON users(rating DESC);