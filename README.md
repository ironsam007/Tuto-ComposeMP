# Initial project setup:
Initial project could be generated from Kotlin Multiplatform Wizard and choosing shared UI with CMP UI framework

## Project Arch:
### Project files:
- ComposeApp: code that will be shared across CMP applications, contains:
    - androidMain: Kotlin code that will be compiled per android
    - desktopMain: Kotlin code that will be compiled per desktop
    - iosMain    : Kotlin code that will be compiled per ios
    - commonMain: Code that is common for all targets, Most of the code should be Here in such project
      NB: In each one of this you have full access to ptfm specific libraries, OS dep... example: android.context

- IosApp: contains iOS applications. needed as an entry point for ios app. SwiftUI code could be added here for more developpement


## Adopted Arch:
This app is single feature[self contained], with a clean arch data-domain-presentation
Dev: start with what the actual problem the app is solving[feature: fetching book list]: domain layer, then presentation then data sources

- Clean Arch :
    - This app is a single feature (book - //here: looks very much self contain)
    - each feature contains 3 clean arch layers: data, domain, presentation


---------------------------------

## Best Practices
### Best practices -  Commit message guidelines:
"commitOperation(module/layer): msg - file"

### Best Practices - MVI implementation
1 - Define the state: Model that represent UI at any given time:
2 - Define the intents: What user could do - sealed class or interface:
3 - Process data in viewModel: VM process the intent and update the state:
4 - Use the viewModel in the UI screen:



-------------------------------
Dependency Inversion: High-level modules (like ViewModel) don’t depend on low-level modules (like network).
Both depend on abstractions (interfaces in domain).

=> On DefaultBookRepository, Decoupling data from domain layer: results obtained in /data model and returned in /domain model //USING mappers 
now this will extend domain repo interface, and domain repo would be used app wide without arch violation

=> But how /domain BookRepository could be used in ViewModel after this changes, i mean its just an interface, and viewModel will need to call functionalities:

In fact DI container(koin) builds the viewModel and hands it the correct implementation:
This does not violate clean arch, because Koin Module lives in composition/root layer like app/sharedModule:
=> That layer is allowed to know all about the other layers, because its the vert outer shell that wires the app together:
=> This is normal in Clean Arch: the top level "composition root" is allowed to "break purity" because its the integration point.
-> the object graphs are composed as close as possible to app entry point
