# Project: <ECLRest>
- Line Manager: Lakmal Jayasooriya
- Application Owner: Nadini Dharmawardena
- Developer: Sithumi Gunathunga

## Application Overview

This Backend API is a Spring Boot backend service used to save transaction details of a E-Channelling Transaction made through Mobitel branches.
It handles the insertion of E-Channelling transaction data into the database (Informix) for record-keeping and reporting purposes.

## Application Architecture

The application follows a client–server architecture, where the client system (e.g., branch POS system, web portal) sends transaction requests, and the server processes, validates, and stores them in the database.Multi-Database Support (Informix & MySQL)
(images/architecture.PNG)

## Application Architecture Summery
- **Spring Boot API**: A RESTful API built with Spring Boot that handles HTTP requests from the frontend and performs business logic operations.
- **Database (MySQL/Informix)**: The backend connects to a MySQL database for retrieve some data and then validate and save them in Informix database.

## Backend Technology summery:
- Spring framwork: 3.5.4
- Java Version: 21
- DataBase: MySQL, Informix
- Jenkins URL:http://172.27.44.25:8080/blue/organizations/jenkins/ECHRest/detail/ECHRest/6/pipeline
- Health Probe URL: http://172.27.44.25:8080/app-health

### Guides
Use the following guides to understand how features work:

### Installation

1. **Clone the repository**
   ```sh
   git clone https://bitbucket.mobitel.lk/scm/ecr/echrest.git 

   ```

2. **Test the API:**
   ```sh
   Sample curl:
   curl --location 'https://cbpapi-stg.mobitel.lk/eclRest/update-ECL-transaction' \
   --header 'Content-Type: application/json' \
   --header 'Cookie: NSC_dcqbqj-tuh.npcjufm.ml-TTM-MC=ffffffffaf13320745525d5f4f58455e445a4a423660' \
   --data ' {
      "referenceNo" : "80247695",
      "contactNo" : "[MOBILE_NO]",
      "date" : "2025-08-05",
      "time" : "15:28:00",
      "totalTransactionAmount" : 1000.00,
      "mobitelCommission" : 50.00,
      "branchName" : "Sithumi",
      "branchCode" : "HEO",
      "userId" : "[EMP_NO]",
      "customerName": "Testing",
      "nic" : "[NIC_NO]",
      "payType" : "CA"
   } 
   ```  
  
   

