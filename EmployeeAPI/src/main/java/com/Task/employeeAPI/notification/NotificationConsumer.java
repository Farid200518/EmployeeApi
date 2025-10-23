//package com.Task.employeeAPI.notification;
//
//import com.Task.employeeAPI.config.RabbitMQConfig;
//import com.Task.employeeAPI.dto.NotificationDTO;
//import com.Task.employeeAPI.exceptions.BadRequestException;
//import com.Task.employeeAPI.services.concrete.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationConsumer {
//
//    private final EmailService emailService;
//
//    @KafkaListener(topics = "email-topic", groupId = "email-group", containerFactory = "kafkaListenerContainerFactory")
//    public void consume(NotificationDTO dto) {
//        if (dto.getEmail() == null) {
//             throw new BadRequestException("Email is null!");
//        }
//        emailService.sendEmail(dto.getEmail(), dto.getSubject(), dto.getBody());
//    }
//}
//
//
