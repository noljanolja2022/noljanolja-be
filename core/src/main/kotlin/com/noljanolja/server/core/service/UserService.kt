package com.noljanolja.server.core.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.noljanolja.server.common.exception.UserNotFound
import com.noljanolja.server.core.exception.Error
import com.noljanolja.server.core.model.User
import com.noljanolja.server.core.model.UserContact
import com.noljanolja.server.core.model.UserDevice
import com.noljanolja.server.core.repo.user.*
import com.noljanolja.server.core.repo.user.UserDeviceModel.Companion.toUserDeviceModel
import com.noljanolja.server.core.repo.user.UserModel.Companion.toUserModel
import com.noljanolja.server.core.utils.parsePhoneNumber
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserService(
    private val userRepo: UserRepo,
    private val userContactsRepo: UserContactsRepo,
    private val userDevicesRepo: UserDevicesRepo,
    private val contactsRepo: ContactRepo,
    private val objectMapper: ObjectMapper,
) {

    suspend fun getUsersByIds(
        userIds: List<String>,
    ): List<User> {
        return userRepo.findAllById(userIds).map { it.toUser(objectMapper) }.toList()
    }

    suspend fun getUsers(
        page: Int,
        pageSize: Int,
        friendId: String?,
        phoneNumber: String?,
        name: String?,
    ): Pair<List<User>, Long> = coroutineScope {
        // friendId does not exist -> Find all
        if (!friendId.isNullOrBlank()) {
            // Get all contacts by friendId -> Collect phone
            val phones = mutableListOf<String>()
            contactsRepo.findAllContactsOfUser(
                userId = friendId,
                isBlocked = false,
            ).toList().forEach { contact ->
                contact.phoneNumber.takeIf { !it.isNullOrBlank() }?.let { phones.add(it) }
            }
            // Count the total
            val total = userRepo.countByPhoneNumberIn(
                phones = phones.sorted(),
            )
            // Get users
            val users = userRepo.findAllByPhoneNumberIn(
                phones = phones.sorted(),
                pageable = PageRequest.of(page - 1, pageSize),
            ).map { it.toUser(objectMapper) }.toList()
            Pair(users, total)
        } else if (!phoneNumber.isNullOrBlank()) {
            val phone = parsePhoneNumber(phoneNumber) ?: throw Error.InvalidPhoneNumber
            val phoneNumberString = phone.nationalNumber.toString()
            val users = userRepo.findAllByPhoneNumberIn(
                listOf(phoneNumberString),
                PageRequest.of(page - 1, pageSize)
            ).map { it.toUser(objectMapper) }.toList()
            val total = userRepo.countByPhoneNumberIn(
                phones = listOf(phoneNumberString),
            )
            Pair(users, total)
        } else { // if friendId exists -> Find by contact phones
            // Count the total
            val total = userRepo.count()
            // Get users
            val users = userRepo.findAllBy(PageRequest.of(page - 1, pageSize))
                .map { it.toUser(objectMapper) }.toList()
            Pair(users, total)
        }
    }

    suspend fun getBlackListOfUser(
        page: Int,
        pageSize: Int,
        userId: String,
    ): Pair<List<User>, Long> {
        val phones = mutableListOf<String>()
        contactsRepo.findAllContactsOfUser(
            userId = userId,
            isBlocked = true,
        ).toList().forEach { contact ->
            contact.phoneNumber.takeIf { !it.isNullOrBlank() }?.let { phones.add(it) }
        }
        // Count the total
        val total = userRepo.countByPhoneNumberIn(
            phones = phones.sorted(),
        )
        // Get users
        val users = userRepo.findAllByPhoneNumberIn(
            phones = phones.sorted(),
            pageable = PageRequest.of(page - 1, pageSize),
        ).map { it.toUser(objectMapper) }.toList()
        return Pair(users, total)
    }

    suspend fun findAllBlackListedUser(
        userId: String,
    ): List<User> {
        val phones = mutableListOf<String>()
        contactsRepo.findAllContactsOfUser(
            userId = userId,
            isBlocked = true,
        ).toList().forEach { contact ->
            contact.phoneNumber.takeIf { !it.isNullOrBlank() }?.let { phones.add(it) }
        }
        // Get users
        return userRepo.findAllByPhoneNumberIn(
            phones = phones.sorted(),
        ).map { it.toUser(objectMapper) }.toList()
    }

    suspend fun userBlockUser(
        userId: String,
        blockedUserId: String,
        isBlocked: Boolean,
    ) {
        if (userId == blockedUserId) return
        val usersInfo = userRepo.findAllById(listOf(userId, blockedUserId)).toList()
        usersInfo.find { it.id == userId } ?: throw UserNotFound
        val blockedUser = usersInfo.find { it.id == blockedUserId } ?: throw UserNotFound
        val blockedUserContact = contactsRepo.findByPhoneNumberAndCountryCode(
            phone = blockedUser.phoneNumber,
            countryCode = blockedUser.countryCode,
        ).toList().first()
        (userContactsRepo.findByUserIdAndContactId(
            userId = userId,
            contactId = blockedUserContact.id,
        ).toList().firstOrNull()?.apply {
            this.isBlocked = isBlocked
        } ?: UserContactModel(
            userId = userId,
            contactId = blockedUserContact.id,
            contactName = blockedUser.name,
        )).also {
            userContactsRepo.save(it)
        }
    }

    suspend fun getUser(
        userId: String,
    ): User? = userRepo.findById(userId)?.toUser(objectMapper)

    suspend fun upsertUser(user: User, isNewUser: Boolean = false): User {
        return userRepo.save(
            user.toUserModel(
                objectMapper = objectMapper,
                isNewUser = isNewUser
            )
        ).also {
            contactsRepo.findByPhoneNumberAndCountryCode(
                phone = it.phoneNumber,
                countryCode = it.countryCode,
            ).toList().ifEmpty {
                contactsRepo.save(
                    ContactModel(
                        countryCode = it.countryCode,
                        phoneNumber = it.phoneNumber,
                    )
                )
            }
        }.toUser(objectMapper)
    }

    suspend fun deleteUser(
        userId: String,
    ) {
//        userRepo.softDeleteById(userId)
        userRepo.deleteById(userId) // TODO: Temporary use this for development. Switch back to softDelete later
    }

    suspend fun upsertUserContacts(
        userId: String,
        userContacts: List<UserContact>,
    ): List<String> {
        val user = userRepo.findById(userId)!!
        // Get existing user contacts
        val existingUserContacts = userContactsRepo.findAllByUserId(userId).toList()
        // Get exising user contact details
        val contactPhones = userContacts.mapNotNull { it.phone?.let { parsePhoneNumber(it, user.countryCode.toInt()) } }
        val existingContacts = contactsRepo.findAllByPhoneNumberIn(
            phones = contactPhones.map { it.nationalNumber.toString() },
        ).toList()
        // New contacts to be saved
        val newContacts = mutableMapOf<Int, ContactModel>()
        val newUserContacts = mutableMapOf<Int, UserContactModel>()
        // Existing contacts to be updated
        val updateUserContacts = mutableListOf<UserContactModel>()
        userContacts.forEachIndexed { index, userContact ->
            // parse phone number
            val phoneNumber = parsePhoneNumber(userContact.phone.orEmpty(), user.countryCode.toInt())
                ?: return@forEachIndexed
            // only check contact which has valid phone number
            // find an existing contact which has the same number
            val existingContact = existingContacts.find {
                (it.phoneNumber != null && it.phoneNumber == phoneNumber.nationalNumber.toString())
            }
            if (existingContact == null) {
                // Contact does not exist -> will be added
                newContacts[index] = ContactModel(
                    id = 0,
                    countryCode = phoneNumber.countryCode.toString(),
                    phoneNumber = phoneNumber.nationalNumber.toString(),
                )
                newUserContacts[index] = UserContactModel(
                    id = 0,
                    userId = userId,
                    contactId = 0,
                    contactName = userContact.name,
                )
            } else {
                // Contact exists -> check if contact name has been changed -> will be updated
                existingUserContacts.find { it.contactId == existingContact.id }?.let {
                    if (userContact.name.isNotBlank() && userContact.name != it.contactName) {
                        updateUserContacts.add(it.copy(contactName = userContact.name))
                    }
                } ?: run {
                    updateUserContacts.add(
                        UserContactModel(
                            id = 0,
                            userId = userId,
                            contactName = userContact.name,
                            contactId = existingContact.id,
                        )
                    )
                }
            }
        }
        if (newContacts.isNotEmpty()) {
            // Save new contacts
            // TODO find a way to save all
            newContacts.forEach { (index, contact) ->
                val savedContact = contactsRepo.save(contact)
                userContactsRepo.save(newUserContacts[index]!!.copy(contactId = savedContact.id))
            }
        }
        if (updateUserContacts.isNotEmpty()) {
            // Update existing contacts
            userContactsRepo.saveAll(updateUserContacts).toList()
        }
        return newUserContacts.map { it.value.userId }
    }

    suspend fun addFriendRequest(
        userId: String,
        friendId: String,
    ) {
        // Add friend to user contact
        addUserToContact(userId, friendId)
        // Add user to friend contact
        addUserToContact(friendId, userId)
    }

    private suspend fun addUserToContact(recipientId: String, personTobeAddedId: String) {
        val friend = userRepo.findById(personTobeAddedId) ?: throw UserNotFound
        val friendPn =
            parsePhoneNumber(friend.phoneNumber, friend.countryCode.toInt()) ?: throw Error.InvalidPhoneNumber
        var existingContact = contactsRepo.findByPhoneNumberAndCountryCode(
            friendPn.nationalNumber.toString(),
            friendPn.countryCode.toString()
        ).toList().firstOrNull()

        if (existingContact == null) {
            existingContact = contactsRepo.save(
                ContactModel(
                    id = 0,
                    countryCode = friendPn.countryCode.toString(),
                    phoneNumber = friendPn.nationalNumber.toString()
                )
            )
        }
        val existingUserContact = userContactsRepo.findByUserIdAndContactId(
            recipientId,
            existingContact.id
        ).toList().firstOrNull()
        if (existingUserContact == null) {
            userContactsRepo.save(
                UserContactModel(
                    id = 0,
                    userId = recipientId,
                    contactId = existingContact.id,
                    contactName = friend.name
                )
            )
        } else {
            userContactsRepo.save(
                existingUserContact.apply { contactName = friend.name }
            )
        }
    }

    suspend fun getUserDevices(
        userId: String,
    ): List<UserDevice> {
        return userDevicesRepo.findAllByUserId(userId).map { it.toUserDevice() }.toList()
    }

    suspend fun upsertUserDevice(
        userDevice: UserDevice,
    ) {
        if (userDevice.deviceToken.isEmpty() && userDevice.userId.isNotEmpty()) {
            userDevicesRepo.deleteByUserId(userDevice.userId)
        } else {
            val existingUserDevice =
                userDevicesRepo.findByUserIdAndDeviceType(userDevice.userId, userDevice.deviceType.name)
            if (existingUserDevice == null) {
                userDevicesRepo.save(userDevice.toUserDeviceModel())
            } else {
                userDevicesRepo.save(
                    existingUserDevice.copy(deviceToken = userDevice.deviceToken)
                )
            }
        }
    }
}
