// Query Power Query (M) da incollare nell'Editor avanzato di Power BI Desktop.

// ---------------------------------------------------------------------------
// 1) Canale database: una query per ogni vista. Esempio per la tabella dei fatti.
//    Richiede Oracle MySQL Connector/NET installato sulla macchina di Power BI.
// ---------------------------------------------------------------------------
let
    Origine = MySQL.Database("localhost:3310", "retail_analytics"),
    Vendite = Origine{[Schema = "retail_analytics", Item = "v_fact_sales"]}[Data]
in
    Vendite

// ---------------------------------------------------------------------------
// 2) Canale REST: KPI mensili già aggregati dall'applicazione.
//    RelativePath e Query tengono fisso l'indirizzo di base: è la forma che
//    Power BI Service accetta anche per l'aggiornamento pianificato.
// ---------------------------------------------------------------------------
let
    Risposta = Json.Document(
        Web.Contents(
            "http://localhost:8081",
            [
                RelativePath = "api/v1/kpi/monthly",
                Query = [from = "2025-01-01", to = "2026-10-31"]
            ]
        )
    ),
    Tabella = Table.FromRecords(Risposta),
    Tipi = Table.TransformColumnTypes(
        Tabella,
        {
            {"month", type text},
            {"revenue", Currency.Type},
            {"margin", Currency.Type},
            {"units", Int64.Type},
            {"target", Currency.Type}
        }
    )
in
    Tipi
