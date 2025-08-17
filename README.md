# 💊 Medical Store Inventory System

## 📌 Overview
The **Medical Store Inventory System** is a **Java-based desktop application** that helps manage a medical store’s inventory.  
It provides a **user-friendly Swing GUI** connected to a **MySQL database** for efficient management of medicines and suppliers.  
The system ensures smooth operations with CRUD features, search, and automatic alerts for stock and expiry.  

---

## ✨ Key Features
- 🗄️ **Database Management**  
  - Automatically creates the `MedicalStore` database and tables (`Medicines`, `Suppliers`) on first run.  

- ⚡ **CRUD Operations** for both medicines and suppliers:  
  - ➕ Create – Add new records  
  - 📖 Read – View all records (ArrayList / TreeSet with sorting support)  
  - ✏️ Update – Modify existing records by ID  
  - ❌ Delete – Safely remove records (prevents deleting suppliers linked to medicines)  

- 🔍 **Search Functionality**  
  - Search medicines by **name** or **batch number**  
  - Search suppliers by **name**  

- 🚨 **Alerts & Notifications**  
  - **Low Stock Alert** – Detects medicines at or below threshold quantity  
  - **Expiry Alert** – Highlights expired medicines or those expiring within 30 days  

---

## 🛠️ Technology Stack
- **Frontend:** Java Swing (GUI)  
- **Backend:** Java  
- **Database:** MySQL  
- **Connectivity:** JDBC (Java Database Connectivity)  

---

## 📦 Prerequisites
- ☕ Java Development Kit (**JDK 8+**)  
- 🐬 MySQL Database Server  
- 🔗 MySQL Connector/J (JDBC driver)  

---

## ⚙️ Setup Instructions
1. **Clone or Download the Project**  
   - Obtain the `MedicalStoreApp.java` file  

2. **Install & Configure MySQL**  
   - Ensure MySQL is installed and running  

3. **Add JDBC Driver**  
   - Download **MySQL Connector/J** and include it in your project classpath  

4. **Database Credentials**  
   - By default, the app connects with:
     ```java
     DriverManager.getConnection(
         "jdbc:mysql://localhost:3306/MedicalStore",
         "root",
         ""
     );
     ```
   - Update `getConnection()` in `MedicalStoreApp.java` if your MySQL credentials differ:  
     ```java
     private Connection getConnection() throws SQLException {
         return DriverManager.getConnection(
             "jdbc:mysql://localhost:3306/MedicalStore",
             "your_username",
             "your_password"
         );
     }
     ```

5. **Run the Application**  
   - Compile and execute:
     ```bash
     javac MedicalStoreApp.java
     java MedicalStoreApp
     ```
   - On first run, the application will automatically create the database and required tables.  

---

## 🖥️ Usage Guide
- 📂 **Load Data**  
  - Choose `Medicines` or `Suppliers`  
  - Select collection type: **ArrayList** or **TreeSet**  
  - Click **Load Data** to display records  

- 🛠️ **Perform CRUD**  
  - Use **Insert**, **Update**, or **Delete** buttons  
  - Follow on-screen prompts to manage records  

- 🔍 **Search**  
  - Search medicines by **Name** / **Batch No.**  
  - Search suppliers by **Name**  

- 🚨 **Check Alerts**  
  - Click **Low Stock** to see:  
    - Medicines below threshold quantity  
    - Expired or soon-to-expire medicines  

---

## 🚀 Future Enhancements
- 👥 Role-based user authentication (Admin / Staff)  
- 🧾 Billing & invoice generation  
- 📊 Export reports (PDF / Excel)  
- ☁️ Cloud database support  

---

## 👨‍💻 Author
Developed by **Rohit H**  
📧 Email: your-email@example.com  
🌐 GitHub: [Rohit9964](https://github.com/Rohit9964)

---

## 📝 License
Licensed under the **MIT License** – feel free to use, modify, and distribute.
