# JCA Specification

## Java Chat Application

**Version 0.1.0**
**Status:** Initial Design Specification
**Language:** Java 25+

---

## Introducation

JCA (Java Chat Application, name tbd\*) is an open-source, self-hostable communication platform focused on privacy, user ownership, and extensibility.

The goal of JCA is to create a communication system where users and communities have control over their own infrastructure instead of depending entirely on centralised platforms.

The first version of JCA focuses on creating a stable foundation:

- A reusable Java API
- A client-server communication architecture
- **(tbd)** Encrypted private messaging
- Local message storage
- **(tbd)** Offline message synchronisation
- A self-hostable server

Future versions may introduce direct peer-to-peer communication, decentralised infrastructure, and community-based systems.

---

## Version 0.1.0 - Goals

The first release of JCA is not intended to replace any existing platform immediatly. Its purpose is to establish the core technologies required for a privacy-focused communication system.

This version should provide:

- User identities
- Authentication
- Secure client-server communication
- Local storage of conversations
- **(tbd)** Offline message delivery
- Synchronisation between clients and servers
- A reusable client API
- A self-hostable server application

---

### Architecture Overview

JCA will be designed as a modular Maven project.

The architecture should separate:

- Shared API definitions
- Core functionality
- Client logic
- Server logic
- User interfaces

---

# Development Milestones

## 0.1.0-alpha

Foundation:

- Maven project structure
- Server startup
- Client connection
- **(tbd)**

## 0.1.0-beta

**(tbd)**

## 0.1.0-release

**(tbd)**

--- 

# Design Philosophy

JCA should prioritise:

- private over convenience
- user ownership over platform lock-in
- open architecture over closed ecosystems
- extensibility over short-term feature count

The goal of version 0.1.0 is not to build the final messenger.

The goal is, to create a strong foundation on which a decentralised, private communication platform can be built.
