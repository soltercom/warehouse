DROP TABLE IF EXISTS ITEMS_BALANCE;
DROP TABLE IF EXISTS TRANSFERS_ROW;
DROP TABLE IF EXISTS PURCHASES_ROW;
DROP TABLE IF EXISTS RECEIPTS_ROW;
DROP TABLE IF EXISTS TRANSFERS;
DROP TABLE IF EXISTS PURCHASES;
DROP TABLE IF EXISTS RECEIPTS;
DROP TABLE IF EXISTS ITEMS;
DROP TABLE IF EXISTS WAREHOUSES;

CREATE TABLE IF NOT EXISTS ITEMS (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    INDEX(name)
 );

CREATE TABLE IF NOT EXISTS WAREHOUSES (
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    INDEX(name)
);

CREATE TABLE IF NOT EXISTS RECEIPTS(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    warehouse_id INTEGER,
    comment VARCHAR(255),
    CONSTRAINT FK_RECEIPTS_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES WAREHOUSES(id)
);

CREATE TABLE IF NOT EXISTS RECEIPTS_ROW(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    receipt_id INTEGER,
    item_id INTEGER,
    quantity DECIMAL(15, 3),
    CONSTRAINT FK_RECEIPTS_ROW_RECEIPTS FOREIGN KEY (receipt_id) REFERENCES RECEIPTS(id),
    CONSTRAINT FK_RECEIPTS_ROW_ITEM FOREIGN KEY (item_id) REFERENCES ITEMS(id)
);

CREATE TABLE IF NOT EXISTS PURCHASES(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   date DATE,
   warehouse_id INTEGER,
   comment VARCHAR(255),
   CONSTRAINT FK_PURCHASES_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES WAREHOUSES(id)
);

CREATE TABLE IF NOT EXISTS PURCHASES_ROW(
   id INTEGER AUTO_INCREMENT PRIMARY KEY,
   purchase_id INTEGER,
   item_id INTEGER,
   quantity DECIMAL(15, 3),
   CONSTRAINT FK_PURCHASES_ROW_PURCHASES FOREIGN KEY (purchase_id) REFERENCES PURCHASES(id),
   CONSTRAINT FK_PURCHASES_ROW_ITEM FOREIGN KEY (item_id) REFERENCES ITEMS(id)
);

CREATE TABLE IF NOT EXISTS TRANSFERS(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    warehouse_from_id INTEGER,
    warehouse_to_id INTEGER,
    comment VARCHAR(511),
    CONSTRAINT FK_TRANSFERS_WAREHOUSE_FROM FOREIGN KEY (warehouse_from_id) REFERENCES WAREHOUSES(id),
    CONSTRAINT FK_TRANSFERS_WAREHOUSE_TO FOREIGN KEY (warehouse_to_id) REFERENCES WAREHOUSES(id)
);

CREATE TABLE IF NOT EXISTS TRANSFERS_ROW(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    transfer_id INTEGER,
    item_id INTEGER,
    quantity DECIMAL(15, 3),
    CONSTRAINT FK_TRANSFERS_ROW_TRANSFERS FOREIGN KEY (transfer_id) REFERENCES TRANSFERS(id),
    CONSTRAINT FK_TRANSFERS_ROW_ITEM FOREIGN KEY (item_id) REFERENCES ITEMS(id)
);

CREATE TABLE IF NOT EXISTS ITEMS_BALANCE(
    id INTEGER AUTO_INCREMENT PRIMARY KEY,
    recorder_id INTEGER,
    recorder_type VARCHAR(100),
    sign TINYINT(1),
    date DATE,
    warehouse_id INTEGER,
    item_id INTEGER,
    quantity DECIMAL(15, 3),
    CONSTRAINT FK_ITEMS_BALANCE_WAREHOUSE FOREIGN KEY (warehouse_id) REFERENCES WAREHOUSES(id),
    CONSTRAINT FK_ITEMS_BALANCE_ITEM FOREIGN KEY (item_id) REFERENCES ITEMS(id),
    INDEX(recorder_id, recorder_type)
);
