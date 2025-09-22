# EDF Scout

## Backend
A Spring Boot REST API that processes EDF files located in a directory specified by the EDF_DIR environment variable.
If the environment variable is not set, it defaults to /data/edf. Its purpose is to demonstrate a clean, minimal backend
that extracts metadata from EDF files and makes it available via a simple API.

### Features
- Scans the directory specified by the DATA_DIRECTORY environment variable for .edf files (default: /data/edf)
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
  - Java 17+ 
  - Spring Boot 3+ 
  - Maven 4+ 
  - EDF library for parsing EDF files (or any other EDF parser)
  - JUnit 5 for testing

### Running the application
1. **Clone the repository:**
   ```bash
   git clone https://github.com/username/edf-scout.git
   cd edf-scout

2. **Set the EDF files directory (environment variable):**
   ```bash
   export DATA_DIRECTORY=/path/to/edf/files or setx DATA_DIRECTORY "C:\path\to\edf\files"

3. **Build and run the Spring Boot application:**
   ```bash
   mvn clean install
   mvn spring-boot:run

4. **Access the API:**
   The API will be available at http://localhost:8080/api/edf

### API response
```aiignore
[
  {
    "fileName": "sleep_study_01.edf",
    "valid": true,
    "identifier": "Patient001",
    "recordingDate": "2025-09-21T22:15:00",
    "patientName": "John Doe",
    "numChannels": 8,
    "channelNames": ["EEG Fpz-Cz", "EEG Pz-Oz", "EOG left", "EOG right", "EMG chin", "ECG", "Resp", "Oximetry"],
    "recordingLength": 3600,
    "numAnnotations": 12
  },
  {
    "fileName": "corrupted_file.edf",
    "valid": false
    "errorMessage": "Example error message"
  }
]
```

### Test
```bash
  mvn test
```

## Frontend
### Tech stack & dependencies
  - Vite
  - Vue 3
  - Tailwind CSS
### Running the application
1. **Install fependencies:**
   ```bash
   cd frontend
   npm install

2. **Run development server:**
   ```bash
   npm run dev

