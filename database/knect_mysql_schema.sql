-- ============================================================================
-- KNECT - DIRECTORIO VIRTUAL DE ESTABLECIMIENTOS
-- Script DDL MySQL 8.0+ basado en el Diagrama MER Mejorado
-- ============================================================================

CREATE DATABASE IF NOT EXISTS knect_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE knect_db;

CREATE TABLE IF NOT EXISTS permissions (
    permission_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

CREATE TABLE IF NOT EXISTS roles_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_perm FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    phone VARCHAR(30),
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_active TINYINT(1) DEFAULT 1,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS establishment_types (
    establishment_types_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    icon VARCHAR(50),
    description TEXT
);

CREATE TABLE IF NOT EXISTS businesses (
    business_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_user_id INT,
    establishment_type_id INT,
    name VARCHAR(150) NOT NULL,
    legal_name VARCHAR(180),
    tax_number VARCHAR(50),
    description TEXT,
    description_en TEXT,
    primary_color VARCHAR(20) DEFAULT '#00A3FF',
    logo_url LONGTEXT,
    banner_url LONGTEXT,
    neighborhood VARCHAR(120),
    address VARCHAR(250),
    phone VARCHAR(40),
    whatsapp VARCHAR(40),
    email VARCHAR(150),
    website VARCHAR(255),
    is_ghost_kitchen TINYINT(1) DEFAULT 0,
    has_dine_in TINYINT(1) DEFAULT 1,
    is_featured TINYINT(1) DEFAULT 0,
    is_active TINYINT(1) DEFAULT 1,
    rating_avg DOUBLE DEFAULT 0.0,
    review_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    search_appearance_count INT DEFAULT 0,
    whatsapp_click_count INT DEFAULT 0,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_bus_owner FOREIGN KEY (owner_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    CONSTRAINT fk_bus_type FOREIGN KEY (establishment_type_id) REFERENCES establishment_types(establishment_types_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS establishments (
    establishment_id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT NOT NULL,
    establishment_type_id INT,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    schedules TEXT,
    neighborhood VARCHAR(120),
    address VARCHAR(250),
    latitude DOUBLE,
    longitude DOUBLE,
    map TEXT,
    phone VARCHAR(40),
    whatsapp VARCHAR(40),
    email VARCHAR(150),
    website VARCHAR(255),
    is_ghost_kitchen TINYINT(1) DEFAULT 0,
    has_dine_in TINYINT(1) DEFAULT 1,
    is_active TINYINT(1) DEFAULT 1,
    whatsapp_clicks INT DEFAULT 0,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_est_bus FOREIGN KEY (business_id) REFERENCES businesses(business_id) ON DELETE CASCADE,
    CONSTRAINT fk_est_type FOREIGN KEY (establishment_type_id) REFERENCES establishment_types(establishment_types_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS reviews (
    review_id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT NOT NULL,
    establishment_id INT,
    user_id INT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    admin_reply TEXT,
    admin_reply_at DATETIME,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_rev_bus FOREIGN KEY (business_id) REFERENCES businesses(business_id) ON DELETE CASCADE,
    CONSTRAINT fk_rev_est FOREIGN KEY (establishment_id) REFERENCES establishments(establishment_id) ON DELETE SET NULL,
    CONSTRAINT fk_rev_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS blog_posts (
    post_id INT AUTO_INCREMENT PRIMARY KEY,
    media_id INT,
    title VARCHAR(250) NOT NULL,
    title_en VARCHAR(250),
    slug VARCHAR(250) NOT NULL UNIQUE,
    summary TEXT,
    summary_en TEXT,
    content LONGTEXT,
    content_en LONGTEXT,
    image_url LONGTEXT,
    author_name VARCHAR(150),
    published_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS media (
    media_id INT AUTO_INCREMENT PRIMARY KEY,
    business_id INT,
    post_id INT,
    url LONGTEXT NOT NULL,
    type VARCHAR(50),
    is_primary TINYINT(1) DEFAULT 0,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS view (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    additional_data LONGTEXT,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS section (
    section_id INT AUTO_INCREMENT PRIMARY KEY,
    view_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    additional_data LONGTEXT,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sec_view FOREIGN KEY (view_id) REFERENCES view(view_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS section_data (
    section_data_id INT AUTO_INCREMENT PRIMARY KEY,
    section_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    content LONGTEXT,
    additional_data LONGTEXT,
    type VARCHAR(50),
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_sd_sec FOREIGN KEY (section_id) REFERENCES section(section_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS contact_messages (
    contact_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    email_sent TINYINT(1) DEFAULT 1,
    is_read TINYINT(1) DEFAULT 0,
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS analytics_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    business_id INT,
    establishment_id INT,
    search_query VARCHAR(250),
    create_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
