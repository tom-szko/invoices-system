CREATE DATABASE IF NOT EXISTS invoices_db;

USE invoices_db;

CREATE TABLE IF NOT EXISTS vat(
vat VARCHAR(5),
vat_value FLOAT,
PRIMARY KEY (vat)
);
INSERT IGNORE INTO vat(vat, vat_value) VALUES('VAT23', 0.23);
INSERT IGNORE INTO vat(vat, vat_value) VALUES('VAT8', 0.08);
INSERT IGNORE INTO vat(vat, vat_value) VALUES('VAT5', 0.05);
INSERT IGNORE INTO vat(vat, vat_value) VALUES('VAT0', 0.00);

CREATE TABLE IF NOT EXISTS unit_type(
unit_name VARCHAR(9),
unit_value VARCHAR(9),
PRIMARY KEY (unit_name)
);
INSERT IGNORE INTO unit_type(unit_name, unit_value) VALUES('PIECE', 'piece');
INSERT IGNORE INTO unit_type(unit_name, unit_value) VALUES('HOUR', 'hour');
INSERT IGNORE INTO unit_type(unit_name, unit_value) VALUES('DAY', 'day');
INSERT IGNORE INTO unit_type(unit_name, unit_value) VALUES('FLAT_RATE', 'flat rate');

CREATE TABLE IF NOT EXISTS invoice_type(
invoice_type VARCHAR(10),
invoice_value VARCHAR(10),
PRIMARY KEY (invoice_type)
);
INSERT IGNORE INTO invoice_type(invoice_type, invoice_value) VALUES('STANDARD', 'Standard');
INSERT IGNORE INTO invoice_type(invoice_type, invoice_value) VALUES('PRO_FORMA', 'Pro-forma');
INSERT IGNORE INTO invoice_type(invoice_type, invoice_value) VALUES('DEBIT_MEMO', 'Debit memo');

CREATE TABLE IF NOT EXISTS addresses(
id INT AUTO_INCREMENT,
street VARCHAR(255),
street_number VARCHAR(30),
postal_code VARCHAR(30),
city VARCHAR(50),
country VARCHAR(50),
PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS contact_details(
id INT AUTO_INCREMENT,
email VARCHAR(255),
phone_number VARCHAR(20),
website VARCHAR(50),
address int,
PRIMARY KEY (id),
FOREIGN KEY (address) REFERENCES addresses (id)
);

CREATE TABLE IF NOT EXISTS account_numbers(
id INT AUTO_INCREMENT,
iban_number VARCHAR(28),
local_number VARCHAR(26),
PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS companies(
id INT AUTO_INCREMENT,
company_name VARCHAR(255),
tax_id VARCHAR(30),
account_number int,
contact_details int,
PRIMARY KEY (id),
FOREIGN KEY (account_number) REFERENCES account_numbers (id),
FOREIGN KEY (contact_details) REFERENCES contact_details (id)
);

CREATE TABLE IF NOT EXISTS invoice_entry(
id INT AUTO_INCREMENT,
item VARCHAR(255),
quantity long,
unit_type varchar(9),
price numeric(8,2),
vat varchar(5),
net_value numeric(8,2),
gross_value numeric(8,2),
PRIMARY KEY (id),
FOREIGN KEY (unit_type) REFERENCES unit_type (unit_name),
FOREIGN KEY (vat) REFERENCES vat (vat)
);

CREATE TABLE IF NOT EXISTS invoice_entries(
id int,
entry int,
PRIMARY KEY (id),
FOREIGN KEY (entry) REFERENCES invoice_entry (id)
);

CREATE TABLE IF NOT EXISTS invoices(
id INT AUTO_INCREMENT,
invoice_type varchar(10),
issue_date date,
due_date date,
seller int,
buyer int,
entries int,
totalNetValue numeric(8,2),
totalGrossValue numeric(8,2),
comments text,
PRIMARY KEY (id),
FOREIGN KEY (invoice_type) REFERENCES invoice_type (invoice_type),
FOREIGN KEY (seller) REFERENCES companies (id),
FOREIGN KEY (buyer) REFERENCES companies (id),
FOREIGN KEY (entries) REFERENCES invoice_entries (id)
);

CREATE OR REPLACE VIEW invoices_view AS
SELECT invoices.id AS invoice_id,
invoices.invoice_type AS invoice_type,
invoices.issue_date AS issue_date,
invoices.due_date AS due_date,
company_seller.company_name AS seller_company_name,
company_seller.tax_id AS seller_company_tax_id,
account_seller.iban_number AS seller_company_iban_number,
account_seller.local_number AS seller_company_local_number,
contact_details_seller.email AS seller_company_email,
contact_details_seller.phone_number AS seller_company_phone_number,
contact_details_seller.website AS seller_company_website,
address_seller.street AS seller_company_street,
address_seller.street_number AS seller_company_street_number,
address_seller.postal_code AS seller_company_postal_code,
address_seller.city AS seller_company_city,
address_seller.country AS seller_company_country,
company_buyer.company_name AS buyer_company_name,
company_buyer.tax_id AS buyer_company_tax_id,
account_buyer.iban_number AS buyer_company_iban_number,
account_buyer.local_number AS buyer_company_local_number,
contact_details_buyer.email AS buyer_company_email,
contact_details_buyer.phone_number AS buyer_company_phone_number,
contact_details_buyer.website AS buyer_company_website,
address_buyer.street AS buyer_company_street,
address_buyer.street_number AS buyer_company_street_number,
address_buyer.postal_code AS buyer_company_postal_code,
address_buyer.city AS buyer_company_city,
address_buyer.country AS buyer_company_country,
invoices.total_net_value AS total_net_value,
invoices.total_gross_value AS total_gross_value,
invoices.comments AS comments,
invoice_entry.item AS item,
invoice_entry.quantity AS quantity,
invoice_entry.unit_type AS unit_type,
invoice_entry.price AS price,
invoice_entry.vat AS vat_rate,
invoice_entry.net_value AS net_value,
invoice_entry.gross_value AS gross_value
FROM invoices
JOIN companies company_seller ON company_seller.id=invoices.seller
JOIN companies company_buyer ON company_buyer.id=invoices.buyer
JOIN invoice_entries ON invoice_entries.id=invoices.id
JOIN invoice_entry ON invoice_entries.entry=invoice_entry.id
JOIN account_numbers account_buyer ON company_buyer.account_number=account_buyer.id
JOIN account_numbers account_seller ON company_seller.account_number=account_seller.id
JOIN contact_details contact_details_seller ON contact_details_seller.id=company_seller.contact_details
JOIN contact_details contact_details_buyer ON contact_details_buyer.id=company_buyer.contact_details
JOIN addresses address_seller ON address_seller.id=contact_details_seller.address
JOIN addresses address_buyer ON address_buyer.id=contact_details_buyer.address;