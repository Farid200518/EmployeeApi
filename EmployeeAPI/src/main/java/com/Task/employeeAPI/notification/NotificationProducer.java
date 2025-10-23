//package com.Task.employeeAPI.notification;
//
//import com.Task.employeeAPI.dto.NotificationDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class NotificationProducer {
//
//    private final KafkaTemplate<String, NotificationDTO> kafkaTemplate;
//
//    public void sendNotification(NotificationDTO dto) {
//        kafkaTemplate.send("email-topic", dto);
//    }
//}
//
