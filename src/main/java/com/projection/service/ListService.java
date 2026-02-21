package com.projection.service;

import com.projection.dto.content.AddToListRequestDto;
import com.projection.dto.content.CreateListRequestDto;
import com.projection.dto.content.ListItemResponseDto;
import com.projection.dto.content.ListResponseDto;
import com.projection.entity.content.ContentReference;
import com.projection.entity.enums.ContentType;
import com.projection.entity.list.ListItem;
import com.projection.entity.list.UserList;
import com.projection.entity.user.User;
import com.projection.exception.ResourceNotFoundException;
import com.projection.repository.ContentReferenceRepository;
import com.projection.repository.ListItemRepository;
import com.projection.repository.UserListRepository;
import com.projection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListService {

    private final UserListRepository userListRepository;
    private final ListItemRepository listItemRepository;
    private final UserRepository userRepository;
    private final ContentReferenceRepository contentReferenceRepository;

    @Transactional(readOnly = true)
    public List<ListResponseDto> getUserLists(Long userId) {
        log.info("Fetching lists for user ID: {}", userId);

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<UserList> lists = userListRepository.findByUserId(userId);
        return lists.stream()
                .filter(list -> !list.getIsDefault()) // exclude system lists like Watchlist, Favorites etc.
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ListResponseDto createList(Long userId, CreateListRequestDto request) {
        log.info("Creating list for user ID: {}, name: {}", userId, request.getName());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if a list with the same name already exists
        if (userListRepository.findByUserIdAndName(userId, request.getName()).isPresent()) {
            throw new IllegalStateException("A list with this name already exists");
        }

        UserList userList = UserList.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .isDefault(false)
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .build();

        userList = userListRepository.save(userList);
        log.info("List created successfully with ID: {}", userList.getId());

        return convertToDto(userList);
    }

    @Transactional
    public void deleteList(UUID listId, Long userId) {
        log.info("Deleting list ID: {} for user ID: {}", listId, userId);

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with ID: " + listId));

        if (!userList.getUser().getId().equals(userId)) {
            throw new SecurityException("You don't have permission to delete this list");
        }

        if (userList.getIsDefault()) {
            throw new IllegalStateException("Cannot delete a default list");
        }

        userListRepository.delete(userList);
        log.info("List deleted successfully");
    }

    @Transactional(readOnly = true)
    public ListResponseDto getListWithItems(UUID listId, Long userId) {
        log.info("Fetching list items for list ID: {}", listId);

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with ID: " + listId));

        if (!userList.getUser().getId().equals(userId) && !userList.getIsPublic()) {
            throw new SecurityException("You don't have permission to view this list");
        }

        List<ListItem> items = listItemRepository.findByListId(listId);
        List<ListItemResponseDto> itemDtos = items.stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList());

        return ListResponseDto.builder()
                .id(userList.getId().toString())
                .name(userList.getName())
                .description(userList.getDescription())
                .isPublic(userList.getIsPublic())
                .itemCount(itemDtos.size())
                .createdAt(userList.getCreatedAt())
                .updatedAt(userList.getUpdatedAt())
                .items(itemDtos)
                .build();
    }

    @Transactional
    public ListItemResponseDto addToList(UUID listId, Long userId, AddToListRequestDto request) {
        log.info("Adding content to list ID: {}, TMDB ID: {}, Type: {}",
                listId, request.getTmdbId(), request.getContentType());

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with ID: " + listId));

        if (!userList.getUser().getId().equals(userId)) {
            throw new SecurityException("You don't have permission to modify this list");
        }

        if (listItemRepository.existsByListIdAndContent(listId, request.getTmdbId(), request.getContentType())) {
            throw new IllegalStateException("Content is already in this list");
        }

        ContentReference contentReference = contentReferenceRepository
                .findByTmdbIdAndContentType(request.getTmdbId(), request.getContentType())
                .orElseGet(() -> {
                    ContentReference newContent = ContentReference.builder()
                            .tmdbId(request.getTmdbId())
                            .contentType(request.getContentType())
                            .build();
                    return contentReferenceRepository.save(newContent);
                });

        ListItem listItem = ListItem.builder()
                .userList(userList)
                .contentReference(contentReference)
                .notes(request.getNotes())
                .build();

        listItem = listItemRepository.save(listItem);
        log.info("Content added to list successfully");

        return convertItemToDto(listItem);
    }

    @Transactional
    public void removeFromList(UUID listId, Long userId, Long tmdbId, ContentType contentType) {
        log.info("Removing content from list ID: {}, TMDB ID: {}, Type: {}", listId, tmdbId, contentType);

        UserList userList = userListRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("List not found with ID: " + listId));

        if (!userList.getUser().getId().equals(userId)) {
            throw new SecurityException("You don't have permission to modify this list");
        }

        ListItem listItem = listItemRepository.findByListIdAndContent(listId, tmdbId, contentType)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in list"));

        listItemRepository.delete(listItem);
        log.info("Content removed from list successfully");
    }

    @Transactional(readOnly = true)
    public Map<String, Boolean> checkContentInLists(Long userId, Long tmdbId, ContentType contentType) {
        log.info("Checking lists for user ID: {} that contain TMDB ID: {}", userId, tmdbId);

        List<UserList> lists = userListRepository.findByUserId(userId)
                .stream()
                .filter(list -> !list.getIsDefault())
                .collect(Collectors.toList());

        return lists.stream().collect(Collectors.toMap(
                list -> list.getId().toString(),
                list -> listItemRepository.existsByListIdAndContent(list.getId(), tmdbId, contentType)));
    }

    private ListResponseDto convertToDto(UserList userList) {
        List<ListItem> items = listItemRepository.findByListId(userList.getId());
        return ListResponseDto.builder()
                .id(userList.getId().toString())
                .name(userList.getName())
                .description(userList.getDescription())
                .isPublic(userList.getIsPublic())
                .itemCount(items.size())
                .createdAt(userList.getCreatedAt())
                .updatedAt(userList.getUpdatedAt())
                .build();
    }

    private ListItemResponseDto convertItemToDto(ListItem listItem) {
        return ListItemResponseDto.builder()
                .id(listItem.getId().toString())
                .tmdbId(listItem.getContentReference().getTmdbId())
                .contentType(listItem.getContentReference().getContentType())
                .notes(listItem.getNotes())
                .addedAt(listItem.getAddedAt())
                .build();
    }
}
