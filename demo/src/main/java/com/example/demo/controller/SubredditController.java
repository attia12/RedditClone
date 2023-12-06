package com.example.demo.controller;

import com.example.demo.dto.SubredditDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.service.SubredditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subreddit")
@Slf4j

public class SubredditController {
    @Autowired
    private SubredditService subredditService;

    @PostMapping
    public ResponseEntity<SubredditDto> createSubreddit(@RequestBody SubredditDto subredditRequest)
    {
       return ResponseEntity.status(HttpStatus.CREATED).body(subredditService.save(subredditRequest));

    }
    @GetMapping
    public ResponseEntity<List<SubredditDto>> getAllSubreddit()
    {
       return ResponseEntity.status(HttpStatus.OK).body(subredditService.getAll()) ;
    }
    @GetMapping("/{id}")
    public ResponseEntity<SubredditDto>getSubreddit(@PathVariable Long id) throws SpringRedditException {
        return ResponseEntity.status(HttpStatus.OK).body(subredditService.getSubreddit(id));
    }
}
