# 🏦 Tax-Calculator

A modern **Spring Boot** application designed to simplify cryptocurrency tax reporting. It processes Koinly JSON exports and generates a comprehensive, mathematically consistent, and accountant-ready PDF report (especially optimized for the Italian tax framework - Agenzia delle Entrate).

## ✨ Features

- **JSON to PDF Conversion**: Transforms raw Koinly JSON exports into an elegant, easy-to-read PDF document.
- **Accurate Net/Gross Accounting**: Reconstructs missing or unallocated gross profits and losses, intelligently handling intra-transaction netting (reported cleanly as *"Plus/minusvalenze compensate"*).
- **Per-Asset Breakdown**: Groups capital gains, losses, net values, and ending balances by individual cryptocurrency.
- **Modern Web Interface**: Upload your JSON file via an intuitive UI, process it, and use the interactive toggle to either preview the PDF in the browser or download it directly.
- **Automated Data Extraction**: Includes a robust JavaScript scraper (`getting-started.html`). When executed in the console of the Koinly web app, it automatically paginates and extracts your full transaction history, investments, wallets, and holdings directly into a JSON file, bypassing standard CSV export limitations.

## 🛠️ Technology Stack

- **Backend**: Java 22, Spring Boot 3.4.0
- **PDF Generation**: iTextPDF 5.5.13.4, Apache PDFBox 3.0.3
- **Frontend**: HTML5, CSS3, Vanilla JavaScript 
- **Utilities**: `io.github.giovannilamarmora.utils:utils-code`

## 🚀 Getting Started

### Prerequisites
- JDK 22 or higher
- Maven 3.8+

### Installation & Execution
1. Clone the repository:
   ```bash
   git clone https://github.com/giovannilamarmora/tax-calculator.git
   cd tax-calculator
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   *Alternatively, run the generated JAR:*
   ```bash
   java -jar target/tax-calculator.jar
   ```

## 💻 Usage

1. Open your browser and navigate to the application URL (default is usually `http://localhost:8085`).
2. **Get your Data**:
   - If you don't have the required JSON file, navigate to the **Documentation** section in the UI. 
   - Copy the provided JavaScript snippet.
   - Log into Koinly, open your browser's Developer Console (F12), paste the script, and press Enter. The script will automatically fetch your full tax data and download a `koinly_data_YYYY.json` file.
3. **Generate the Report**:
   - Upload the downloaded JSON file into the Tax-Calculator web interface.
   - Choose whether to **Download directly** or **View in browser**.
   - Click **Generate PDF Report**.
4. Hand the mathematically sound PDF to your accountant!

## 📊 How It Works (The Math)

Koinly's API often truncates detailed investment arrays for large or highly complex transactions, reporting only the net global gain. This creates a visual discrepancy where the sum of individual asset profits does not match the final tax total. 

Tax-Calculator solves this using a sophisticated **2-pass parsing algorithm**:
1. It calculates all granularly available `investments`.
2. It detects discrepancies between the transaction's `root_gain` and the sum of its detailed investments.
3. It automatically re-allocates these missing gains/losses to the respective traded asset.
4. Any remaining mathematical artifacts caused by intra-transaction gross compensations are cleanly isolated in a **"Plus/minusvalenze compensate"** row.

This ensures that **the columns sum up perfectly to the global tax totals**, rendering the document 100% transparent and reliable for tax authorities.

## 📄 License

This project is licensed under the Apache License 2.0. See the `LICENSE` file for details.