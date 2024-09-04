# Product order management
## Popis projektu
Zadání projektu bylo definováno v rámci kurzu Skillmea – Junior Java programátor/ka

### Text zadání
Aplikační server umožňuje spravovat produkty a objednávky. Webové rozhraní(API), stejně jako objekty použité pro komunikaci, jsou definované v zadané specifikaci.
Specifikace API je dostupná na adrese: [app.swaggerhub](https://app.swaggerhub.com/apis-docs/JAHICJAKUB/Street_of_Code_Spring_Boot_zadanie/1.0.0)
Systém umožňuje vytvářet a odebírat produkty. Dále pak úpravu existující produktů, jako například navýšení počtu kusů na skladě.
Systém umožňuje vytvářet objednávky. Do nich je možné přidávat produkty, které jsou na skladě. V případě, že je nedostatečné množství produktu skladem, odpovědí je pak chybový kód 400.
Systém umožňuje zaplacení objednávky. Po zaplacení se její stav změní na `paid`. Do již zaplacené objednávky dále není možné přidávat produkty.

Systém obsahuje automatizované integrační testy, které jsou umístěny ve složce `src/test/java`. Testy kontrolují funkčnost API.
Pokud všechny testy projdou, tak je aplikace správně naimplementovaná.

## Technologie
- Jazyk: Java
- Framework: Spring Boot
- Databáze: H2
- IDE: [IntelliJ IDEA]()

## Struktura projektu
Aplikaci jsem se snažil navrhnout tak, aby měla jasnou strukturu. Pod hlavním balíčkem `sk.streetofcode.productordermanagement` naleznete následující rozčlenění:
- `api`: obsahuje balíčky jako: `exception`, `request`, `response`, dále pak rozhraní jako například: `ProducService.java `
- `controller`: obsahuje třídy, které zpracovávají HTTP požadavky a vrací odpovědi. Například `OrderController.java`
- `domain`: obsahuje doménové třídy `Item`, `Order`, `Product`, `ShoppingList`
- `implementation`: rozděluje řešení projektu na dvě větve - `jdbc`, `jpa`
  Výběr implementace se provádí v souboru `application.properties`: proměnná - `spring.profiles.default` může nabývat hodnot: `jpa` nebo `jdbc` v závislosti na tom, které řešení chceme použít
## Jak aplikaci spustit
Otevři projekt v IntelliJ IDEA, jdi na adresu `src/main/java/sk.streetofcode.productordermanagement/ProductOrderManagementApplication.java` a vedle `main` klikni na zelenou šipku.

Alternativou je pak spuštění přes terminal pomocí příkazu:
`./mvnw spring-boot:run`

API beží na portu 8080. Bez frontentdu je možné vyzkoušet funkčnost v prohlížeči pomocí Swaggeru UI, který je na adrese:
http://localhost:8080/swagger-ui/index.html#


