# Retail Analytics

Applicazione Spring Boot per l'analisi delle vendite di una rete di negozi: API REST, front-end Thymeleaf
e viste a stella pronte per Power BI.

## Stack

| Ambito | Scelta |
|---|---|
| Linguaggio e build | Java 21, Maven (wrapper incluso) |
| Framework | Spring Boot 4.1.2: Web MVC, Data JPA, Validation, Thymeleaf, Actuator |
| Database | MySQL 8, schema gestito da Flyway |
| Documentazione API | springdoc-openapi 3.2.0 (Swagger UI) |
| Test | JUnit, Mockito, MockMvc, Testcontainers |
| Analisi | viste `v_dim_*` e `v_fact_*`, query M e misure DAX in `powerbi/` |

## Avvio rapido

1. Crea database e utenti nel container MySQL (una volta sola), da PowerShell nella cartella del progetto:

   ```powershell
   Get-Content db\init\01-create-database.sql | docker exec -i NOME_CONTAINER mysql -uroot -pPASSWORD_ROOT
   ```

2. Avvia l'applicazione:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   In Eclipse: *Import > Existing Maven Projects*, poi *Run As > Spring Boot App* (o *Java Application*)
   sulla classe `RetailAnalyticsApplication`.

3. Apri:

   | Indirizzo | Contenuto |
   |---|---|
   | http://localhost:8081/ | dashboard |
   | http://localhost:8081/stores | gestione negozi |
   | http://localhost:8081/sales | vendite |
   | http://localhost:8081/swagger-ui.html | documentazione delle API |
   | http://localhost:8081/actuator/health | stato dell'applicazione |

Al primo avvio Flyway crea le tabelle e carica i dati di esempio: 3 distretti, 12 negozi, 24 prodotti
e circa 35.000 righe di vendita dal 2025-01-01 al 2026-10-31.

## Configurazione

I valori di connessione hanno un default e si sovrascrivono con variabili d'ambiente.

| Variabile | Default | Significato |
|---|---|---|
| `DB_HOST` | `localhost` | host di MySQL |
| `DB_PORT` | `3310` | porta pubblicata dal container |
| `DB_NAME` | `retail_analytics` | database |
| `DB_USER` | `retail` | utente applicativo |
| `DB_PASSWORD` | `retail` | password (solo sviluppo) |

Le credenziali presenti nel repository (`retail`, `retail_bi` e la root dello stack Compose) sono valori
di sviluppo, validi solo per un database locale con dati di esempio. In qualsiasi altro ambiente le password
si passano con le variabili d'ambiente e non vanno salvate nei file.

## Test

```powershell
.\mvnw.cmd test      # test unitari e di strato web: non serve Docker
.\mvnw.cmd verify    # aggiunge i test di integrazione con Testcontainers: serve Docker avviato
```

`verifica.bat` esegue `verify` e scrive l'esito in `verifica-log.txt`.

## Struttura

```
src/main/java/io/github/auticad/retail
  api/         controller REST e gestione errori (ProblemDetail)
  web/         controller MVC e form per le pagine Thymeleaf
  service/     regole di business e transazioni
  repository/  Spring Data JPA + query KPI in SQL (JdbcClient)
  domain/      entità JPA
  dto/         record di ingresso e uscita
  config/      configurazione OpenAPI
src/main/resources
  db/migration/   migrazioni Flyway V1..V4
  templates/      pagine Thymeleaf
  static/css/     foglio di stile
db/init/          script di creazione database e utenti
powerbi/          query M e misure DAX
```

## Limiti noti

Non c'è autenticazione: l'applicazione è pensata per l'uso in locale. Spring Security è il passo successivo
previsto.
