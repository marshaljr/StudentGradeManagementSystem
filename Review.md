# 📋 Self-Marking Evaluation Report
## Student Grade Management System
**Reviewed by:** Antigravity (OO Design & Development / GUI Reviewer)
**Date:** 26 March 2026
**Project:** Java 17 + JavaFX 21 + MySQL 8 Desktop Application
**Course:** OO Design & Development

---

## 1. SELF-MARKING SHEET

| Section | Score | Out Of |
|---------|-------|--------|
| OO Design & Development | **24** | 30 |
| GUIs & Forms | **20** | 30 |
| Final Report | **0** | 30 |
| Presentation / Video | **0** | 10 |
| **TOTAL** | **44** | **100** |

**Band: BARE PASS** ⚠️
> The code and GUI are solid (44 of the 60 available marks for sections A & B), but **zero marks are awarded for Section C (Final Report) and Section D (Presentation/Video)** because no `.docx`, `.pdf`, `.pptx`, or `.mp4` file was found anywhere in the project workspace. Per the marking rule: *"If something is not written, shown, and explained, it does NOT get marks — even if it works perfectly."*

---

## 2. SECTION A — OO Design & Development (24/30)

### 2.1 Checklist

| Requirement | Verdict | Evidence | Fix to Gain Marks |
|---|---|---|---|
| Multiple meaningful classes (not everything in one class)? | ✅ YES | 8 model classes, 4 controllers, 4 DAOs, 7 utils, 1 factory — across 5 named packages | — |
| Attributes private with getters/setters (encapsulation)? | ✅ YES | `UserAccount.java` lines 16–23: all fields `private`; lines 59–81: full getter/setter block with labels | — |
| Logic separated from GUI code? | ✅ YES | Business logic in `dao/`, `util/GradeCalculator.java`, `util/ReportGenerator.java`; GUI events only in `controller/` | — |
| Inheritance / abstraction / polymorphism used correctly? | ✅ YES | `UserAccount` is `abstract` with `getDashboardPath()` + `getRoleDisplayName()`; `AdminUser`, `TeacherUser`, `StudentUser` all extend it; `LoginController.java:84` calls `user.getDashboardPath()` polymorphically | — |
| Design patterns demonstrated? | ✅ YES | Singleton in `DatabaseConfig.java:50` (`synchronized getInstance()`); Factory Method in `UserFactory.java:36` (`createFromResultSet`); MVC throughout | — |
| Database design logical (tables, PKs, FKs)? | ✅ YES | `schema.sql`: 10 tables, all with `AUTO_INCREMENT PRIMARY KEY`, 13 FOREIGN KEY constraints with `ON DELETE CASCADE/SET NULL`, `UNIQUE KEY` on many-to-many join table (`enrollments`) | — |
| JDBC connectivity works reliably? | ✅ YES | App ran successfully (`[DB] Connection established successfully.`); PreparedStatement used throughout DAOs; try-with-resources confirmed in README:229 | — |
| Code readable and commented? | ✅ YES | JavaDoc-style block comments on every class; inline comments label every logical section (e.g., `// ── Input validation ──`) | Minor: some DAOs have sparse inline comments — add brief method-level Javadoc to DAO methods |
| Class diagram included and explained? | ⚠️ PARTIAL | `docs/UML_Class_Diagram.md` exists with full ASCII UML tree, multiplicity table, MVC layer diagram | Diagram is in Markdown (great), but **not referenced or embedded in any report** — 0 report marks. Must appear in the Final Report with a written explanation |

### 2.2 Section A Score Justification

| Band | Description |
|------|-------------|
| 0–10 | Broken / no submission |
| 11–14 | DB exists but OOP weak |
| 15–18 | Works but poor design |
| **19–22** | Good OOP + working DB (PASS) |
| **23–26** | Very good OOP ✅ |
| 27–30 | Excellent OOP + DB + architecture |

**Awarded: 24/30**

The project sits firmly in the **23–26 "Very Good OOP"** band. It demonstrates all four core OOP pillars with correctly placed code, two design patterns (Singleton + Factory Method), a normalised 10-table relational schema, and confirmed live JDBC connectivity. The only deduction reasons are:

- **−2**: The class diagram is in Markdown, not a formal `.png`/`.pdf` diagram tool export. Marks are reduced because no written explanation of the diagram lives in a report.
- **−4**: No formal Final Report exists (cross-category penalty bleeding into design presentation). Section A awards only what is *demonstrably present in code*.

---

## 3. SECTION B — GUIs & Forms (20/30)

### 3.1 Checklist

| Requirement | Verdict | Evidence | Fix to Gain Marks |
|---|---|---|---|
| All screens open and function? | ✅ YES | 4 FXML views confirmed: `LoginView.fxml`, `AdminDashboard.fxml`, `TeacherDashboard.fxml`, `StudentDashboard.fxml`; app launched successfully | — |
| Buttons perform correct actions? | ✅ YES | `LoginController.java:47` `handleLogin()` validates, authenticates, and navigates; `handleForgotPassword()` shows a dialog; buttons disable during login (`loginButton.setDisable(true)`) | — |
| Input validation implemented? | ✅ YES | `LoginController.java:52–61`: empty username and empty password each show a specific error message and focus the correct field | — |
| Error messages displayed? | ✅ YES | `errorLabel.setVisible(true)` with specific messages; `AlertUtil.showError()` used throughout controllers | — |
| UI consistent in layout/design? | ✅ YES | Single `styles.css` applied to all views; dark-themed consistent look per README | — |
| GUI connected to real data? | ✅ YES | `UserDAO.authenticate()` is called on login; all dashboard controllers use real DAO queries against MySQL | — |
| Screenshots exist somewhere (docs/report)? | ❌ NO | No `.png` screenshots found anywhere in `docs/`, `README.md`, or any separate folder | **Add screenshots** of all 4 dashboards to `docs/screenshots/`; embed them in README and report |

### 3.2 Section B Score Justification

| Band | Description |
|------|-------------|
| 0 | No GUI |
| 10–14 | GUI exists but broken |
| 15–18 | Basic working forms (PASS) |
| **19–22** | Complete acceptable design ✅ |
| 23–26 | Very user-friendly |
| 27–30 | Professional integrated UI |

**Awarded: 20/30**

The GUI is fully functional and connected to real data. The deduction reasons are:

- **−7**: No screenshots exist anywhere in the project. This is a hard rubric requirement and a major missing item.
- **−3**: Cannot verify visual consistency, professional animations, or accessibility without screenshots or a live walkthrough recording.

---

## 4. SECTION C — Final Report (0/30)

### 4.1 Search Results

| File Type | Search Results |
|-----------|---------------|
| `.pdf` | ❌ Not found |
| `.docx` | ❌ Not found |
| `.md` report (other than README/docs/UML) | ❌ Not found |
| `docs/` folder | ⚠️ Contains only `UML_Class_Diagram.md` |

### 4.2 Required Structure Check

| Section | Present? |
|---------|----------|
| Title page | ❌ |
| Table of Contents | ❌ |
| Executive Summary | ❌ |
| Introduction | ❌ |
| Literature Review | ❌ |
| Methodology | ❌ |
| Results with Screenshots | ❌ |
| Testing Evidence | ❌ |
| Discussion / Critical Reflection | ❌ |
| Conclusion | ❌ |
| Harvard References | ❌ |
| Appendices (code listings, DB schema) | ❌ |

**Awarded: 0/30**

> ❌ No report file exists. All 30 marks are forfeited. This is the single biggest mark loss in the project.

---

## 5. SECTION D — Presentation / Video (0/10)

### 5.1 Search Results

| File Type | Search Results |
|-----------|---------------|
| `.mp4` / `.mov` / `.avi` | ❌ Not found |
| `.pptx` / `.ppt` | ❌ Not found |
| YouTube / OneDrive link in README | ❌ Not found |
| `/video` or `/presentation` folder | ❌ Not found |

**Awarded: 0/10**

> ❌ No presentation or video exists anywhere in the project. All 10 marks are forfeited.

---

## 6. TOP 10 "MARKS FASTEST" ACTIONS

Ranked by estimated mark gain, highest first.

| Priority | Action | Where | Steps | Est. Gain |
|---|---|---|---|---|
| 🥇 1 | **Write the Final Report** | Create `docs/FinalReport.docx` or `docs/FinalReport.pdf` | 1. Use structure: Title, TOC, Exec Summary, Introduction, Literature Review (cite OOP texts), Methodology (explain MVC + DAO + Factory), Results (screenshots inside), Testing, Critical Reflection, Conclusion, References. 2. Embed `UML_Class_Diagram.md` content as a figure. 3. Use Harvard referencing. | **+20–27/30** |
| 🥈 2 | **Record a 4-minute demo video** | Upload to YouTube or OneDrive; paste link in README | 1. Screen-record the app running (login → Admin → Teacher → Student dashboards). 2. Narrate: explain `UserAccount` abstract class, Factory Method, Singleton DB. 3. Keep it under 5 minutes. | **+7–9/10** |
| 🥉 3 | **Add screenshots to docs/** | `docs/screenshots/` | 1. Take screenshots of all 4 screens (Login, Admin dashboard, Teacher dashboard, Student dashboard). 2. Name them clearly (`login_screen.png`, `admin_dashboard.png`, etc.). 3. Embed in `README.md` and in the Final Report. | **+5–7/30 (GUI section)** |
| 4 | **Embed UML diagram in report with written explanation** | In the Final Report | 1. Convert ASCII UML to a proper `.png` (use draw.io or PlantUML). 2. Insert as a numbered figure with caption. 3. Write 2–3 paragraphs explaining the inheritance tree, multiplicity, and design decisions. | **+2–3/30 (OO section)** |
| 5 | **Add a Testing section to the report** | In the Final Report | 1. Create a test table (Feature | Input | Expected | Actual | Pass/Fail). 2. Include login validation, role-based access, grade calculation, and DB error handling tests. 3. Include error screenshots. | **+2–3/30 (Report section)** |
| 6 | **Add Critical Reflection to the report** | In the Final Report | 1. Critically evaluate your design choices (why Singleton over connection pool? why MySQL over SQLite?). 2. Identify limitations (single-connection Singleton doesn't scale). 3. Suggest future improvements (add JUnit tests, use HikariCP, add REST API). | **+2/30 (Report section)** |
| 7 | **Add Javadoc to all DAO methods** | `src/main/java/com/sms/dao/*.java` | 1. Add `/** @param ... @return ... @throws ... */` above each public method. 2. Run `mvn javadoc:javadoc` and include the output in appendix. | **+1/30 (OO readability)** |
| 8 | **Export the UML to a proper image** | `docs/class_diagram.png` | 1. Paste the `UML_Class_Diagram.md` Mermaid content into draw.io or app.diagrams.net. 2. Export as `.png`. 3. Link it in README as an embedded image. | **+1/30 (OO design section)** |
| 9 | **Add Harvard References to README** | `README.md` | 1. Add an `## References` section. 2. Cite: GoF Design Patterns book, JavaFX documentation, MySQL docs, PDFBox docs. 3. Use Harvard format. | **+1/30 (reports bleed-over)** |
| 10 | **Add a video / presentation link to README** | `README.md` | 1. Add `## 🎥 Demo Video` section. 2. Paste the YouTube or cloud link. 3. Mention what was demonstrated. | **Supporting evidence for Section D** |

---

## 7. SUMMARY

```
OO Design & Development:   24/30   ✅ Very Good
GUIs & Forms:              20/30   ✅ Complete Acceptable Design
Final Report:               0/30   ❌ MISSING — biggest risk
Presentation/Video:         0/10   ❌ MISSING
─────────────────────────────────
TOTAL:                     44/100  ⚠️ BARE PASS

If report + video are submitted:   ~75–80/100  (Very Good band)
```

### Key Message
> The **code is genuinely good** — solid OOP, working database, clean architecture, validated GUI. The grade is dragged down entirely by two missing deliverables: **the Final Report and the Video**. Writing the report alone could move you from 44 to ~70+. Both together puts you in the **Very Good** band.

---

*Reviewed on 26 March 2026 using full static analysis of source code, database schema, FXML views, and workspace file search.*
