package com.mcms.web.rest;

import com.mcms.domain.Room;
import com.mcms.repository.RoomRepository;
import com.mcms.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mcms.domain.Room}.
 */
@RestController
@RequestMapping("/api/rooms")
@Transactional
public class RoomResource {

    private static final Logger LOG = LoggerFactory.getLogger(RoomResource.class);

    private static final String ENTITY_NAME = "room";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoomRepository roomRepository;

    public RoomResource(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    /**
     * {@code POST  /rooms} : Create a new room.
     *
     * @param room the room to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new room, or with status {@code 400 (Bad Request)} if the room has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) throws URISyntaxException {
        LOG.debug("REST request to save Room : {}", room);
        if (room.getId() != null) {
            throw new BadRequestAlertException("A new room cannot already have an ID", ENTITY_NAME, "idexists");
        }
        room = roomRepository.save(room);
        return ResponseEntity.created(new URI("/api/rooms/" + room.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, room.getId().toString()))
            .body(room);
    }

    /**
     * {@code PUT  /rooms/:id} : Updates an existing room.
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated room,
     * or with status {@code 400 (Bad Request)} if the room is not valid,
     * or with status {@code 500 (Internal Server Error)} if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Room> updateRoom(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Room room)
        throws URISyntaxException {
        LOG.debug("REST request to update Room : {}, {}", id, room);
        if (room.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, room.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        room = roomRepository.save(room);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, room.getId().toString()))
            .body(room);
    }

    /**
     * {@code PATCH  /rooms/:id} : Partial updates given fields of an existing room, field will ignore if it is null
     *
     * @param id the id of the room to save.
     * @param room the room to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated room,
     * or with status {@code 400 (Bad Request)} if the room is not valid,
     * or with status {@code 404 (Not Found)} if the room is not found,
     * or with status {@code 500 (Internal Server Error)} if the room couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Room> partialUpdateRoom(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Room room
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Room partially : {}, {}", id, room);
        if (room.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, room.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roomRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Room> result = roomRepository
            .findById(room.getId())
            .map(existingRoom -> {
                if (room.getName() != null) {
                    existingRoom.setName(room.getName());
                }
                if (room.getCode() != null) {
                    existingRoom.setCode(room.getCode());
                }
                if (room.getRoomType() != null) {
                    existingRoom.setRoomType(room.getRoomType());
                }
                if (room.getStatus() != null) {
                    existingRoom.setStatus(room.getStatus());
                }
                if (room.getCapacityBags() != null) {
                    existingRoom.setCapacityBags(room.getCapacityBags());
                }
                if (room.getCurrentOccupancy() != null) {
                    existingRoom.setCurrentOccupancy(room.getCurrentOccupancy());
                }
                if (room.getAreaSqM() != null) {
                    existingRoom.setAreaSqM(room.getAreaSqM());
                }
                if (room.getHasHVAC() != null) {
                    existingRoom.setHasHVAC(room.getHasHVAC());
                }
                if (room.getHasMisting() != null) {
                    existingRoom.setHasMisting(room.getHasMisting());
                }
                if (room.getLastDisinfectionDate() != null) {
                    existingRoom.setLastDisinfectionDate(room.getLastDisinfectionDate());
                }
                if (room.getNote() != null) {
                    existingRoom.setNote(room.getNote());
                }

                return existingRoom;
            })
            .map(roomRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, room.getId().toString())
        );
    }

    /**
     * {@code GET  /rooms} : get all the rooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rooms in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public List<Room> getAllRooms() {
        LOG.debug("REST request to get all Rooms");
        return roomRepository.findAll();
    }

    /**
     * {@code GET  /rooms/:id} : get the "id" room.
     *
     * @param id the id of the room to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the room, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_OPERATOR', 'ROLE_USER')")
    public ResponseEntity<Room> getRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Room : {}", id);
        Optional<Room> room = roomRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(room);
    }

    /**
     * {@code DELETE  /rooms/:id} : delete the "id" room.
     *
     * @param id the id of the room to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Room : {}", id);
        roomRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
