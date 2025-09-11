# EDF Scout

## Backend
A Spring Boot REST API that processes EDF files located in a fixed directory (/data/edf).
Its purpose is to demonstrate a clean, minimal backend that extracts metadata from EDF files and makes it available via a simple API.

### Features
- Scans the /data/edf directory for .edf files
- If a file is invalid or corrupted, it is still included in the response with valid=false
- If a file is valid, the following information is extracted (when available):
  - File name and extension 
  - Identifier 
  - Recording date (from the file header, not filesystem)
  - Patient name
  - Number of channels 
  - Channel names 
  - Recording length (seconds)
  - Number of annotations

### Tech stack & dependencies
Coming soon 

### Running the application
Coming soon 

### API response
Coming soon 

### Test