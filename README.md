# Damanesign Java SDK

SDK Java Maven pour consommer les APIs Damanesign.

## Prérequis

- Java 11+
- Maven 3.8+

## Installation locale

```bash
mvn clean install
```

## Installation Maven

Après publication sur Maven Central :

```xml
<dependency>
  <groupId>ma.damanesign</groupId>
  <artifactId>damanesign-java-sdk</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Création d'une transaction

### Étape 1 : upload du document

Avant de créer la transaction, le PDF doit être envoyé à Damanesign via `POST /files/upload`.
Le SDK expose cette étape avec `uploadFile(...)`.

```java
FileResponse file = client.uploadFile(Path.of("contract.pdf"));
String fileId = file.getId();
```

Vous utiliserez ensuite `fileId` dans `members[].fields[].file`.

### Étape 2 : création puis lancement

```java
import com.damanesign.sdk.DamanesignClient;
import com.damanesign.sdk.model.CreateTransactionRequest;
import com.damanesign.sdk.model.FieldRequest;
import com.damanesign.sdk.model.FileResponse;
import com.damanesign.sdk.model.MemberRequest;
import com.damanesign.sdk.model.TransactionResponse;

import java.nio.file.Path;

public class Example {
    public static void main(String[] args) {
        DamanesignClient client = DamanesignClient.builder()
                .baseUrl("https://api-recette.damanesign.ma")
                .apiKey("YOUR_API_KEY")
                .build();

        FileResponse file = client.uploadFile(Path.of("contract.pdf"));
        String fileId = file.getId();

        TransactionResponse transaction = client.createTransaction(CreateTransactionRequest.builder()
                .name("Contrat client")
                .type("simple")
                .deliveryMode("email")
                .authenticationMode("email")
                .ordered(false)
                .addMember(MemberRequest.builder()
                        .type(MemberRequest.SIGNER)
                        .firstname("Sara")
                        .lastname("El Amrani")
                        .email("sara@example.com")
                        .phone("+212600000000")
                        .addField(FieldRequest.builder()
                                .file(fileId)
                                .type(FieldRequest.SIGNATURE)
                                .page(1)
                                .position("141,268,151,101")
                                .build())
                        .build())
                .build());

        client.startTransaction(transaction.getId());

        System.out.println(transaction.getId());
    }
}
```

Le SDK utilise l'authentification HTTP `x-api-key: <apiKey>`, conformément au Swagger Damanesign.

Le flux standard suit le Developer Portal :

1. `POST /files/upload` avec le champ multipart `file`.
2. `POST /transactions` avec `name`, `type`, `authenticationMode`, `members` et `members[].fields[].file`.
3. `POST /transactions/{id}/start` pour lancer la transaction.

## Méthodes exposées

Le client couvre les opérations publiques du Swagger `2.5.3` :

```java
client.listTransactions(filter);
client.getTransaction(transactionId);
client.createTransaction(request);
client.updateTransaction(transactionId, request);
client.deleteTransaction(transactionId);
client.updateMember(transactionId, memberId, memberRequest);
client.updateMemberAuthentication(transactionId, memberId, "sms");
client.startTransaction(transactionId);
client.sendReminder(transactionId);
client.prolongTransaction(transactionId, expiresAt);
client.cancelTransaction(transactionId);
client.getSignatureUrl(transactionId, memberId);

client.uploadFile(path);
client.getFile(fileId);
client.downloadFile(fileId);

client.sealDocument(sealRequest);
client.listSeals(sealFilter);
```

Les filtres de liste utilisent `TransactionFilter` et `SealFilter` :

```java
var transactions = client.listTransactions(TransactionFilter.create()
        .status(List.of("draft", "active"))
        .type(List.of("simple"))
        .limit(20));
```

## Champs additionnels

Les modèles acceptent des propriétés additionnelles grâce à `additionalProperty(...)`.
Cela permet de rester compatible avec une évolution du Swagger sans bloquer l'intégration.

```java
CreateTransactionRequest request = CreateTransactionRequest.builder()
        .name("Contrat client")
        .additionalProperty("customField", "value")
        .build();
```
