# mortalityRISK: A Spatially Explicit Tool for Evaluating Wildlife Population Responses to Infrastructure-Induced Mortality

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**mortalityRISK** is an individual-based, multi-species, stochastic simulation framework built for Population Viability Analysis (PVA). It enables users to evaluate the long-term demographic and spatial consequences of infrastructure-induced wildlife mortality (e.g., roads, railways, power lines, and wind farms). 

The tool includes an accessible, user-friendly Graphical User Interface (GUI) and is implemented in Java utilizing multithreaded programming to guarantee robust computational efficiency.

---

## Key Features

* **Dual Modeling Environments:** Supports both **Spatially Explicit Mode** (incorporating grid-based metapopulation dynamics, and explicit animal dispersal) and **Spatially Implicit Mode** (assessing isolated populations for general demographic threshold analyses).
* **Empirical Data Integration:** Directly incorporates real-world mortality survey data and localized infrastructure density to compute safe biological thresholds.
* **Multi-Species Pipelines:** Simulates multiple species and alternative management or infrastructure development scenarios concurrently.
* **Reproducible Workflows:** Execute simulations visually through the point-and-click GUI or run command-line scripts automated by the tool's built-in command string exporter.

---

## Workflow Overview

<img src="doc/ModelScheme.png" alt="mortalityRISK Workflow Diagram" width="500">

_Figure 1: Conceptual workflow of mortalityRISK, detailing required dataset inputs, internal interface processes, execution modes, and visual outputs._

---

## Getting Started

### Prerequisites
* **Java Runtime Environment (JRE):** Ensure Java 25 or later is available on your machine.

### Installation
 No installation required, navigate to your local folder and run the standalone application executable by either double clicking the .jar file or running:
```
   java -jar mortalityRISK.jar
```

## Input Data Requirements

The simulation utilizes two primary input components:

### 1. Demographic Spreadsheet (`.xlsx`)
A setup sheet declaring explicit demographic parameters. Key parameters include:
* **Infrastructure-Induced Mortality** (the actual observed mortality related to infrastructures)
* **Life Phases** & **Survival Rate** (supports variations across distinct life stages and sexes).
* **Longevity** & **Age at First Birth**.
* **Reproductive Metrics** 
* **Max Dispersal Length** & **Mate Finding Radius** (essential for spatial models).

### 2. Georeferenced Spatial Layers (Spatially Explicit Mode Only)
* **Species Distribution Layer:** A .asc raster setting the target grid dimensions and identifying currently occupied or potential colonization areas.
* **Infrastructure Density Layer:** A .asc rasters identifying the infrastructure density in each cell (e.g., kilometers of roads, total wind turbine).

---

## Exported Outputs

The program saves analytical tables and graphical visualizations out-of-the-box:
* **Population Time Series:** Clear CSV logs and data plots detailing total animal counts over time alongside 95% confidence intervals across stochastic iterations.
* **Overall Extinction Risk:** A comparative visual layout mapping species persistence levels across diverse infrastructure scenarios.
* **Persistence Map (`.asc` raster):** Cell-by-cell spatial grids mapping the probability that local subpopulations persist across simulation repetitions.
* **Mortality Threshold Graphs (Implicit Mode):** Sensitivity diagnostics pointing out the exact mortality boundaries that provoke abrupt population collapses.

---

## Developers & Authors

* **Tomé Neves** (Code Development & Conceptualization) — CIBIO, Centro de Investigação em Biodiversidade e Recursos Genéticos, Universidade do Porto / Universidade de Lisboa.
* **Clara Grilo** (Conceptualization) — CIBIO, Centro de Investigação em Biodiversidade e Recursos Genéticos, Universidade do Porto / Universidade de Lisboa.

### Project Acknowledgements
`mortalityRISK` was conceptualized within the framework of project **RISKY** (Wildlife Mortality from Energy and Transport Infrastructure - 03-55-RISKY). It was supported through funding from **OSCARS** via the European Commission’s Horizon Europe Research and Innovation Programme (grant agreement No. 101129751).

---

## License

This project is licensed under the **MIT License**. You are free to modify, distribute, and implement this framework in downstream applications provided proper author attribution remains intact.

### Third-Party Java Libraries Bundled
* **Apache Commons, Log4j2, POI, XMLBeans** (Licensed under Apache License 2.0)
* **MigLayout** (Licensed under BSD 3-Clause License)
* **JFreeChart & JCommon** (Licensed under LGPL v2.1)

---

## Citation

When using `mortalityRISK` or citing the theoretical modeling framework, please reference our main article:

> Neves, T., & Grilo, C. (2026). mortalityRISK: a spatially explicit tool for evaluating wildlife population responses to infrastructure-induced mortality. *Journal TBD*. (In Press / Citation Details Pending).
