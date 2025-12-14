# Architektura a Vylepšení Aplikace

Tento dokument popisuje vícevrstvou architekturu aplikace a všechna vylepšení implementovaná pro kritizované oblasti.

## 📐 Vícevrstvá Architektura

Aplikace je navržena podle principů vícevrstvé architektury (Multi-Layer Architecture):

### 1. **Prezentační vrstva (Presentation Layer)**
- **Controller vrstva**: `com.finalworks.controller.*`
  - `FinalWorkController` - REST API pro správu finálních prací
  - `StudentController` - REST API pro správu studentů
  - `CommentController` - REST API pro komentáře
  - **Zodpovědnost**: 
    - Příjem HTTP požadavků
    - Validace vstupů pomocí `@Valid` a Bean Validation
    - Mapování na DTO objekty
    - Vracení HTTP odpovědí

### 2. **Obchodní logika (Business/Service Layer)**
- **Service vrstva**: `com.finalworks.service.*`
  - `FinalWorkService` - obchodní logika pro finální práce
  - `EmailService` - služba pro odesílání emailů
  - `DataInitializationService` - inicializace dat
  - **Zodpovědnost**:
    - Implementace obchodní logiky
    - Transakční management (`@Transactional`)
    - Konverze mezi entitami a DTO
    - Validace obchodních pravidel

### 3. **Datová vrstva (Data/Persistence Layer)**
- **Repository vrstva**: `com.finalworks.repository.*`
  - `FinalWorkRepository`, `StudentRepository`, `CommentRepository`
  - **Zodpovědnost**:
    - Přístup k databázi
    - CRUD operace
    - Custom queries

### 4. **Model vrstva (Domain/Entity Layer)**
- **Entity třídy**: `com.finalworks.model.*`
  - `FinalWork`, `Student`, `Comment`
  - **Zodpovědnost**:
    - Reprezentace databázových entit
    - JPA anotace pro mapování
    - Optimistic locking (`@Version`)

### 5. **DTO vrstva (Data Transfer Objects)**
- **DTO třídy**: `com.finalworks.dto.*`
  - `FinalWorkDTO`, `StudentDTO`, `CommentDTO`, `StudentRequestDTO`
  - **Zodpovědnost**:
    - Přenos dat mezi vrstvami
    - Validace vstupů
    - Oddělení vnitřní struktury od API

## ✅ Implementovaná Vylepšení

### 1. Ošetření Vstupu (Input Validation)

#### Backend Validace:
- ✅ **Bean Validation** na všech DTO objektech:
  - `@NotBlank` - povinná pole
  - `@NotNull` - povinné objekty
  - `@Size` - délka řetězců (min/max)
  - `@Email` - validace emailu
  - `@PasswordValidator` - custom validace hesel

#### Validace Hesel:
```java
@PasswordValidator
@Size(min = 8, max = 128)
```
- Minimálně 8 znaků
- Alespoň 1 velké písmeno
- Maximálně 128 znaků

#### Sanitizace Vstupů:
- ✅ `InputSanitizer` utility třída
- Ošetření XSS útoků
- Odstranění nebezpečných HTML tagů
- Encoding speciálních znaků

#### Příklady Validace:
```java
// FinalWorkDTO
@NotBlank(message = "Title is required")
@Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")

// CommentDTO
@Size(min = 1, max = 2000, message = "Comment must be between 1 and 2000 characters")

// StudentRequestDTO
@Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
```

### 2. Správa Chyb (Error Handling)

#### Global Exception Handler:
- ✅ `GlobalExceptionHandler` - centralizované zpracování chyb
- ✅ Strukturované error responses
- ✅ Logování všech chyb
- ✅ Vhodné HTTP status kódy

#### Typy Chyb:
```java
- ResourceNotFoundException → 404 NOT FOUND
- BadRequestException → 400 BAD REQUEST
- ConflictException → 409 CONFLICT
- ValidationException → 400 BAD REQUEST (s field errors)
- OptimisticLockingFailureException → 409 CONFLICT
- Generic Exception → 500 INTERNAL SERVER ERROR
```

#### Error Response Struktura:
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": 1234567890,
  "fieldErrors": {
    "email": "Email must be valid",
    "password": "Password must be at least 8 characters"
  }
}
```

### 3. Zabezpečení Vícenásobného Přístupu (Concurrent Access)

#### Optimistic Locking:
- ✅ `@Version` anotace na všech entitách:
  - `FinalWork.version`
  - `Comment.version`
  - `Student.version`

#### Transakční Management:
- ✅ `@Transactional` na všech write operacích
- ✅ Automatický rollback při chybách
- ✅ Izolace transakcí

#### Ošetření Concurrent Modifications:
```java
@ExceptionHandler({OptimisticLockingFailureException.class})
public ResponseEntity<ErrorResponse> handleOptimisticLockingException() {
    // Vrací 409 CONFLICT s informací o konfliktu
}
```

### 4. Ošetření Hesla Uživatele (Password Handling)

#### Bezpečné Ukládání:
- ✅ **BCrypt** hashování hesel
- ✅ Automatický salt generation
- ✅ Heslo se nikdy neukládá jako plain text
- ✅ Heslo se nikdy nevrací v API odpovědích

#### Validace:
- ✅ Minimálně 8 znaků
- ✅ Alespoň 1 velké písmeno
- ✅ Maximálně 128 znaků
- ✅ Custom `@PasswordValidator` anotace

#### Implementace:
```java
// Hashování před uložením
student.setPassword(passwordEncoder.encode(plainPassword));

// Heslo se nikdy nevrací v DTO
@JsonIgnore
private String password;
```

### 5. Logování (Logging)

#### Comprehensive Logging:
- ✅ **SLF4J + Logback** pro logování
- ✅ Logování na všech úrovních:
  - `DEBUG` - detailní informace pro debugging
  - `INFO` - důležité operace (create, update, delete)
  - `WARN` - varování (not found, validation failures)
  - `ERROR` - chyby s stack trace

#### Logování v Controller vrstvě:
```java
logger.info("Creating final work with title: {}", title);
logger.warn("Final work not found with id: {}", id);
logger.error("Error creating final work", e);
```

#### Logování v Service vrstvě:
```java
logger.debug("Fetching all final works");
logger.info("Successfully created final work with id: {}", id);
logger.error("Error fetching final work with id: {}", id, e);
```

#### Logování v Exception Handleru:
```java
logger.warn("Resource not found: {}", ex.getMessage());
logger.error("Unexpected error occurred", ex);
```

### 6. Korektní Předávání Hodnot Mezi Vrstvami

#### DTO Pattern:
- ✅ Oddělení entit od API
- ✅ Validace na DTO úrovni
- ✅ Konverze Entity ↔ DTO v service vrstvě

#### Předávání Dat:
```
Controller (DTO) → Service (Entity) → Repository (Entity) → Database
                ← Service (DTO) ← Repository (Entity) ← Database
```

#### Error Propagation:
- ✅ Chyby se propagují z Repository → Service → Controller
- ✅ Service vrstva přidává kontext k chybám
- ✅ Controller vrstva mapuje na HTTP status kódy

### 7. Použité Styly a Knihovny

#### Backend:
- **Spring Boot 3.2.0** - framework
- **Spring Data JPA** - datová vrstva
- **Spring Security** - bezpečnost
- **Spring Validation** - validace
- **Lombok** - redukce boilerplate kódu
- **SLF4J + Logback** - logování
- **BCrypt** - hashování hesel
- **PostgreSQL** - databáze

#### Frontend:
- **React 18.2.0** - UI framework
- **React Router** - routing
- **Axios** - HTTP klient
- **CSS Modules** - styling

### 8. Smysluplné Uživatelské Rozhraní

#### UX Features:
- ✅ Loading states
- ✅ Error messages
- ✅ Form validation
- ✅ Responsive design
- ✅ Intuitive navigation

## 🔒 Bezpečnostní Opatření

1. **Input Sanitization** - prevence XSS útoků
2. **Password Hashing** - BCrypt s automatickým salt
3. **SQL Injection Prevention** - JPA/Hibernate parametrizované dotazy
4. **CORS Configuration** - omezení přístupu
5. **HTTPS** - šifrovaná komunikace
6. **Optimistic Locking** - prevence race conditions

## 📊 Transakční Management

Všechny write operace jsou zabaleny v transakcích:
```java
@Transactional
public FinalWorkDTO createFinalWork(FinalWorkDTO dto) {
    // Automatický rollback při chybě
    // ACID vlastnosti zajištěny
}
```

## 🎯 Best Practices Implementované

1. ✅ Separation of Concerns
2. ✅ Single Responsibility Principle
3. ✅ DRY (Don't Repeat Yourself)
4. ✅ Fail Fast - validace na vstupu
5. ✅ Comprehensive Error Handling
6. ✅ Security by Design
7. ✅ Logging Best Practices
8. ✅ Transaction Management
9. ✅ Input Validation & Sanitization
10. ✅ DTO Pattern pro oddělení vrstev

## 📝 Závěr

Aplikace implementuje všechny požadované oblasti:
- ✅ Ošetření vstupu
- ✅ Správa chyb
- ✅ Vícevrstvá architektura
- ✅ Korektní předávání hodnot
- ✅ Zabezpečení vícenásobného přístupu
- ✅ Ošetření hesla
- ✅ Logování
- ✅ Bezpečnostní opatření
