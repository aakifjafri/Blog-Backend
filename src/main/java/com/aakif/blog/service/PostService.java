package com.aakif.blog.service;

import com.aakif.blog.dto.PostDto;
import com.aakif.blog.exception.PostNotFoundException;
import com.aakif.blog.model.Post;
import com.aakif.blog.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private AuthService authService;
    @Autowired
    private PostRepository postRepository;

    @Transactional
    public void createPost(PostDto postDto) {

        Post post = mapFromDtoToPost(postDto);
        postRepository.save(post);
    }

    private Post mapFromDtoToPost(PostDto postDto) {

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        User loggedUser = authService.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        post.setCreatedOn(Instant.now());
        post.setUsername(loggedUser.getUsername());
        post.setUpdatedOn(Instant.now());
        return post;

    }

    @Transactional
    public List<PostDto> showAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::mapFromPostToDto).collect(Collectors.toList());
    }

    private PostDto mapFromPostToDto(Post post) {

        PostDto postDto = new PostDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setContent(post.getContent());
        postDto.setUsername(post.getUsername());
        return postDto;
    }

    @Transactional
    public PostDto readSinglePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("No post found for id - "+id));
        return mapFromPostToDto(post);
    }
}
