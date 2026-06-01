# Parameter Reference Guide

This document provides a comprehensive breakdown of all data inputs, life-history attributes, and execution flags utilized across `mortalityRISK`.

---

## 1. Simulation Setup & Configuration Parameters
These variables are supplied via the demographic spreadsheet (`.xlsx`) or through CLI.

| Parameter | Application Mode | Value Type | Description |
| :--- | :---: | :---: | :--- |
| **Sex Ratio** | Both | Probability | Probability that a newborn individual is male. |
| **Survival Rate** | Both | Probability / iteration | Probability of surviving one discrete iteration, excluding infrastructure mortality. Can vary by sex and life phase. When varying by life phase, a value for should be provided for each phase, separated by `;`. |
| **Longevity** | Both | int (Iterations) | Maximum absolute lifespan of an individual. |
| **Life Phase Change** | Both | int (Iterations) | The exact age boundaries defining when an individual transitions to their next developmental life phase. Should be separated by `;`. E.g. `4;24;42` |
| **Age at First Birth** | Both | int (Iterations) | Minimum age required for female individuals to give birth. |
| **Min Interval Between Births** | Both | int (Iterations) | Minimum required iterations to elapse between reproductive events. |
| **Average Interval Between Births** | Both | int (Iterations) | Average iterations between births. |
| **Minimum Offspring Number** | Both | int (Individuals) | Minimum number of offspring produced per reproductive event. |
| **Maximum Offspring Number** | Both | int (Individuals) | Maximum number of offspring produced per reproductive event. |
| **Average Offspring Number** | Both | double (Individuals) | Alternative configuration setting: target average offspring number produced per litter. |
| **Starting Population** | Spatially Implicit | int (Individuals) | Total initial closed population size used to start spatially implicit runs. |
| **Population Density** | Spatially Explicit | double (Ind. / km²) | Density metric used to calculate subpopulation size inside each discrete grid cell area. |
| **Maximum Population** | Both | int or double | Density-dependent carrying capacity limit (Total population in implicit mode, or cap per km² in explicit mode). |
| **Max Dispersal Length** | Spatially Explicit | double (Meters / iteration) | Maximum radial distance an individual can migrate during the dispersal step. |
| **Mate Finding Radius** | Spatially Explicit | double (Meters) | Active radius within which a receptive female searches for available males. |
| **Infrastructure-Induced Mortality** | Both | Ind. / unit / iteration | Empirical infrastructure-induced mortality expressing individuals killed per infrastructure unit (e.g., per km of road) per iteration. |

---

## 2. Command Line Execution (CLI) Arguments
These ordered parameters are fed directly into the terminal execution string when launching the tool without the GUI wrapper.

### Spatially Explicit Sequence (12 Arguments)
| Order | Flag / Variable | Type | Expected Format / Range |
| :---: | :--- | :---: | :--- |
| **1** | `InputFile` | String | Valid system path to configuration Excel sheet (`.xlsx`). |
| **2** | `Species Folder` | String | Directory path containing spatial species presence rasters. |
| **3** | `Infrastructure File` | String | System path pointing directly to infrastructure layer (`.asc`). |
| **4** | `Number of iterations` | int | Total duration of the simulation (e.g., `600`). |
| **5** | `Number of repetitions` | int | Total stochastic loops to execute (e.g., `100`). |
| **6** | `Number of extra scenarios` | int | Number of infrastructure mortality scenarios. Values greater than 0 provide progressively finer mortality variations. |
| **7** | `Minimum Persistence Threshold` | double | Threshold for considering a cell locally extinct. (Range: `0.0` - `1.0`). |
| **8** | `Output Folder` | String | Destination path where files are stored. |
| **9** | `Number of Cores` | int | Available CPUs (e.g., `4`, `8`). |
| **10**| `Initial date` | String | Date to start the simulation on. Formatted explicitly as `yyyy-mm-dd`. |
| **11**| `Time unit` | String | Strict categorical text input: `Day`, `Month`, or `Year`. |
| **12**| `Maximum Processed Individuals` | int | Maximum number of individuals explicitly simulated per spatial cell. Set to 0 to allow for an unlimited number of individuals. |

### Spatially Implicit Sensitivity Sweep Sequence (10 or 15 Arguments)
| Order | Flag / Variable | Type | Expected Format / Range |
| :---: | :--- | :---: | :--- |
| **1** | `InputFile` | String | Valid system path to configuration Excel sheet (`.xlsx`). |
| **2** | `Infrastructure density` | double | Infrastructure density value. |
| **3** | `Number of iterations` | int | Total duration of the simulation (e.g., `600`). |
| **4** | `Number of repetitions` | int | Total stochastic loops to execute (e.g., `100`). |
| **5** | `Number of extra scenarios` | int | Number of infrastructure mortality scenarios. Values greater than 0 provide progressively finer mortality variations. |
| **6** | `Output Folder` | String | Destination path where files are stored. |
| **7** | `Number of Cores` | int | Available CPUs (e.g., `4`, `8`). |
| **8** | `Initial date` | String | Fixed calendar format structured layout: `yyyy-mm-dd`. |
| **9** | `Time unit` | String | Temporal step tracking unit choice: `Day`, `Month`, or `Year`. |
| **10**| `Maximum Processed Individuals` | int | Maximum number of individuals explicitly simulated per spatial cell. Set to 0 to allow for an unlimited number of individuals. |
| **11**| `Sweep Initial Value` | double | _(optional)_ Starting numeric mortality value for the sensitivity calculation. |
| **12**| `Sweep Final Value` | double | _(optional)_ Ending numeric mortality value for the sensitivity calculation. |
| **13**| `Sweep Infrastructure mortality?` | boolean | _(optional)_ Set `true` to analyse infrastructure-induced mortlaity; `false` evaluates survival baseline variations. |
| **14**| `Sweep Resolution` | int | _(optional)_ Number of steps between the initial and final value. |
| **15**| `Scale sweep mortality to yearly` | boolean | _(optional)_ Set `true` to normalize resulting mortality values into annual. |
