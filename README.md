# Brewkery 

A native Android coffee & bakery ordering application built with Kotlin as part of the Clickretina Android Developer assignment.

## Features

* Coffee & bakery menu
* REST API integration
* Product details
* Product customization
* Cart management
* Quantity management
* Dynamic price calculation
* 8% tax
* $2.50 delivery fee
* Order confirmation
* Order status
* Loading and error states

## Tech Stack

* Kotlin
* Android
* MVVM
* Retrofit
* Kotlin Coroutines
* ViewModel
* Repository Pattern
* Coil
* JUnit

## Architecture

```text
UI
 ↓
ViewModel
 ↓
Repository
 ↓
Retrofit
 ↓
REST API
```

## API

Menu API:

```text
https://raw.githubusercontent.com/VivekShah138/Brewkery/main/data.json
```

Individual item API:

```text
https://raw.githubusercontent.com/VivekShah138/Brewkery/main/api/items/{id}.json
```

## AI Usage

AI tools were used during development for architecture guidance, API integration, debugging, and code improvement.

### AI Tools Used

* ChatGPT
* Android Studio AI

### Prompts Used

**Prompt 1 — Project Architecture**

```text
Build a native Android app called Brewkery using Kotlin, MVVM, Retrofit, Coroutines and Repository pattern. Follow the provided Brewkery prototype and implement the required menu, product details, cart and order status screens.
```

**Prompt 2 — API Integration**

```text
Help me implement Retrofit API integration for the Brewkery REST API using Kotlin Coroutines, Repository and ViewModel. Do not hardcode the API data.
```

### Bug Caught & Fixed
Bug:
The initial implementation allowed users to increase the quantity of an item without any limit. This meant users could continuously increase the quantity to an unrealistic or unlimited value.
Cause:
There was no maximum quantity validation in the quantity increment logic.
Fix:
I added a maximum quantity condition of 30 items per product. The quantity can now be increased only until it reaches 30.
Result:
The quantity is now restricted to a maximum of 30 items per product, preventing unlimited quantity selection and making the cart behavior more realistic.
3. Sync Gradle.
4. Run the application on an Android device or emulator.

## Developer

**Akshit**
