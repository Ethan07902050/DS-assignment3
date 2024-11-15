# 2024 DS Assignment 3: Paxos Voting Algorithm

## Table of Contents

1. [Execution Guide](#execution-guide)
    - [Step 1: Compile the Project](#step-1-compile-the-project)
    - [Step 2: Running PaxosSimulation](#step-2-running-paxossimulation)
2. [Configuration Files](#configuration-files)
    - [Fields Explanation](#fields-explanation)
    - [Example Configuration](#example-configuration)
    - [Test Scenarios](#test-scenarios)
3. [System Overview](#system-overview)
4. [Classes and Their Responsibilities](#classes-and-their-responsibilities)
    - [PaxosSimulation](#1-paxossimulation)
    - [Member](#2-member)
    - [Proposer](#3-proposer)
    - [Acceptor](#4-acceptor)
    - [Learner](#5-learner)
    - [ElectionServer](#6-electionserver)
    - [Message](#7-message)

## Execution Guide

### Step 1: Compile the Project

Before running any of the servers or clients, you need to compile the project using Maven.

```bash
mvn clean compile
```

### Step 2: Running PaxosSimulation
Run the `PaxosSimulation` program with a specified configuration file (e.g., `config_only_M1_proposes.json`). Use the following command:

```bash
mvn exec:java -Dexec.mainClass="PaxosSimulation" -Dexec.args="config_only_M1_proposes.json"
```

## Configuration Files
The configuration files are used to define the behavior of each member in the Paxos protocol simulation. They are in JSON format and contain a single key, `members`, which is an array of objects. Each object in the array represents a member of the council (`M1` to `M9`) and specifies its behavior.

### Fields Explanation
- `id`:  The unique identifier of the council member
- `proposedValue`: The value that the member proposes as a candidate for council president.
  - If `null`, the member does not propose a value.
  - If set to a string (e.g., `M1`), the member proposes that value.
- `responseType`:  Determines how the member responds to voting queries in the simulation.
  - `immediate`: The member responds to messages without delay.
  - `small_delay`: The member responds after 100 ms.
  - `large_delay`: The member responds after 2000 ms.
  - `no_response`: The member does not respond to messages (simulates going offline).

### Example Configuration
```
{
  "members": [
    {"id": "M1", "proposedValue": "M1", "responseType": "immediate"},
    {"id": "M2", "proposedValue": "M2", "responseType": "large_delay"},
    {"id": "M3", "proposedValue": null, "responseType": "no_response"},
    {"id": "M4", "proposedValue": null, "responseType": "immediate"},
    {"id": "M5", "proposedValue": null, "responseType": "small_delay"},
    {"id": "M6", "proposedValue": null, "responseType": "large_delay"},
    {"id": "M7", "proposedValue": null, "responseType": "immediate"},
    {"id": "M8", "proposedValue": null, "responseType": "no_response"},
    {"id": "M9", "proposedValue": null, "responseType": "small_delay"}
  ]
}
```

### Test Scenarios
Each configuration is stored in `src/main/resources`. The expected result is that all responding members agree on a single value.

1. `config_only_M1_preposes.json`
    - `M1`: Always proposes and responds immediately.
    - `M2` to `M9`: Respond immediately without proposing.
2. `config_M1_and_M2_propose.json`
    - `M1` to `M2`: Propose and respond immediately.
    - `M3` to `M9`: Respond immediately without proposing.
3. `config_M2_large_delay.json`
    - `M1`: Proposes and responds immediately.
    - `M2`: Proposes with a large delay to simulate poor internet.
    - `M3`: Proposes and responds immediately.
    - `M4` to `M9`: Respond immediately without proposing.
4. `config_M3_no_response.json`
    - `M1`: Proposes and responds immediately.
    - `M2`: Proposes and responds with a small delay to simulate Sheoak Café connectivity.
    - `M3`: Proposes but goes offline (does not respond at all).
    - `M4` to `M9`: Respond immediately without proposing.
5. `config_mixed_responses_1.json`
    - `M1`: Proposes and responds immediately.
    - `M2`: Proposes with a large delay to simulate poor internet.
    - `M3`: Proposes with a small delay to simulate mixed connectivity.
    - `M4` to `M6`: Respond immediately without proposing.
    - `M7` to `M9`: Go offline (do not respond).
6. `config_mixed_responses_2.json`
    - `M1`: Proposes and responds immediately.
    - `M2`: Proposes with a large delay to simulate poor internet.
    - `M3`: Goes offline (does not respond at all).
    - `M4`: Responds immediately.
    - `M5`: Responds with a small delay.
    - `M6`: Responds with a large delay.
    - `M7`: Responds immediately.
    - `M8`: Goes offline (does not respond).
    - `M9`: Responds with a small delay.

## System Overview

The system simulates a voting protocol using the Paxos consensus algorithm. It models the election of a council president among a group of members (`M1` to `M9`) with varying response behaviors (e.g., immediate response, delay, or going offline). The system is composed of several interacting components:

1. **Paxos Roles**:
    - **Proposer**: Proposes a value to be agreed upon.
    - **Acceptor**: Participates in the voting process, responding to proposals.
    - **Learner**: Learns the final consensus result once it is decided.

2. **Communication**:
    - Uses sockets to enable communication between members.

3. **Simulation**:
    - Allows the configuration of various scenarios to test the Paxos algorithm under different network and response conditions.

## Classes and Their Responsibilities

### 1. `PaxosSimulation`
- **Purpose**: Acts as the entry point for the system and sets up the simulation environment.
- **Responsibilities**:
    - Loads configuration files defining member behaviors and proposed values.
    - Initializes `Member` objects for each participant and starts their processes.
    - Manages the overall simulation of the election process.

### 2. `Member`
- **Purpose**: Represents a participant in the Paxos algorithm.
- **Responsibilities**:
    - Combines the roles of Proposer, Acceptor, and Learner, depending on its configuration.
    - Communicates with other members via sockets.
    - Handles incoming messages and invokes appropriate methods in its Proposer, Acceptor, or Learner components.

### 3. `Proposer`
- **Purpose**: Implements the proposer role in the Paxos algorithm.
- **Responsibilities**:
    - Initiates the voting process by sending `prepare` messages to acceptors.
    - Handles responses to `prepare` messages (promises) and sends `accept` messages for the value it proposes.
    - Manages proposal numbers to ensure uniqueness.

### 4. `Acceptor`
- **Purpose**: Implements the acceptor role in the Paxos algorithm.
- **Responsibilities**:
    - Responds to `prepare` messages with a promise, ensuring no lower-numbered proposal is accepted.
    - Processes `accept` requests and records the accepted proposal if valid.

### 5. `Learner`
- **Purpose**: Implements the learner role in the Paxos algorithm.
- **Responsibilities**:
    - Listens for consensus results (accepted proposals) from acceptors.
    - Aggregates responses to determine the final agreed value.
    - Informs other members of the final decision once a consensus is reached.

### 6. `ElectionServer`
- **Purpose**: Manages communication between members in the simulation.
- **Responsibilities**:
    - Acts as a central server for socket-based communication.
    - Routes messages between members and ensures message delivery.
    - Handles connection establishment for all members.

### 7. `Message`
- **Purpose**: Represents the messages exchanged during the Paxos protocol.
- **Responsibilities**:
    - Encapsulates message types (`prepare`, `promise`, `accept`, `accepted`) and associated data.
    - Provides serialization and deserialization methods for socket communication.


