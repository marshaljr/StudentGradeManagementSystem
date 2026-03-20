package com.sms.util;

import com.sms.dao.AttendanceDAO;
import com.sms.dao.CourseDAO;
import com.sms.dao.GradeDAO;
import com.sms.model.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportGenerator — Creates PDF transcripts / report cards and CSV exports.
 *
 * Uses Apache PDFBox for PDF and Apache Commons CSV for CSV output.
 */
public class ReportGenerator {

    private static final String SCHOOL_NAME = "Springfield Academy";
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    private final GradeDAO      gradeDAO      = new GradeDAO();
    private final CourseDAO     courseDAO     = new CourseDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    // ═══════════════════════════════════════════════════════════
    // PDF TRANSCRIPT
    // ═══════════════════════════════════════════════════════════

    /**
     * Generates a student transcript as a PDF file.
     *
     * @param student  the StudentUser whose transcript to generate
     * @param filePath absolute path of the output PDF
     * @return true on success
     */
    public boolean generateTranscript(StudentUser student, String filePath) {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float pageWidth = page.getMediaBox().getWidth();
            float margin = 60;
            float y = 720;

            // ── Header ────────────────────────────────────────────────
            cs.setFont(fontBold, 18);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText(SCHOOL_NAME);
            cs.endText();

            y -= 22;
            cs.setFont(fontRegular, 11);
            cs.beginText();
            cs.newLineAtOffset(margin, y);
            cs.showText("Official Academic Transcript");
            cs.endText();

            drawLine(cs, margin, y - 6, pageWidth - margin, y - 6);
            y -= 25;

            // ── Student Info ─────────────────────────────────────────
            cs.setFont(fontBold, 11);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Student Name: "); cs.endText();
            cs.setFont(fontRegular, 11);
            cs.beginText(); cs.newLineAtOffset(margin + 110, y);
            cs.showText(student.getFullName()); cs.endText();
            y -= 16;

            cs.setFont(fontBold, 11);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Student ID:   "); cs.endText();
            cs.setFont(fontRegular, 11);
            cs.beginText(); cs.newLineAtOffset(margin + 110, y);
            cs.showText(student.getStudentNumber()); cs.endText();
            y -= 16;

            cs.setFont(fontBold, 11);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Program:      "); cs.endText();
            cs.setFont(fontRegular, 11);
            cs.beginText(); cs.newLineAtOffset(margin + 110, y);
            cs.showText(nullSafe(student.getProgram())); cs.endText();
            y -= 16;

            cs.setFont(fontBold, 11);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Date Issued:  "); cs.endText();
            cs.setFont(fontRegular, 11);
            cs.beginText(); cs.newLineAtOffset(margin + 110, y);
            cs.showText(LocalDate.now().format(FMT)); cs.endText();
            y -= 25;

            drawLine(cs, margin, y, pageWidth - margin, y);
            y -= 20;

            // ── Grades Table Header ───────────────────────────────────
            cs.setFont(fontBold, 10);
            cs.beginText(); cs.newLineAtOffset(margin, y); cs.showText("Course");       cs.endText();
            cs.beginText(); cs.newLineAtOffset(280, y);    cs.showText("Credits");      cs.endText();
            cs.beginText(); cs.newLineAtOffset(330, y);    cs.showText("Final %");      cs.endText();
            cs.beginText(); cs.newLineAtOffset(390, y);    cs.showText("Grade");        cs.endText();
            cs.beginText(); cs.newLineAtOffset(440, y);    cs.showText("GPA Points");   cs.endText();
            cs.beginText(); cs.newLineAtOffset(510, y);    cs.showText("Status");       cs.endText();
            y -= 6;
            drawLine(cs, margin, y, pageWidth - margin, y);
            y -= 14;

            // ── Per-Course Rows ───────────────────────────────────────
            List<Course> courses = courseDAO.getCoursesByStudent(student.getStudentId());
            double totalWeightedPoints = 0;
            int    totalCredits        = 0;

            cs.setFont(fontRegular, 10);
            for (Course course : courses) {
                List<Grade>      grades      = gradeDAO.getGradesByStudentAndCourse(
                        student.getStudentId(), course.getCourseId());
                List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(course.getCourseId());

                Map<Integer, Assessment> aMap = new HashMap<>();
                for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);

                double pct         = GradeCalculator.calculateFinalPercentage(grades, aMap);
                String letter      = pct >= 0 ? GradeCalculator.getLetterGrade(pct, course.getGradingScale()) : "N/A";
                double gradePoint  = pct >= 0 ? GradeCalculator.getGradePoint(letter) : 0;
                int    credits     = course.getCredits();

                String pctStr = pct >= 0 ? String.format("%.1f%%", pct) : "-";
                String gpStr  = pct >= 0 ? String.format("%.2f", gradePoint) : "-";

                cs.beginText(); cs.newLineAtOffset(margin, y);
                cs.showText(truncate(course.getCourseCode() + " " + course.getCourseName(), 38));
                cs.endText();
                cs.beginText(); cs.newLineAtOffset(280, y); cs.showText(String.valueOf(credits)); cs.endText();
                cs.beginText(); cs.newLineAtOffset(330, y); cs.showText(pctStr);   cs.endText();
                cs.beginText(); cs.newLineAtOffset(390, y); cs.showText(letter);   cs.endText();
                cs.beginText(); cs.newLineAtOffset(440, y); cs.showText(gpStr);    cs.endText();
                String status = pct >= 0 ? GradeCalculator.getPassFailStatus(pct, course.getGradingScale()) : "N/A";
                cs.beginText(); cs.newLineAtOffset(510, y); cs.showText(status);   cs.endText();
                y -= 16;

                if (pct >= 0) {
                    totalWeightedPoints += gradePoint * credits;
                    totalCredits        += credits;
                }
            }

            // ── GPA Summary ───────────────────────────────────────────
            y -= 6;
            drawLine(cs, margin, y, pageWidth - margin, y);
            y -= 18;

            double gpa = totalCredits > 0
                    ? Math.round((totalWeightedPoints / totalCredits) * 100.0) / 100.0
                    : 0;

            cs.setFont(fontBold, 11);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Cumulative GPA: " + String.format("%.2f", gpa) + " / 4.00");
            cs.endText();
            y -= 16;
            cs.setFont(fontRegular, 10);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Total Credits Earned: " + totalCredits);
            cs.endText();

            y -= 40;
            drawLine(cs, margin, y, margin + 160, y);
            y -= 14;
            cs.setFont(fontRegular, 9);
            cs.beginText(); cs.newLineAtOffset(margin, y);
            cs.showText("Authorized Signature / Registrar");
            cs.endText();

            cs.close();
            doc.save(filePath);
            return true;

        } catch (IOException e) {
            System.err.println("[ReportGenerator] PDF error: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════
    // CSV GRADE EXPORT
    // ═══════════════════════════════════════════════════════════

    /**
     * Exports all grades for a course to a CSV file.
     *
     * @param course   the course to export
     * @param filePath output file path
     * @return true on success
     */
    public boolean exportGradesToCSV(Course course, String filePath) {
        List<Assessment> assessments = gradeDAO.getAssessmentsByCourse(course.getCourseId());
        List<StudentUser> students  = courseDAO.getEnrolledStudents(course.getCourseId());

        try (FileWriter fw = new FileWriter(filePath);
             CSVPrinter csv = new CSVPrinter(fw, CSVFormat.DEFAULT)) {

            // Header row
            List<String> header = new ArrayList<>();
            header.add("Student Number");
            header.add("Student Name");
            for (Assessment a : assessments) {
                header.add(a.getTitle() + " (" + a.getMaxMarks() + ")");
            }
            header.add("Final %");
            header.add("Letter Grade");
            header.add("Status");
            csv.printRecord(header);

            // Data rows
            Map<Integer, Assessment> aMap = new HashMap<>();
            for (Assessment a : assessments) aMap.put(a.getAssessmentId(), a);

            for (StudentUser s : students) {
                List<Grade> grades = gradeDAO.getGradesByStudentAndCourse(
                        s.getStudentId(), course.getCourseId());

                Map<Integer, Double> marksMap = new HashMap<>();
                for (Grade g : grades) marksMap.put(g.getAssessmentId(), g.getMarksObtained());

                List<Object> row = new ArrayList<>();
                row.add(s.getStudentNumber());
                row.add(s.getFullName());
                for (Assessment a : assessments) {
                    Double m = marksMap.get(a.getAssessmentId());
                    row.add(m != null ? m : "");
                }

                double pct    = GradeCalculator.calculateFinalPercentage(grades, aMap);
                String letter = pct >= 0 ? GradeCalculator.getLetterGrade(pct, course.getGradingScale()) : "";
                String status = pct >= 0 ? GradeCalculator.getPassFailStatus(pct, course.getGradingScale()) : "";
                row.add(pct >= 0 ? String.format("%.1f", pct) : "");
                row.add(letter);
                row.add(status);
                csv.printRecord(row);
            }
            return true;
        } catch (IOException e) {
            System.err.println("[ReportGenerator] CSV error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exports attendance summary for a course to CSV.
     */
    public boolean exportAttendanceToCSV(Course course, String filePath) {
        List<StudentUser> students = courseDAO.getEnrolledStudents(course.getCourseId());
        try (FileWriter fw = new FileWriter(filePath);
             CSVPrinter csv = new CSVPrinter(fw, CSVFormat.DEFAULT)) {

            csv.printRecord("Student Number", "Student Name",
                    "Present", "Absent", "Late", "Excused", "Attendance %");

            for (StudentUser s : students) {
                Map<String, Integer> summary = attendanceDAO.getAttendanceSummary(
                        s.getStudentId(), course.getCourseId());
                double pct = attendanceDAO.getAttendancePercentage(
                        s.getStudentId(), course.getCourseId());

                csv.printRecord(
                    s.getStudentNumber(),
                    s.getFullName(),
                    summary.getOrDefault("PRESENT", 0),
                    summary.getOrDefault("ABSENT",  0),
                    summary.getOrDefault("LATE",    0),
                    summary.getOrDefault("EXCUSED", 0),
                    String.format("%.1f%%", pct)
                );
            }
            return true;
        } catch (IOException e) {
            System.err.println("[ReportGenerator] CSV att error: " + e.getMessage());
            return false;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────

    private void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2)
            throws IOException {
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private String nullSafe(String s) { return s != null ? s : ""; }

    private String truncate(String s, int maxLen) {
        return s.length() > maxLen ? s.substring(0, maxLen - 1) + "…" : s;
    }
}
