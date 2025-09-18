Det her er mit projekt til SP1, hvor jeg har bygget en backend til at holde styr på en masse film.

Hvad kan den? 
Systemet henter en masse data om danske film (og co-produktioner) fra de sidste 5 år fra The Movie Database (TMDb) API og gemmer dem i vores egen PostgreSQL-database.

Når data er gemt, kan programmet blandt andet:

Vise en liste over alle film, skuespillere eller genrer.

Søge efter en film ud fra dens titel (hvor den er ligeglad med store/små bogstaver).

Finde de højest, lavest og mest populære film.

Udregne den gennemsnitlige rating for alle film i databasen.

Tilføje, opdatere og slette film.

Tech Stack 
jeg har brugt en bunke forskellige teknologier til at bygge det her:
Sprog: Java 17
Build Tool: Maven
Database: PostgreSQL
ORM: JPA / Hibernate
Test: JUnit 5 & Testcontainers (så vi kan teste på en rigtig database i Docker)
API Kommunikation: Javas indbyggede HttpClient
JSON Håndtering: Jackson
Mindre tastearbejde: Lombok

Sådan køres det:
For at få projektet op at køre på din egen computer, skal du gøre følgende:

1. Klon repo'et
Først og fremmest, hent koden ned.
git clone https://github.com/din-bruger/Movie_Repository_SP1.git

3. Sæt din database op
Sørg for, at du har PostgreSQL kørende på din maskine. Opret en tom database. Jeg har kaldt min movierepository, men du kan kalde den, hvad du vil.

4. Lav en config.properties fil
Denne fil indeholder alle hemmelighederne, som f.eks. API-nøgle og database-adgangskode.

Opret filen her: src/main/resources/config.properties
Smid følgende indhold i filen og ret værdierne, så de passer til dit setup:
Properties

# Dine database-indstillinger
DB_NAME=movierepository
DB_USERNAME=postgres
DB_PASSWORD=dit_postgres_password

# Din API nøgle fra TMDb
TMDB_API_KEY=indsæt_din_lange_api_nøgle_her

4. Kør data-importen
Nu skal vi have fyldt databasen med film.
Første gang du kører, skal du ændre hibernate.hbm2ddl.auto står til create i HibernateConfig.java. Det bygger alle tabellerne på ny. 

Kør main-metoden i app/Main.java. Programmet vil nu hente dataen. 

Når du har kørt importen én gang, så ret hibernate.hbm2ddl.auto til update i HibernateConfig.java. Ellers sletter du hele din database, næste gang du starter programmet!

5. Kør testene
For at tjekke at alt virker, kan du køre mine tests. Højreklik på src/test/java-mappen i IntelliJ og vælg "Run 'All Tests'". Dette vil starte en midlertidig database i Docker og køre alle JUnit-tests.

Hvem har lavet det?
Jonathan
