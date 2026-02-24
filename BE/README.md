# Finance Tracker Backend App

## Aplikacija podrzava:
- Kreiranje racuna ```POST api/accounts```
- Azuriranje racuna ```PUT api/accounts/{id}```
- Brisanje racuna ```DELETE api/accounts/{id}```
- BULK Insert-ovanje racuna ```POST api/accounts/bulk```
- getAll racuna ```GET api/accounts/getAccounts```
- Kreiranje zaposlenog ```POST api/employees```
- getAll zaposlenog ```GET api/employees```
- getById zaposlenog ```GET api/employees/{id}```
- Azuriranje zaposlenog ```PUT api/employees/{id}```
- Brisanje zaposlenog ```DELETE api/employees/{id}```
- BULK Insert-ovanje zaposlenog ```POST api/employees/bulkSaveAll```

## Tehnologije
- Java 17+
- Maven
- Spring Boot 3.2.2
- Spring Data JPA
- Hibernate
- Jakarta Validation
- Lombok 1.18.42
- MySQL