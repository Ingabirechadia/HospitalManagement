package com.example.HMS.service;

import com.example.HMS.dto.response.ReportResponse;
import com.example.HMS.entity.Appointment;
import com.example.HMS.enums.AppointmentStatus;
import com.example.HMS.enums.AvailabilityStatus;
import com.example.HMS.repository.AppointmentRepository;
import com.example.HMS.repository.DoctorRepository;
import com.example.HMS.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Cacheable(value = "reports", key = "#reportType + '_' + #startDate + '_' + #endDate")
    public ReportResponse generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();

        switch (reportType.toLowerCase()) {
            case "daily":
                data = generateDailyReport(startDate);
                break;
            case "weekly":
                data = generateWeeklyReport(startDate, endDate);
                break;
            case "monthly":
                data = generateMonthlyReport(startDate, endDate);
                break;
            case "patients":
                data = generatePatientReport();
                break;
            case "doctors":
                data = generateDoctorReport();
                break;
            case "services":
                data = generateServiceReport(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid report type: " + reportType);
        }

        return new ReportResponse(reportType, LocalDate.now().toString(), data);
    }

    private Map<String, Object> generateDailyReport(LocalDate date) {
        Map<String, Object> data = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(date);

        data.put("date", date.toString());
        data.put("totalAppointments", appointments.size());
        data.put("pending", appointments.stream().filter(a -> a.getStatus().equals(AppointmentStatus.PENDING)).count());
        data.put("approved", appointments.stream().filter(a -> a.getStatus().equals(AppointmentStatus.APPROVED)).count());
        data.put("completed", appointments.stream().filter(a -> a.getStatus().equals(AppointmentStatus.COMPLETED)).count());
        data.put("cancelled", appointments.stream().filter(a -> a.getStatus().equals(AppointmentStatus.CANCELLED)).count());
        data.put("rejected", appointments.stream().filter(a -> a.getStatus().equals(AppointmentStatus.REJECTED)).count());

        return data;
    }

    private Map<String, Object> generateWeeklyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(startDate, endDate);

        data.put("startDate", startDate.toString());
        data.put("endDate", endDate.toString());
        data.put("totalAppointments", appointments.size());


        Map<LocalDate, Long> appointmentsByDay = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getAppointmentDate, Collectors.counting()));
        data.put("appointmentsByDay", appointmentsByDay);

        return data;
    }

    private Map<String, Object> generateMonthlyReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(startDate, endDate);

        data.put("startDate", startDate.toString());
        data.put("endDate", endDate.toString());
        data.put("totalAppointments", appointments.size());


        Map<AppointmentStatus, Long> appointmentsByStatus = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        data.put("appointmentsByStatus", appointmentsByStatus);

        return data;
    }

    private Map<String, Object> generatePatientReport() {
        Map<String, Object> data = new HashMap<>();
        long totalPatients = patientRepository.count();
        long activePatients = patientRepository.findAllActive().size();

        data.put("totalPatients", totalPatients);
        data.put("activePatients", activePatients);
        data.put("inactivePatients", totalPatients - activePatients);

        return data;
    }

    private Map<String, Object> generateDoctorReport() {
        Map<String, Object> data = new HashMap<>();
        long totalDoctors = doctorRepository.count();
        long activeDoctors = doctorRepository.findActiveDoctorsByAvailabilityStatus(AvailabilityStatus.AVAILABLE).size();

        data.put("totalDoctors", totalDoctors);
        data.put("activeDoctors", activeDoctors);
        data.put("inactiveDoctors", totalDoctors - activeDoctors);

        return data;
    }

    private Map<String, Object> generateServiceReport(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> data = new HashMap<>();
        List<Appointment> appointments = appointmentRepository.findByAppointmentDateBetween(startDate, endDate);


        Map<String, Long> appointmentsByService = appointments.stream()
                .filter(a -> a.getService() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getService().getName(),
                        Collectors.counting()
                ));

        data.put("startDate", startDate.toString());
        data.put("endDate", endDate.toString());
        data.put("totalAppointments", appointments.size());
        data.put("appointmentsByService", appointmentsByService);


        if (!appointmentsByService.isEmpty()) {
            String mostRequested = appointmentsByService.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
            data.put("mostRequestedService", mostRequested);
        }

        return data;
    }
}