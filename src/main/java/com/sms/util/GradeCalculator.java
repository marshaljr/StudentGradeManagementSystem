package com.sms.util;

import com.sms.model.Grade;
import com.sms.model.Assessment;

import java.util.List;
import java.util.Map;

/**
 * GradeCalculator — Contains all grade calculation logic.
 *
 * Separated from model classes to keep business logic in one place.
 */
public class GradeCalculator {

    private GradeCalculator() {}

    /**
     * Calculates the weighted final percentage for a student in a course.
     *
     * Formula:
     *   finalPercent = Σ( (marksObtained / maxMarks) * weight ) / Σ(weight) * 100
     *
     * @param grades      list of Grade objects for the student
     * @param assessments map of assessmentId → Assessment (to get maxMarks and weight)
     * @return final percentage (0–100), or -1 if no grades found
     */
    public static double calculateFinalPercentage(
            List<Grade> grades,
            Map<Integer, Assessment> assessments) {

        if (grades == null || grades.isEmpty()) return -1;

        double weightedSum   = 0;
        double totalWeight   = 0;

        for (Grade g : grades) {
            if (g.getMarksObtained() < 0) continue; // skip ungraded
            Assessment a = assessments.get(g.getAssessmentId());
            if (a == null) continue;

            double percentage = (g.getMarksObtained() / a.getMaxMarks()) * 100.0;
            weightedSum += percentage * a.getWeight();
            totalWeight += a.getWeight();
        }

        if (totalWeight == 0) return -1;
        return weightedSum / totalWeight;
    }

    /**
     * Converts a numeric percentage to a letter grade using the specified scale.
     *
     * @param percentage 0–100
     * @param scale      "STANDARD", "STRICT", or "GENEROUS"
     * @return letter grade string (A+, A, B+, ... F)
     */
    public static String getLetterGrade(double percentage, String scale) {
        if ("STRICT".equalsIgnoreCase(scale)) {
            if (percentage >= 95) return "A+";
            if (percentage >= 90) return "A";
            if (percentage >= 85) return "A-";
            if (percentage >= 80) return "B+";
            if (percentage >= 75) return "B";
            if (percentage >= 70) return "B-";
            if (percentage >= 65) return "C+";
            if (percentage >= 60) return "C";
            if (percentage >= 55) return "C-";
            if (percentage >= 50) return "D";
            return "F";
        } else if ("GENEROUS".equalsIgnoreCase(scale)) {
            if (percentage >= 85) return "A+";
            if (percentage >= 80) return "A";
            if (percentage >= 75) return "A-";
            if (percentage >= 70) return "B+";
            if (percentage >= 65) return "B";
            if (percentage >= 60) return "B-";
            if (percentage >= 55) return "C+";
            if (percentage >= 50) return "C";
            if (percentage >= 45) return "C-";
            if (percentage >= 40) return "D";
            return "F";
        } else {
            // STANDARD scale
            if (percentage >= 90) return "A+";
            if (percentage >= 85) return "A";
            if (percentage >= 80) return "A-";
            if (percentage >= 75) return "B+";
            if (percentage >= 70) return "B";
            if (percentage >= 65) return "B-";
            if (percentage >= 60) return "C+";
            if (percentage >= 55) return "C";
            if (percentage >= 50) return "C-";
            if (percentage >= 45) return "D";
            return "F";
        }
    }

    /**
     * Converts a letter grade to a grade point (4.0 scale).
     *
     * @param letterGrade e.g. "A+", "B", "F"
     * @return grade point value
     */
    public static double getGradePoint(String letterGrade) {
        return switch (letterGrade) {
            case "A+", "A" -> 4.00;
            case "A-"      -> 3.70;
            case "B+"      -> 3.30;
            case "B"       -> 3.00;
            case "B-"      -> 2.70;
            case "C+"      -> 2.30;
            case "C"       -> 2.00;
            case "C-"      -> 1.70;
            case "D"       -> 1.00;
            default        -> 0.00; // F or unknown
        };
    }

    /**
     * Calculates a student's GPA across multiple courses.
     *
     * @param courseGrades  map of courseCredits → letterGrade
     * @return GPA on a 4.0 scale, rounded to 2 decimal places
     */
    public static double calculateGPA(Map<Integer, String> courseGrades) {
        double totalPoints  = 0;
        int    totalCredits = 0;

        for (Map.Entry<Integer, String> entry : courseGrades.entrySet()) {
            int credits = entry.getKey();
            double gp   = getGradePoint(entry.getValue());
            totalPoints  += gp * credits;
            totalCredits += credits;
        }

        if (totalCredits == 0) return 0.0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }

    /**
     * Determines whether a student passes or fails based on their final percentage.
     *
     * @param percentage 0–100
     * @param scale      "STANDARD", "STRICT", or "GENEROUS"
     * @return "PASS" or "FAIL"
     */
    public static String getPassFailStatus(double percentage, String scale) {
        if (percentage < 0) return "N/A";
        String letter = getLetterGrade(percentage, scale);
        return "F".equalsIgnoreCase(letter) ? "FAIL" : "PASS";
    }

    /**
     * Validates that a marks value is within [0, maxMarks].
     *
     * @param marks    the value to validate
     * @param maxMarks the maximum allowed marks
     * @return true if valid
     */
    public static boolean isValidMarks(double marks, double maxMarks) {
        return marks >= 0 && marks <= maxMarks;
    }
}
