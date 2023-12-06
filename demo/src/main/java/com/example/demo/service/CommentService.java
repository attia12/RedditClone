package com.example.demo.service;

import com.example.demo.dto.CommentsDto;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.mapper.CommentMapper;
import com.example.demo.model.Comment;
import com.example.demo.model.NotificationEmail;
import com.example.demo.model.Post;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class CommentService {
    private static final String POST_URL = "";
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    private final CommentMapper commentMapper;
    @Autowired
    private CommentRepository commentRepository;

    private final MailContentBuilder mailContentBuilder;

    private final MailService mailService;

    public void save(CommentsDto commentsDto) throws SpringRedditException {
        Post post=postRepository.findById(commentsDto.getPostId()).orElseThrow(()->new PostNotFoundException(commentsDto.getPostId().toString()));
        Comment comment=commentMapper.map(commentsDto,post,authService.getCurrentUser());
        commentRepository.save(comment);
        String message=mailContentBuilder.build(post.getUser().getUsername()+" posted a comment on your post." + POST_URL);
        sendCommentNotification(message,post.getUser());

    }

    private void sendCommentNotification(String message, User user) throws SpringRedditException {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " Commented on your post", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postID) {
        Post post=postRepository.findById(postID).orElseThrow(()->new PostNotFoundException(postID.toString()));
       return commentRepository.findByPost(post).stream().map(commentMapper::mapToDto ).collect(Collectors.toList());
    }

    public List<CommentsDto> getAllCommentsForUser(String username) {
       User user=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username)) ;
       return commentRepository.findAllByUser(user).stream().map(commentMapper::mapToDto).collect(Collectors.toList());

    }
}
