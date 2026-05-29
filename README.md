# Grocery Inventory App

By: MANALO, Rommel P. (2024-00584-SR-0) - BSIT 2-3
For: OOP Workshop Activity

This is a JavaFX desktop application for grocery store inventory management. It was built with Java 21, Maven, JavaFX/FXML, Supabase PostgreSQL via JDBC, and BCrypt password hashing.

The app supports product inventory CRUD, user login, role-based access, admin-managed employee accounts, and an activity log for important actions such as logins, product changes, and account updates.

## Main Features

- Product inventory management: add, update, delete, search, and view grocery products.
- Login system: users sign in with BCrypt-verified passwords stored in Supabase.
- Role-based permissions: Admin, Operator, and Viewer accounts see different parts of the app.
- Account management: admins can create and update employee accounts.
- Activity log: records authentication, inventory, and account-management activity.

## Test Accounts

### Admin

Username: `admin123`  
Password: `adminpassword`

Admins can manage inventory, view activity logs, and create or update employee accounts.

### Operator

Username: `Employee`  
Password: `Employeepassword`

Operators can manage inventory and view activity logs. They cannot create or update accounts.

### Viewer

Username: `Vieweronly`  
Password: `Viewerpassword`

Viewers can only view and search the inventory. They cannot edit products, view activity logs, or manage accounts.
