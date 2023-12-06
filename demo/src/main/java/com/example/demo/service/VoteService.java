package com.example.demo.service;

import com.example.demo.dto.VoteDto;
import com.example.demo.exception.PostNotFoundException;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.model.Post;
import com.example.demo.model.Vote;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.demo.model.VoteType.UPVOTE;

@Service

public class VoteService {
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
   private PostRepository postRepository;
    @Autowired
    private AuthService authService;
    public void vote(VoteDto voteDto) throws SpringRedditException {
        Post post=postRepository.findById(voteDto.getPostId()).orElseThrow(()->new PostNotFoundException("Post not found with Id"+voteDto.getPostId()));
        Optional<Vote>voteByPostAndUser=voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,authService.getCurrentUser());
        if(voteByPostAndUser.isPresent() && voteByPostAndUser.get().getVoteType().equals(voteDto.getVoteType()))
        {
            throw new SpringRedditException("you have already "+voteDto.getVoteType());
        }
        if(UPVOTE.equals(voteDto.getVoteType()))
        {
            post.setVoteCount(post.getVoteCount()+1);
        }
        else {
            post.setVoteCount(post.getVoteCount()-1);

        }
        voteRepository.save(mapToVote(voteDto,post));
        postRepository.save(post);

    }

    private Vote mapToVote(VoteDto voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}
