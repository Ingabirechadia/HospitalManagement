package com.example.HMS.repository;

import com.example.HMS.entity.Appointment;
import com.example.HMS.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByAppointmentDate(LocalDate date);

    List<Appointment> findByAppointmentDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status != 'CANCELLED'")
    Long countActiveAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId,
                                                @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.status != 'CANCELLED'")
    List<Appointment> findActiveAppointmentsByDoctorAndDate(@Param("doctorId") Long doctorId,
                                                            @Param("date") LocalDate date);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
            "WHERE a.patient.id = :patientId AND a.appointmentDate = :date AND a.appointmentTime = :time " +
            "AND a.status != 'CANCELLED'")
    boolean existsByPatientAndDateAndTime(@Param("patientId") Long patientId,
                                          @Param("date") LocalDate date,
                                          @Param("time") LocalTime time);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND a.appointmentDate = :date AND a.appointmentTime = :time " +
            "AND a.status != 'CANCELLED'")
    boolean existsByDoctorAndDateAndTime(@Param("doctorId") Long doctorId,
                                         @Param("date") LocalDate date,
                                         @Param("time") LocalTime time);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :startDate AND :endDate AND a.status = :status")
    List<Appointment> findAppointmentsBetweenDatesAndStatus(@Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate,
                                                            @Param("status") AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate < :date AND a.status IN :statuses")
    List<Appointment> findPastAppointmentsWithStatus(@Param("date") LocalDate date,
                                                     @Param("statuses") List<AppointmentStatus> statuses);

    @Query("SELECT a FROM Appointment a WHERE a.patient.id = :patientId AND a.appointmentDate >= :date")
    List<Appointment> findUpcomingAppointmentsByPatient(@Param("patientId") Long patientId,
                                                        @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate >= :date")
    List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId,
                                                       @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.service.id = :serviceId AND a.appointmentDate BETWEEN :startDate AND :endDate")
    Long countAppointmentsByServiceAndDateRange(@Param("serviceId") Long serviceId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}