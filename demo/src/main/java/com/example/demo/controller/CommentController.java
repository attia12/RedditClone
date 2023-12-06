package com.example.demo.controller;

import com.example.demo.dto.CommentsDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/comments/")

public class CommentController {
    @Autowired
    private CommentService commentService;
    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) throws SpringRedditException {
        commentService.save(commentsDto);
       return new ResponseEntity<>(HttpStatus.CREATED);


    }
    @GetMapping("by-postId/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId)
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForPost(postId));


    }
    @GetMapping("by-user/{username}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForUser(@PathVariable String username)
    {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getAllCommentsForUser(username));
    }

}
