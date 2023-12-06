package com.example.demo.service;

import com.example.demo.dto.SubredditDto;
import com.example.demo.exception.SpringRedditException;
import com.example.demo.mapper.SubredditMapper;
import com.example.demo.model.Subreddit;
import com.example.demo.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor

public class SubredditService {
    @Autowired
    private SubredditRepository subredditRepository;
    @Autowired

    private final SubredditMapper subredditMapper;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto)
    {

       Subreddit save=subredditRepository.save(subredditMapper.mapDtoToSubreddit(subredditDto));
       subredditDto.setId(save.getId());
       return subredditDto;

    }

//    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
//        return Subreddit.builder().name(subredditDto.getName()).description(subredditDto.getDescription()).build();
//    }
 @Transactional(readOnly=true)
    public List<SubredditDto> getAll() {
       return subredditRepository.findAll().stream().map(subredditMapper::mapSubredditToDto).collect(Collectors.toList());
    }

    public SubredditDto getSubreddit(Long id) throws SpringRedditException {
        Subreddit subreddit=subredditRepository.findById(id).orElseThrow(()->
          new SpringRedditException("No subreddit found with the id "+id)
        );
        return subredditMapper.mapSubredditToDto(subreddit);
    }

//    private SubredditDto mapToDto(Subreddit subreddit) {
//        return SubredditDto.builder().name(subreddit.getName()).id(subreddit.getId()).description(subreddit.getDescription()).build();
//    }
}
