package com.example.demo;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface MessageRepository extends CrudRepository<Message, Long>{
    ArrayList<Message> findByContentContainingIgnoreCaseOrDateContainingIgnoreCaseOrSentByContainingIgnoreCase(String content, String date, String sentBy);
}
