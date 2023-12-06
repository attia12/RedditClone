package com.example.demo.controller;

import com.example.demo.dto.VoteDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes/")
@AllArgsConstructor

public class VoteController {
    @Autowired
    private VoteService voteService;
    @PostMapping
    public ResponseEntity<Void>vote(@RequestBody VoteDto voteDto) throws SpringRedditException {
        voteService.vote(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
