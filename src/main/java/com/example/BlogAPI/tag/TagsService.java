package com.example.BlogAPI.tag;

import com.example.BlogAPI.post.dto.PostResponse;
import com.example.BlogAPI.tag.dto.TagRequest;
import com.example.BlogAPI.tag.dto.TagResponse;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TagsService {
    private final TagsRepository tagsRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TagsService(TagsRepository tagsRepository, ModelMapper modelMapper) {
        this.tagsRepository = tagsRepository;
        this.modelMapper = modelMapper;
    }

    public List<TagResponse> getAllTags() {
        return tagsRepository.findAll().stream()
                .map(tag -> convertToTagResponseWithoutPosts(tag))
                .collect(Collectors.toList());
    }

    public TagResponse getTagByIdWithPosts(Long id) {
        Tag tag = tagsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No tag with id " + id));

        return convertToTagResponseWithPosts(tag);
    }

    @Transactional
    public TagResponse addTag(TagRequest tagRequest) {
        return convertToTagResponseWithoutPosts(tagsRepository.save(modelMapper.map(tagRequest, Tag.class)));
    }

    @Transactional
    public void deleteTagById(Long id) {
        if (!tagsRepository.existsById(id)) {
            throw new EntityNotFoundException("No tag with id " + id);
        }
        tagsRepository.deleteById(id);
    }

    private Tag convertToTag(TagRequest tagRequest) {
        return  modelMapper.map(tagRequest, Tag.class);
    }

    private TagResponse convertToTagResponseWithoutPosts(Tag tag) {
        return modelMapper.map(tag, TagResponse.class);
    }

    private TagResponse convertToTagResponseWithPosts(Tag tag) {
        TagResponse tagResponse = modelMapper.map(tag, TagResponse.class);

        List<PostResponse> postResponses = tag.getPosts().stream()
                .map(post -> modelMapper.map(post, PostResponse.class))
                .collect(Collectors.toList());

        tagResponse.setPostResponses(postResponses);
        return tagResponse;
    }
}
