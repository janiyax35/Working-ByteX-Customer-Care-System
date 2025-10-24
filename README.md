<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:2a2a32,100:131316&height=200&section=header&text=ByteX%20Customer%20Care%20System&fontSize=40&fontColor=ffffff&animation=fadeIn&fontAlignY=38&desc=Comprehensive%20IT%20Support%20Management%20Solution&descAlignY=60&descAlign=50" width="100%">
</div>

<p align="center">
  <a href="https://www.java.com"><img src="https://img.shields.io/badge/Java-17-orange.svg?style=for-the-badge&logo=java&logoColor=white" alt="Java 17"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg?style=for-the-badge&logo=spring-boot" alt="Spring Boot"></a>
  <a href="https://www.mysql.com"><img src="https://img.shields.io/badge/MySQL-8.0-blue.svg?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL"></a>
  <a href="https://www.thymeleaf.org"><img src="https://img.shields.io/badge/Thymeleaf-Latest-green.svg?style=for-the-badge&logo=thymeleaf&logoColor=white" alt="Thymeleaf"></a>
  <a href="#"><img src="https://img.shields.io/badge/JavaScript-ES6+-yellow?style=for-the-badge&logo=javascript" alt="JavaScript"></a>
</p>

<div align="center">
  <h3>
    <a href="#overview">Overview</a> •
    <a href="#key-features">Features</a> •
    <a href="#system-architecture">Architecture</a> •
    <a href="#installation">Installation</a> •
    <a href="#usage">Usage</a> •
    <a href="#tech-stack">Tech Stack</a> •
    <a href="#screenshots">Screenshots</a> •
    <a href="#contributor">Contributor</a> •
    <a href="#license">License</a>
  </h3>
</div>

<div align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Share+Tech+Mono&duration=2000&pause=1000&color=00EEFF&center=true&vCenter=true&random=false&width=600&lines=Role-Based+Access+Control;Ticket+Management+System;Inventory+and+Parts+Management;Repair+Tracking;Supplier+Management;Comprehensive+Reporting" alt="Project Features"/>
</div>

<hr>

## Overview

ByteX Customer Care System is a comprehensive, role-based platform designed to streamline customer support operations, repair tracking, and inventory management. It provides a unified solution for IT hardware repair businesses to manage their entire workflow from initial customer contact through to repair completion and parts management.

The system features dedicated modules for various stakeholders:
- 👤 **Customers**: Submit and track support tickets
- 👨‍💼 **Staff**: Manage customer communications and ticket assignment
- 🔧 **Technicians**: Handle repairs and request parts
- 📦 **Product Managers**: Oversee product catalog and repair parts
- 🏭 **Warehouse Managers**: Manage inventory and supplier relations
- 🔐 **Administrators**: System configuration and user management

## Key Features

<table>
  <tr>
    <td width="50%">
      <h3>🎫 Ticket Management</h3>
      <ul>
        <li>Create, update and track customer support tickets</li>
        <li>Priority-based ticket handling</li>
        <li>Multi-stage workflow (Customer → Staff → Technician → Resolution)</li>
        <li>File attachments support for problem documentation</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🔧 Repair Management</h3>
      <ul>
        <li>Detailed repair tracking from diagnosis to completion</li>
        <li>Parts request workflow for technicians</li>
        <li>Repair history and documentation</li>
        <li>Technician assignment and workload management</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>📦 Inventory Control</h3>
      <ul>
        <li>Comprehensive parts catalog</li>
        <li>Stock level monitoring with alerts</li>
        <li>Automated reordering thresholds</li>
        <li>Part usage tracking across repairs</li>
      </ul>
    </td>
    <td width="50%">
      <h3>🏭 Supplier Management</h3>
      <ul>
        <li>Supplier database with contact information</li>
        <li>Purchase order creation and tracking</li>
        <li>Delivery schedule management</li>
        <li>Parts to supplier mapping</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
      <h3>👥 Role-Based Access</h3>
      <ul>
        <li>Six distinct user roles with appropriate permissions</li>
        <li>Secure authentication system</li>
        <li>Activity logging for accountability</li>
        <li>Role-specific dashboards and interfaces</li>
      </ul>
    </td>
    <td width="50%">
      <h3>📊 Reporting & Analytics</h3>
      <ul>
        <li>Repair statistics and performance metrics</li>
        <li>Inventory utilization reports</li>
        <li>Customer satisfaction tracking</li>
        <li>Financial and operational insights</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td width="50%">
    <h3>🎨 Enhanced User Interface</h3>
    <ul>
      <li>**Multiple Themes**: User-selectable color themes (Default Green, Blue, Orange, Purple) stored in local storage.</li>
      <li>**Custom Mouse Cursor**: A unique, arrow-shaped cursor that changes appearance on hover.</li>
      <li>**Custom Context Menu**: Right-click menu providing quick navigation options (Back, Reload, Dashboard, Profile, Logout).</li>
      <li>Modern dark-mode aesthetic with interactive elements.</li>
    </ul>
  </td>
  <td width="50%">
  </tr>
</table>

## System Architecture

The ByteX Customer Care System is built on a modern, layered architecture:

```
┌─────────────────────────────────────────────────┐
│                  Web Interface                  │
│             (Thymeleaf + HTML/CSS)              │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│              Spring MVC Controllers             │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│                Service Layer                    │
│       (Business Logic & Process Workflows)      │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│           Spring Data JPA Repositories          │
└───────────────────────┬─────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────┐
│                  MySQL Database                 │
│    (Tickets, Users, Repairs, Parts, Orders)     │
└─────────────────────────────────────────────────┘
```

## Installation

### Prerequisites

- Java JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

### Setup Instructions

1.  **Clone the repository**
    ```bash
    git clone https://github.com/janiyax35/Working-ByteX-Customer-Care-System
    cd Working-ByteX-Customer-Care-System
    ```
    *Ensure the static files are placed correctly:*
      * Theme CSS files (`theme-*.css`) should be in `src/main/resources/static/css/themes/`.
      * `theme-switcher.js` should be in `src/main/resources/static/js/`.
      * `style.css` should be in `src/main/resources/static/css/`.

2. **Configure MySQL database**
   ```bash
   mysql -u root -p
   ```
   ```sql
   CREATE DATABASE v2_12bytex_database;
   exit;
   ```
   
3. **Run the database schema script**
   ```bash
   mysql -u root -p v2_12bytex_database < "Working Code.sql"
   ```

4. **Update database configuration**
   
   Edit `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/v2_12bytex_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

7. **Access the application**
   
   Open your browser and navigate to: `http://localhost:8080`

## Usage

### Login Credentials

The system comes pre-populated with sample users for each role:

| Username  | Password  | Role              |
|-----------|-----------|-------------------|
| admin     | admin123  | Administrator     |
| sf1       | staff123  | Staff Member      |
| tech1     | tech123   | Technician        |
| pm1       | pm123     | Product Manager   |
| wm        | wm123     | Warehouse Manager |
| customer1 | pass123   | Customer          |

### Workflow Example

1. **Customer** creates a new support ticket for a computer issue
2. **Staff** reviews the ticket and assigns it to a **Technician**
3. **Technician** diagnoses the problem and requests parts from the **Product Manager**
4. **Product Manager** approves part requests or orders new parts from **Warehouse Manager**
5. **Warehouse Manager** manages inventory and places orders with suppliers if needed
6. **Technician** completes the repair once parts are available
7. **Staff** communicates completion to the customer and closes the ticket

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.5.4, Spring Data JPA
- **Frontend**: Thymeleaf, HTML5, CSS3, JavaScript (ES6+)
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Version Control**: Git

## Screenshots

<div align="center">
  <p><i>Screenshots will be added soon</i></p>
</div>

## Contributor

<div align="center">
  <a href="https://github.com/janiyax35">
    <img src="https://avatars.githubusercontent.com/u/janiyax35" width="100px" style="border-radius:50%;" alt="Janith Deshan"/>
    <br />
    <b>Janith Deshan</b>
    <br />
    BSc (Hons) Information Technology Specialized in Cyber Security
    <br />
    <sub>Sri Lanka Institute of Information Technology (SLIIT)</sub>
    <br />
    <a href="https://github.com/janiyax35">
      <img src="https://img.shields.io/badge/GitHub-janiyax35-00eeff?style=flat-square&logo=github&logoColor=white&labelColor=black" alt="GitHub"/>
    </a>
  </a>
</div>

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

<div align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:131316,100:2a2a32&height=120&section=footer&text=ByteX%20Solutions&fontSize=20&fontColor=ffffff&animation=fadeIn&fontAlignY=70" width="100%">
</div>
