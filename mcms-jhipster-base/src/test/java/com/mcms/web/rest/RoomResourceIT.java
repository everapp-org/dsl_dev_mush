package com.mcms.web.rest;

import static com.mcms.domain.RoomAsserts.*;
import static com.mcms.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mcms.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcms.IntegrationTest;
import com.mcms.domain.Room;
import com.mcms.domain.enumeration.RoomStatus;
import com.mcms.domain.enumeration.RoomType;
import com.mcms.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link RoomResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RoomResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final RoomType DEFAULT_ROOM_TYPE = RoomType.STERILE_ZONE;
    private static final RoomType UPDATED_ROOM_TYPE = RoomType.INCUBATION;

    private static final RoomStatus DEFAULT_STATUS = RoomStatus.ACTIVE;
    private static final RoomStatus UPDATED_STATUS = RoomStatus.MAINTENANCE;

    private static final Integer DEFAULT_CAPACITY_BAGS = 1;
    private static final Integer UPDATED_CAPACITY_BAGS = 2;

    private static final Integer DEFAULT_CURRENT_OCCUPANCY = 1;
    private static final Integer UPDATED_CURRENT_OCCUPANCY = 2;

    private static final BigDecimal DEFAULT_AREA_SQ_M = new BigDecimal(1);
    private static final BigDecimal UPDATED_AREA_SQ_M = new BigDecimal(2);

    private static final Boolean DEFAULT_HAS_HVAC = false;
    private static final Boolean UPDATED_HAS_HVAC = true;

    private static final Boolean DEFAULT_HAS_MISTING = false;
    private static final Boolean UPDATED_HAS_MISTING = true;

    private static final LocalDate DEFAULT_LAST_DISINFECTION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_LAST_DISINFECTION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/rooms";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoomMockMvc;

    private Room room;

    private Room insertedRoom;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createEntity() {
        return new Room()
            .name(DEFAULT_NAME)
            .code(DEFAULT_CODE)
            .roomType(DEFAULT_ROOM_TYPE)
            .status(DEFAULT_STATUS)
            .capacityBags(DEFAULT_CAPACITY_BAGS)
            .currentOccupancy(DEFAULT_CURRENT_OCCUPANCY)
            .areaSqM(DEFAULT_AREA_SQ_M)
            .hasHVAC(DEFAULT_HAS_HVAC)
            .hasMisting(DEFAULT_HAS_MISTING)
            .lastDisinfectionDate(DEFAULT_LAST_DISINFECTION_DATE)
            .note(DEFAULT_NOTE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createUpdatedEntity() {
        return new Room()
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .roomType(UPDATED_ROOM_TYPE)
            .status(UPDATED_STATUS)
            .capacityBags(UPDATED_CAPACITY_BAGS)
            .currentOccupancy(UPDATED_CURRENT_OCCUPANCY)
            .areaSqM(UPDATED_AREA_SQ_M)
            .hasHVAC(UPDATED_HAS_HVAC)
            .hasMisting(UPDATED_HAS_MISTING)
            .lastDisinfectionDate(UPDATED_LAST_DISINFECTION_DATE)
            .note(UPDATED_NOTE);
    }

    @BeforeEach
    void initTest() {
        room = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedRoom != null) {
            roomRepository.delete(insertedRoom);
            insertedRoom = null;
        }
    }

    @Test
    @Transactional
    void createRoom() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Room
        var returnedRoom = om.readValue(
            restRoomMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Room.class
        );

        // Validate the Room in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertRoomUpdatableFieldsEquals(returnedRoom, getPersistedRoom(returnedRoom));

        insertedRoom = returnedRoom;
    }

    @Test
    @Transactional
    void createRoomWithExistingId() throws Exception {
        // Create the Room with an existing ID
        room.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        room.setName(null);

        // Create the Room, which fails.

        restRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        room.setCode(null);

        // Create the Room, which fails.

        restRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRoomTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        room.setRoomType(null);

        // Create the Room, which fails.

        restRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        room.setStatus(null);

        // Create the Room, which fails.

        restRoomMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRooms() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        // Get all the roomList
        restRoomMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].roomType").value(hasItem(DEFAULT_ROOM_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].capacityBags").value(hasItem(DEFAULT_CAPACITY_BAGS)))
            .andExpect(jsonPath("$.[*].currentOccupancy").value(hasItem(DEFAULT_CURRENT_OCCUPANCY)))
            .andExpect(jsonPath("$.[*].areaSqM").value(hasItem(sameNumber(DEFAULT_AREA_SQ_M))))
            .andExpect(jsonPath("$.[*].hasHVAC").value(hasItem(DEFAULT_HAS_HVAC)))
            .andExpect(jsonPath("$.[*].hasMisting").value(hasItem(DEFAULT_HAS_MISTING)))
            .andExpect(jsonPath("$.[*].lastDisinfectionDate").value(hasItem(DEFAULT_LAST_DISINFECTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE)));
    }

    @Test
    @Transactional
    void getRoom() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        // Get the room
        restRoomMockMvc
            .perform(get(ENTITY_API_URL_ID, room.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(room.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.roomType").value(DEFAULT_ROOM_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.capacityBags").value(DEFAULT_CAPACITY_BAGS))
            .andExpect(jsonPath("$.currentOccupancy").value(DEFAULT_CURRENT_OCCUPANCY))
            .andExpect(jsonPath("$.areaSqM").value(sameNumber(DEFAULT_AREA_SQ_M)))
            .andExpect(jsonPath("$.hasHVAC").value(DEFAULT_HAS_HVAC))
            .andExpect(jsonPath("$.hasMisting").value(DEFAULT_HAS_MISTING))
            .andExpect(jsonPath("$.lastDisinfectionDate").value(DEFAULT_LAST_DISINFECTION_DATE.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE));
    }

    @Test
    @Transactional
    void getNonExistingRoom() throws Exception {
        // Get the room
        restRoomMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingRoom() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room
        Room updatedRoom = roomRepository.findById(room.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedRoom are not directly saved in db
        em.detach(updatedRoom);
        updatedRoom
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .roomType(UPDATED_ROOM_TYPE)
            .status(UPDATED_STATUS)
            .capacityBags(UPDATED_CAPACITY_BAGS)
            .currentOccupancy(UPDATED_CURRENT_OCCUPANCY)
            .areaSqM(UPDATED_AREA_SQ_M)
            .hasHVAC(UPDATED_HAS_HVAC)
            .hasMisting(UPDATED_HAS_MISTING)
            .lastDisinfectionDate(UPDATED_LAST_DISINFECTION_DATE)
            .note(UPDATED_NOTE);

        restRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedRoom.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedRoom))
            )
            .andExpect(status().isOk());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedRoomToMatchAllProperties(updatedRoom);
    }

    @Test
    @Transactional
    void putNonExistingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(put(ENTITY_API_URL_ID, room.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(room))
            )
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(room)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoomWithPatch() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room using partial update
        Room partialUpdatedRoom = new Room();
        partialUpdatedRoom.setId(room.getId());

        partialUpdatedRoom
            .currentOccupancy(UPDATED_CURRENT_OCCUPANCY)
            .hasHVAC(UPDATED_HAS_HVAC)
            .lastDisinfectionDate(UPDATED_LAST_DISINFECTION_DATE);

        restRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoom))
            )
            .andExpect(status().isOk());

        // Validate the Room in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedRoom, room), getPersistedRoom(room));
    }

    @Test
    @Transactional
    void fullUpdateRoomWithPatch() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the room using partial update
        Room partialUpdatedRoom = new Room();
        partialUpdatedRoom.setId(room.getId());

        partialUpdatedRoom
            .name(UPDATED_NAME)
            .code(UPDATED_CODE)
            .roomType(UPDATED_ROOM_TYPE)
            .status(UPDATED_STATUS)
            .capacityBags(UPDATED_CAPACITY_BAGS)
            .currentOccupancy(UPDATED_CURRENT_OCCUPANCY)
            .areaSqM(UPDATED_AREA_SQ_M)
            .hasHVAC(UPDATED_HAS_HVAC)
            .hasMisting(UPDATED_HAS_MISTING)
            .lastDisinfectionDate(UPDATED_LAST_DISINFECTION_DATE)
            .note(UPDATED_NOTE);

        restRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRoom.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedRoom))
            )
            .andExpect(status().isOk());

        // Validate the Room in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertRoomUpdatableFieldsEquals(partialUpdatedRoom, getPersistedRoom(partialUpdatedRoom));
    }

    @Test
    @Transactional
    void patchNonExistingRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(patch(ENTITY_API_URL_ID, room.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(room)))
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(room))
            )
            .andExpect(status().isBadRequest());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRoom() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        room.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoomMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(room)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Room in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRoom() throws Exception {
        // Initialize the database
        insertedRoom = roomRepository.saveAndFlush(room);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the room
        restRoomMockMvc
            .perform(delete(ENTITY_API_URL_ID, room.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return roomRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Room getPersistedRoom(Room room) {
        return roomRepository.findById(room.getId()).orElseThrow();
    }

    protected void assertPersistedRoomToMatchAllProperties(Room expectedRoom) {
        assertRoomAllPropertiesEquals(expectedRoom, getPersistedRoom(expectedRoom));
    }

    protected void assertPersistedRoomToMatchUpdatableProperties(Room expectedRoom) {
        assertRoomAllUpdatablePropertiesEquals(expectedRoom, getPersistedRoom(expectedRoom));
    }
}
